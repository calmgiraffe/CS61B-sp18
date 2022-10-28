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

    // mapping depths to array of doubles representing latitudes and longitudes of partitions
    double[][] latCache;
    double[][] lonCache;

    public Rasterer() {
        this.ftPerDeg = 288200.0;
        this.lowestRes = Math.abs(MapServer.ROOT_ULLON - MapServer.ROOT_LRLON) * ftPerDeg
                / MapServer.TILE_SIZE;
        this.latDiff = MapServer.ROOT_ULLAT - MapServer.ROOT_LRLAT;
        this.lonDiff = MapServer.ROOT_LRLON - MapServer.ROOT_ULLON;
        this.latCache = new double[MapServer.NUM_DEPTHS][];
        this.lonCache = new double[MapServer.NUM_DEPTHS][];

        // Fill the cache with proper values for each depth level
        for (int d = 0; d <= 7; d += 1) {
            int numDivisions = (int) Math.pow(2, d);
            double[] latPartitions = new double[numDivisions];
            double[] lonPartitions = new double[numDivisions];

            // Fill the double[] array for the particular depth level, then add key-value to cache
            for (int i = 0; i < numDivisions; i += 1) {
                latPartitions[i] = MapServer.ROOT_LRLAT + latDiff * i / numDivisions;
                lonPartitions[i] = MapServer.ROOT_ULLON + lonDiff * i / numDivisions;
            }
            latCache[d] = latPartitions;
            lonCache[d] = lonPartitions;
        }
        System.out.println(1);
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

        // Given params, determine required depth
        double ULLon = params.get("ullon");
        double LRLon = params.get("lrlon");
        double width = params.get("w");
        double requiredRes = Math.abs(ULLon - LRLon) * ftPerDeg / width;

        // Continuously decrease res until res is less than requiredRes
        int depth = 0;
        double res = lowestRes;
        for (int i = 0; i <= 7; i += 1) {
            if (res <= requiredRes) {
                results.put("depth", depth);
                break;
            }
            res = res / 2;
            depth += 1;
        }

        // Range finding
        // From depth, determine the maximum value of the partition x and y coordinates
        // length = 2^depth
        // maxXY = length - 1

        // Determine which box UL and LR belong in

        return results;
    }

}
