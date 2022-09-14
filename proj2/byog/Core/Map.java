package byog.Core;

import byog.TileEngine.TETile;
import byog.TileEngine.Tileset;

/** Map object to represent the underlying data type (TETIle[][]),
 *  and other invariants like its width, height, numRooms, etc */
public class Map {
    private TETile[][] map;
    private int width;
    private int height;
    private int numRooms;

    public Map(int width, int height) {
        this.map = new TETile[width][height];
        this.width = width;
        this.height = height;
    }

    /* Fill the map with Tileset.NOTHING */
    public void fillWithNothing() {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                map[x][y] = Tileset.NOTHING;
            }
        }
    }

    /* Returns the underlying TETile[][] object (its pointer) */
    public TETile[][] getMap() {
        return map;
    }

    /* Draws a rectangular room of width and height, and places its bottom corner at position.
     * Tileset.WALL is used as the wall tile, and the room is filled with Tileset.FLOOR */
    public void generateRoom(int width, int height, Position p) {
        int startX = p.X();
        int startY = p.Y();
        int endX = startX + width - 1;
        int endY = startY + height - 1;

        // Draw top and bottom walls
        for (int x = startX; x <= endX; x++) {
            map[x][startY] =  Tileset.WALL;
            map[x][endY] = Tileset.WALL;
        }
        // Draw left and right walls
        for (int y = startY; y <= endY; y++) {
            map[startX][y] = Tileset.WALL;
            map[endX][y] = Tileset.WALL;
        }
        // Draw interior
        for (int x = startX + 1; x <= endX - 1; x++) {
            for (int y = startY + 1; y <= endY - 1; y++) {
                map[x][y] = Tileset.FLOOR;
            }
        }
    }

    private void generateRoomsHelper(int divisions) {
        // Divide current map in half along random point within a certain range (not too far to one side)
        // For each divided part, divide yet again
        // Have a recursive depth of 3, use divisions variable to detect base case
        // If base case, draw a room within the divided section

    }

    public void generateRooms() {
        generateRoomsHelper(3);
    }
}
