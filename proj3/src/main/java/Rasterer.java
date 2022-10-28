import java.util.HashMap;
import java.util.Map;

/**
 * This class provides all code necessary to take a query box and produce
 * a query result. The getMapRaster method must return a Map containing all
 * seven of the required fields, otherwise the front end code will probably
 * not draw the output correctly.
 */
public class Rasterer {
    double ftPerDeg;
    double lowestRes;
    double latDiff;
    double lonDiff;
    // mapping depths to double[] representing lats and longs of partitions
    double[][] latCache;
    double[][] lonCache;

    public Rasterer() {
        this.ftPerDeg = 288200.0;
        this.lowestRes = (MapServer.ROOT_LRLON - MapServer.ROOT_ULLON) * ftPerDeg
                / MapServer.TILE_SIZE;
        this.latDiff = MapServer.ROOT_LRLAT - MapServer.ROOT_ULLAT;
        this.lonDiff = MapServer.ROOT_LRLON - MapServer.ROOT_ULLON;
        this.latCache = new double[MapServer.NUM_DEPTHS][];
        this.lonCache = new double[MapServer.NUM_DEPTHS][];

        // Fill the cache with proper values for each depth level (0 to 7)
        for (int d = 0; d <= 7; d += 1) {

            // partitions = 2^depth. For example, at depth 7, 2^7 = 128 partitions along lat or lon
            int numDivisions = (int) Math.pow(2, d);
            double[] latPartitions = new double[numDivisions];
            double[] lonPartitions = new double[numDivisions];

            // Fill the double[] array for the particular depth level, then add key-value to cache
            for (int i = 0; i < numDivisions; i += 1) {
                latPartitions[i] = (MapServer.ROOT_ULLAT + latDiff * i / numDivisions) * -1;
                lonPartitions[i] = MapServer.ROOT_ULLON + lonDiff * i / numDivisions;
            }
            latCache[d] = latPartitions;
            lonCache[d] = lonPartitions;
        }
    }

    /**
     * Takes a user query and finds the grid of images that best matches the query. These
     * images will be combined into one big image (rastered) by the front end. <br>
     *
     *     The grid of images must obey the following properties, where image in the
     *     grid is referred to as a "tile".
     *     <ul>
     *         <li>The tiles collected must cover the most longitudinal distance per pixel
     *         (LonDPP) possible, while still covering less than or equal to the amount of
     *         longitudinal distance per pixel in the query box for the user viewport size. </li>
     *         <li>Contains all tiles that intersect the query bounding box that fulfill the
     *         above condition.</li>
     *         <li>The tiles must be arranged in-order to reconstruct the full image.</li>
     *     </ul>
     *
     * @param params Map of the HTTP GET request's query parameters - the query box and
     *               the user viewport width and height.
     *
     * ex: {ullon=-122.241632, lrlon=-122.24053, w=892.0, h=875.0, ullat=37.87655, lrlat=37.87548}
     *
     * @return A map of results for the front end as specified: <br>
     * "render_grid"   : String[][], the files to display. <br>
     * "raster_ul_lon" : Number, the bounding upper left longitude of the rastered image. <br>
     * "raster_ul_lat" : Number, the bounding upper left latitude of the rastered image. <br>
     * "raster_lr_lon" : Number, the bounding lower right longitude of the rastered image. <br>
     * "raster_lr_lat" : Number, the bounding lower right latitude of the rastered image. <br>
     * "depth"         : Number, the depth of the nodes of the rastered image <br>
     * "query_success" : Boolean, whether the query was able to successfully complete; don't
     *                    forget to set this to true on success! <br>
     */
    public Map<String, Object> getMapRaster(Map<String, Double> params) {
        System.out.println(params); // for debugging
        Map<String, Object> results = new HashMap<>();

        if (!isValidParams(params)) {
            results.put("query_success", false);
            return results;
        }
        // Given params, determine required resolution
        double ULLon = params.get("ullon");
        double LRLon = params.get("lrlon");
        double ULLat = params.get("ullat");
        double LRLat = params.get("lrlat");
        double requiredRes = (LRLon - ULLon) * ftPerDeg / params.get("w");

        // Determine depth by continuously halving res until it is less than requiredRes
        int depth = 0;
        double res = lowestRes;
        for (int i = 0; i < 7; i += 1) {
            if (res <= requiredRes) {
                break;
            }
            res = res / 2;
            depth += 1;
        }
        double divisions = Math.pow(2, depth);

        /* Use binary search on cache to determine index of closest partition, given min/max x/y,
        which represent the lowest and highest x/y values to use when generating the filenames. */
        int minX = binarySearch(lonCache[depth], 0, lonCache[depth].length, ULLon);
        int maxX = binarySearch(lonCache[depth], 0, lonCache[depth].length, LRLon);
        int minY = binarySearch(latCache[depth], 0, latCache[depth].length, -ULLat);
        int maxY = binarySearch(latCache[depth], 0, latCache[depth].length, -LRLat);

        results.put("render_grid", generateRenderGrid(minX, maxX, minY, maxY, depth));
        results.put("raster_ul_lon", MapServer.ROOT_ULLON + minX * lonDiff / divisions);
        results.put("raster_ul_lat", MapServer.ROOT_ULLAT + minY * latDiff / divisions);
        results.put("raster_lr_lon", MapServer.ROOT_ULLON + (maxX + 1) * lonDiff / divisions);
        results.put("raster_lr_lat", MapServer.ROOT_ULLAT + (maxY + 1) * latDiff / divisions);
        results.put("depth", depth);
        results.put("query_success", true);

        return results;
    }

    /**
     * Return false if query box is outside root longitude/latitudes or if invalid query.
     * @param params query parameters
     * @return true or false
     */
    private boolean isValidParams(Map<String, Double> params) {
        double upperLeftLon = params.get("ullon");
        double lowerRightLon = params.get("lrlon");
        double upperLeftLat = params.get("ullat");
        double lowerRightLat = params.get("lrlat");

        /* Immediately return false if invalid query.
        Invalid if upperRightLon is left of upperLeftLon, or lowerRightLat above upperLeftLat.
        Invalid if LRLon is to the left to ROOT_ULLON, or if ULLON is right of ROOT_LRLON.
        Invalid if LRLat is above ROOT_ULLAT, or if ULLat < ROOT_LRLAT */
        if (lowerRightLon < upperLeftLon || lowerRightLat > upperLeftLat) {
            return false;
        } else if (lowerRightLon < MapServer.ROOT_ULLON || upperLeftLon > MapServer.ROOT_LRLON) {
            return false;
        } else if (lowerRightLat > MapServer.ROOT_ULLAT || upperLeftLat < MapServer.ROOT_LRLAT) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * Given min/max x/y, which represent the lowest and highest x/y values to use when generating
     * the filenames, return the complete String[][] renderGrid object.
     */
    private String[][] generateRenderGrid(int minX, int maxX, int minY, int maxY, int depth) {
        int numRows = maxY - minY + 1;
        int numCols = maxX - minX + 1;
        String prefix = "d" + depth;
        String[][] render_grid = new String[numRows][numCols];
        for (int row = minY; row <= maxY; row += 1) {
            for (int col = minX; col <= maxX; col += 1) {
                render_grid[row - minY][col - minX] = prefix + "_x" + col + "_y" + row + ".png";
            }
        }
        return render_grid;
    }

    /**
     * Given a double[] representing the cache at a certain depth level,
     * determine the index of the closest partition. Start is the index of the left boundary of
     * binary search (inclusive), and end is the index of the right boundary (exclusive).
     * For example, start and end are initially 0 and 8 for an array of length 8.
     * When end - start = 1, the sub array currently looked at is of length 1.
     */
    public int binarySearch(double[] cache, int start, int end, double desired) {
        // Base case
        if (end - start == 1) {
            return start;
        }
        // Calculate mid, then one of three depending on comparison of desired and cache[mid]
        int mid = (start + end) / 2;
        if (desired == cache[mid]) {
            return mid;
        } else if (desired > cache[mid]) {
            return binarySearch(cache, mid, end, desired);
        } else {
            return binarySearch(cache, start, mid, desired);
        }
    }
}
