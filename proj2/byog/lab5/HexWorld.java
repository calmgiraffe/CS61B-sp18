package byog.lab5;
import org.junit.Test;
import static org.junit.Assert.*;

import byog.TileEngine.TERenderer;
import byog.TileEngine.TETile;
import byog.TileEngine.Tileset;

import java.util.Random;

/**
 * Draws a world consisting of hexagonal regions.
 */
public class HexWorld {
    private static final int WIDTH = 7;
    private static final int HEIGHT = 6;
    private static final long SEED = 287123;
    private static final Random RANDOM = new Random(SEED);

    /* A simple private class to represent the x and y position of a hexagon. */
    private static class Position {
        private final int x;
        private final int y;

        Position(int x, int y) {
            this.x = x;
            this.y = y;
        }

        /* Returns the x coordinate of the Position */
        public int getX() {
            return this.x;
        }

        /* Returns the y coordinate of the Position */
        public int getY() {
            return this.y;
        }
    }

    /* Given the row and size of a hexagon, calcuates the position of the first tile */
    private static int findStart(int y, int s) {
        if (y < s) {
            return s - y - 1;
        } else {
            return y - s;
        }
    }

    /* Given the row and size of a hexagon, calcuates the position of the final tile */
    private static int findEnd(int y, int s) {
        if (y < s) {
            return 2*s - 1 + y;
        } else {
            return 4*s - 2 - y;
        }
    }

    /* Select a random tile from a set of given Tilesets */
    public static TETile chooseRandomTile() {
        int tileNum = RANDOM.nextInt(4);

        switch (tileNum) {
            case 0: return Tileset.GRASS;
            case 1: return Tileset.WATER;
            case 2: return Tileset.FLOWER;
            case 3: return Tileset.SAND;
            default: return Tileset.NOTHING;
        }
    }

    /*
     * Add a hexagon to the TERenderer world at Position p.
     * The lower left corner of the hexagon will be placed at p.
     * int s is the straight edge side length of the hexagon.
     * TETile t indicate the type of tile to fill the hexagon with.
     */
    public static void addHexagon(TETile[][] world, Position p, int s, TETile t) {
        int height = 2*s;
        // width = 3*s - 2;

        for (int y = p.getY(); y < p.getY() + height; y++) {
            int start = findStart(y, s);
            int end = findEnd(y, s);

            for (int x = start; x < end; x++) {
                world[x][y] = t;
            }
        }
    }

    public static void fillBlanks(TETile[][] world) {
        for (int x = 0; x < WIDTH; x++) {
            for (int y = 0; y < HEIGHT; y++) {
                if (world[x][y] == null) {
                    world[x][y] = Tileset.NOTHING;
                }
            }
        }
    }

    public static void main(String args[]) {
        Position p = new Position(0, 0);

        // initialize the tile rendering engine with a window of size WIDTH x HEIGHT
        TERenderer ter = new TERenderer();
        ter.initialize(WIDTH, HEIGHT);

        // initialize tiles
        TETile[][] world = new TETile[WIDTH][HEIGHT];
        TETile tile = chooseRandomTile();
        addHexagon(world, p, 3, tile);

        // Fills in blanks with empty spaces
        fillBlanks(world);

        // draws the world to the screen
        ter.renderFrame(world);
    }
}
