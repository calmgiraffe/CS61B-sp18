package byog.Core;

import byog.TileEngine.TETile;
import byog.TileEngine.Tileset;
import java.util.Random;

public class MapGenerator {
    private final int width;
    private final int height;
    private final TETile[][] map;
    private final Random randGen;

    /* Constructor for MapGenerator object that takes in map dimensions and seed? */
    public MapGenerator(int width, int height, String seed) {
        this.width = width;
        this.height = height;
        this.map = new TETile[width][height]; // initialize map
        this.randGen = new Random(Long.parseLong(seed)); // initialize random generator
        fillWithNothing();
    }


    private void fillWithNothing() {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                map[x][y] = Tileset.NOTHING;
            }
        }
    }

    /* Returns the 'map' object */
    public TETile[][] returnMap() {
        return map;
    }

    /* Draws a rectangular room of width and height, and places its bottom corner at position.
    *  Tileset.WALL is used as the wall tile, and the room is filled with Tileset.FLOOR */
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
}
