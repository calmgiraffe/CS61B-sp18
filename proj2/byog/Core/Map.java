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
    private static TETile[][] map;
    private final int width;
    private final int height;
    private final Partition partition;
    private final ArrayList<Room> rooms = new ArrayList<>();

    /**
     * Map constructor
     */
    public Map(int width, int height) {
        map = new TETile[width][height];
        this.width = width;
        this.height = height;
        this.partition = new Partition(new Position(0, 0), width, height);
        this.fillWithNothing(); // Initially fill map with Tileset.NOTHING
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
    public void generateRooms() {
        Partition.splitAndConnect(partition); // make binary tree of partitions and draw hallways, making a connected graph
        Partition.addRooms(rooms, partition); // traverse partition tree and add leafs to rooms array

        for (Room r : rooms) {
            r.drawRoom();

            int choice = Game.random.nextIntInclusive(1, 100); // representing 100%
            if (choice < 50) {
                int xLower = r.lowerLeft().x();
                int xUpper = r.upperRight().x();
                int yLower = r.lowerLeft().y();
                int yUpper = r.upperRight().y();

                // Pick random location in the room and draw an irregular room there
                int x = Game.random.nextIntInclusive(xLower, xUpper);
                int y = Game.random.nextIntInclusive(yLower, yUpper);
                int size = Game.random.nextIntInclusive(5, 10);
                r.drawIrregular(size, new Position(x, y));
            }
        }
    }

    /**
     * Returns the TETile[][] object associated with this class.
     */
    public static TETile[][] getMap() {
        return map;
    }

    /**
     * Draws the specified tile at position p. If p out of range, prints a message indicating
     * the type of tile and location a placement was attempted. Use this method so you don't get IndexErrors.
     *
     */
    public static void placeTile(TETile[][] map, Position p, TETile tile) {
        if ((0 <= p.x() && p.x() < map.length) && (0 <= p.y() && p.y() < map[0].length)) {
            map[p.x()][p.y()] = tile;
        } else {
            System.out.println("Unable to place " + tile.toString() + " at (" + p.x() + ", " + p.y() + ")");
        }
    }

    /**
     * Draws the specified tile at the specified x & y. If x or y out of range, prints a message indicating
     * the type of tile and location a placement was attempted. Use this method so you don't get IndexErrors.
     */
    public static void placeTile(TETile[][] map, int x, int y, TETile tile) {
        if ((0 <= x && x < map.length) && (0 <= y && y < map[0].length)) {
            map[x][y] = tile;
        } else {
            System.out.println("Unable to place " + tile.toString() + " at (" + x + ", " + y + ")");
        }
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
            // throw new ArrayIndexOutOfBoundsException("Unable to get tile at (" + x + ", " + y + ")");
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
            // throw new ArrayIndexOutOfBoundsException("Unable to get tile at (" + p.x() + ", " + p.y() + ")");
        }
    }
}
