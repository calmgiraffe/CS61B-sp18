package byog.lab5;
import org.junit.Test;
import static org.junit.Assert.*;

import byog.TileEngine.TERenderer;
import byog.TileEngine.TETile;
import byog.TileEngine.Tileset;

import java.util.Random;

/**
 * Draws a world consisting of hexagonal regions. */
public class HexWorld {
    /*
     * World parameters are determined at startup from SIZE.
     * Adjusting SIZE consequently changes hexagon dimensions, which changes the world dimensions */
    private static final int SIZE = 5;

    private static final int WIDTH = 3*SIZE - 2;
    private static final int HEIGHT = 2*SIZE;
    private static final int WINDOW_WIDTH = 5*WIDTH - 4*(SIZE - 1);
    private static final int WINDOW_HEIGHT = 5*HEIGHT;

    private static final long SEED = 987654321;
    private static final Random RANDOM = new Random(SEED);

    /*
     * A simple private class to represent the x and y position of a hexagon. */
    private static class Position {
        private int x;
        private int y;
        private int firstY;

        Position(int x, int y) {
            this.x = x;
            this.y = y;
            this.firstY = y;
        }

        /* Returns the x coordinate of the Position */
        public int getX() {
            return x;
        }

        /* Returns the y coordinate of the Position */
        public int getY() {
            return y;
        }

        public void moveLeft() {
            x = x - (WIDTH - SIZE + 1);
            y = firstY - SIZE;
            firstY = y;
        }

        public void moveRight() {
            x = x + (WIDTH - SIZE + 1);
            y = firstY - SIZE;
            firstY = y;
        }

        public void moveUp() {
            y = y + HEIGHT;
        }
    }

    /*
     * Given the current row (y) and the size (s) of the hexagon, calculates the point within the rectangle
     * bounding the hexagon where the first tile of the row should be drawn. */
    private static int findStart(int y, int s) {
        if (y < s) {
            return s - y - 1;
        } else {
            return y - s;
        }
    }

    /*
     * Given the current row (y) and the size (s) of the hexagon, calculates the point within the rectangle
     * bounding the hexagon where the drawing of the tiles within the row should be terminated */
    private static int findEnd(int y, int s) {
        if (y < s) {
            return 2*s - 1 + y;
        } else {
            return 4*s - 2 - y;
        }
    }

    /*
     * Select a random tile from a set of given Tilesets */
    public static TETile chooseRandomTile() {
        int tileNum = RANDOM.nextInt(5);

        switch (tileNum) {
            case 0: return Tileset.TREE;
            case 1: return Tileset.WATER;
            case 2: return Tileset.FLOWER;
            case 3: return Tileset.SAND;
            case 4: return Tileset.MOUNTAIN;
            default: return Tileset.NOTHING;
        }
    }

    /*
     * Add a hexagon to the TERenderer world at Position p.
     * The lower left corner of the rectangle bounding the hexagon will be placed at p.
     * int s is the straight edge side length of the hexagon.
     * TETile t is the type of tile to fill the hexagon with. */
    public static void addHexagon(TETile[][] world, Position p, int s, TETile t) {

        for (int y = p.getY(); y < p.getY() + HEIGHT; y++) {
            int start = findStart(y - p.getY(), s);
            int end = findEnd(y - p.getY(), s);

            for (int x = start + p.getX(); x < end + p.getX(); x++) {
                world[x][y] = t;
            }
        }
    }

    /*
     * Calculates the 19 positions where the hexagons are to be drawn,
     * and for each position, places a hexagon with a random tile type. */
    public static void addHexagons(TETile[][] world, int s) {

        // Determine starting position based off size
        Position p1 = new Position(0, 2*s);
        Position p2 = new Position(WINDOW_WIDTH - WIDTH, 2*s);

        int stack = 3;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < stack; j++) {

                TETile tile1 = chooseRandomTile();
                TETile tile2 = chooseRandomTile();
                addHexagon(world, p1, s, tile1);
                addHexagon(world, p2, s, tile2);
                p1.moveUp();
                p2.moveUp();
            }

            p1.moveRight();
            p2.moveLeft();
            stack += 1;
        }
    }

    /*
     * Iterate through every tile in the world and replace null values with blanks.
     * Note: more efficient method would be drawing blanks at the same time that the hexagon is being drawn. */
    public static void fillBlanks(TETile[][] world) {

        for (int x = 0; x < WINDOW_WIDTH; x++) {
            for (int y = 0; y < WINDOW_HEIGHT; y++) {
                if (world[x][y] == null) {
                    world[x][y] = Tileset.NOTHING;
                }
            }
        }
    }


    public static void main(String[] args) {
        // Initialize the tile rendering engine with a window of size WINDOW_WIDTH x WINDOW_HEIGHT
        TERenderer ter = new TERenderer();
        ter.initialize(WINDOW_WIDTH, WINDOW_HEIGHT);

        // Initialize tiles
        TETile[][] world = new TETile[WINDOW_WIDTH][WINDOW_HEIGHT];

        // Determine the 19 positions where the hexagons are to be drawn.
        addHexagons(world, SIZE);

        // Fills in blanks with empty spaces
        fillBlanks(world);

        // draws the world to the screen
        ter.renderFrame(world);
    }
}
