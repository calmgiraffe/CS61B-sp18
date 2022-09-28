package byog.Core;

import byog.TileEngine.TETile;
import byog.TileEngine.Tileset;
import java.util.ArrayList;

/**
 * Map object to represent the underlying data type (TETIle[][]),
 * and other invariants like its width, height, numRooms, etc
 */
public class Map {

    /**
     * Map instance variables
     */
    private final RandomExtra random;
    private final TETile[][] map;
    private final int width;
    private final int height;
    private final Partition partition;
    private final ArrayList<Room> rooms = new ArrayList<>();

    /**
     * Map constructor
     */
    public Map(int width, int height, long seed) {
        this.random = new RandomExtra(seed);
        this.map = new TETile[width][height];
        this.width = width;
        this.height = height;
        this.partition = new Partition(new Position(0, 0), width, height);
        fillWithNothing();
    }

    /**
     * Fill the map with NOTHING Tileset.
     */
    private void fillWithNothing() {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                map[x][y] = Tileset.NOTHING;
            }
        }
    }

    /**
     * Randomly generates some rectangular rooms on the map.
     */
    public void generateWorld() {
        // make binary tree of partitions and draw hallways, making a connected graph
        Partition.splitAndConnect(partition, random, map);

        // traverse partition tree and add leafs to rooms array
        Partition.addRooms(rooms, partition);

        for (Room r : rooms) {
            r.drawRoom(map, random); // Todo: add ability to only draw some rooms

            if (random.nextIntInclusive(1, 100) < 50) {
                int size = random.nextIntInclusive(5, 8);
                r.drawIrregular(size, r.randomPositionInRoom(random, 0), random, map);
            }
            if (random.nextIntInclusive(1, 100) < 60) {
                int size = random.nextIntInclusive(5, 7);
                r.drawIrregularGrass(size, r.randomPositionInRoom(random, 1), random, map);
            }
        }
    }

    /**
     * Draws the specified tile at position p. If p out of range, prints a
     * message indicating the type of tile and location a placement was attempted.
     * Use this method so you don't get IndexErrors.
     *
     */
    public static void placeTile(TETile[][] map, Position p, TETile tile) {
        if ((0 <= p.x() && p.x() < map.length) && (0 <= p.y() && p.y() < map[0].length)) {
            map[p.x()][p.y()] = tile;
        }
    }

    /**
     * Draws the specified tile at the specified x & y. If x or y out of range, prints a message
     * indicating the type of tile and location a placement was attempted.
     * Use this method so you don't get IndexErrors.
     */
    public static void placeTile(TETile[][] map, int x, int y, TETile tile) {
        if ((0 <= x && x < map.length) && (0 <= y && y < map[0].length)) {
            map[x][y] = tile;
        }
        // System.out.println("Unable to place " + tile.toString() + " at (" + x + ", " + y + ")");
    }

    /**
     * Returns the tile at specified x and y coordinates on the map, but does not remove the tile.
     * If out of bounds, returns null.
     */
    public static TETile peek(TETile[][] map, int x, int y) {
        if ((0 <= x && x < map.length) && (0 <= y && y < map[0].length)) {
            return map[x][y];
        } else {
            return null;
        }
    }

    /**
     * Returns the tile at specified Position on the map, but does not remove the tile.
     * If out of bounds, returns null.
     */
    public static TETile peek(TETile[][] map, Position p) {
        if ((0 <= p.x() && p.x() < map.length) && (0 <= p.y() && p.y() < map[0].length)) {
            return map[p.x()][p.y()];
        } else {
            return null;
        }
    }

    /**
     * Returns the TETile[][] associated with this object.
     */
    public TETile[][] getMap() {
        return map;
    }
}
