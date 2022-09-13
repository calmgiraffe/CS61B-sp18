package byog.Core;

import byog.TileEngine.*;
import java.util.Random;

public class MapGenerator {
    private final int width;
    private final int height;
    private TETile[][] map;
    private Random randGen;

    /* Constructor for MapGenerator object that takes in map dimensions and seed? */
    public MapGenerator(int width, int height, String seed) {
        this.width = width;
        this.height = height;
        this.map = new TETile[width][height]; // initialize map
        this.randGen = new Random(Long.parseLong(seed)); // initialize random generator
        fillWithNothing(this.map, width, height);
    }

    /* Room object to represent a room (rectangular space with walls) within a map */
    private class Room {
        int bottomLeftX;
        int bottomLeftY;
        int topRightX;
        int topRightY;
    }

    /* Fill the map with Tileset.NOTHING */
    private void fillWithNothing(TETile[][] map, int width, int height) {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                map[x][y] = Tileset.NOTHING;
            }
        }
    }

    public TETile[][] returnMap() {
        return map;
    }

    public void generateMap() {
        // divide map into recta

    }
}
