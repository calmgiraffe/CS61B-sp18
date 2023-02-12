package byog.Core;

import byog.TileEngine.TETile;
import byog.TileEngine.Tileset;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * Map object to represent the underlying data type (TETIle[][]) representing the world,
 * and other variables/invariants like its current level, width, height, rooms, etc */
public class Map implements Serializable {
    /* Static variables */
    public static final int MAP = 0;
    public static final int FOVMAP = 1;
    private static final int IRREGULAR_ROOM_ODDS = 50;
    private static final int GRASS_ODDS = 70;

    /** Public variables */
    protected final int width;
    protected final int height;
    protected ArrayList<Room> rooms = new ArrayList<>();;
    protected int level = 1;
    /* Private variables. Encapsulate the underlying data type of these */
    private ArrayList<TETile[][]> maps = new ArrayList<>();
    private final TETile[][] map;
    private final TETile[][] fovmap;
    private final Partition partition;

    Map(int width, int height) {
        this.map = new TETile[width][height];
        this.fovmap = new TETile[width][height];
        this.width = width;
        this.height = height;
        this.partition = new Partition(new Position(0, 0), width, height);
    }

    /** Generates dungeon and draws irregular rooms, grass */
    public void generate() {
        /* Fill both TETile[][] data structure with blank tiles */
        for (int x = 0; x < map.length; x++) {
            for (int y = 0; y < map[0].length; y++) {
                map[x][y] = Tileset.NOTHING;
                fovmap[x][y] = Tileset.BLANK;
            }
        }
        /* Make binary tree of partitions and draws hallways and rooms, making a connected graph.
        * As a side effect, also populates this.rooms with Room objects */
        partition.generateTree();

        for (Room r : rooms) {
            // 50% chance of drawing an irregular room
            if (Game.rand.nextInt(100) < IRREGULAR_ROOM_ODDS) {
                int size = Game.rand.nextInt(5, 8);
                Position randPos = r.randomPosition(0);
                r.drawIrregular(size, randPos.x, randPos.y);
            }
            // 70% chance of drawing grass in the room
            if (Game.rand.nextInt(100) < GRASS_ODDS) {
                int size = Game.rand.nextInt(5, 7);
                Position randPos = r.randomPosition(1);
                r.drawIrregularGrass(size, randPos.x, randPos.y);
            }
        }
    }

    /** Returns the tile at specified x and y coordinates on the map, but does not remove the tile.
     * If out of bounds, returns null. */
    public TETile peek(int x, int y, int type) {
        if (isValid(x, y)) {
            switch (type) {
                case MAP: return map[x][y];
                case FOVMAP: return fovmap[x][y];
            }
        }
        return null;
    }

    /** Draws the specified tile at the specified x & y.
     * Use this method so you don't get IndexErrors. */
    public void place(int x, int y, TETile tile, int type) {
        if (isValid(x, y)) {
            switch (type) {
                case MAP: map[x][y] = tile;
                case FOVMAP: fovmap[x][y] = tile;
            }
        }
    }

    /** Returns true if x and y are within the dimensions of the TETile[][] matrix. */
    public boolean isValid(int x, int y) {
        return (0 <= x && x < width) && (0 <= y && y < height);
    }

    /** Given a xy coordinate on the map (range of 0 to width-1, 0 to height-1), converts this
     * to a corresponding 1D coordinate. This process can be imagined as lining up the rows of the
     * map into one long line. */
    public int to1D(Position p) {
        if (!isValid(p.x, p.y)) {
            throw new ArrayIndexOutOfBoundsException("Position out of bounds.");
        }
        return width * p.y + p.x;
    }

    /** Given a 1D position on the map, converts this to a corresponding new Position. */
    public Position toPosition(int position) {
        int x = position % width;
        int y = position / width;
        if (!isValid(x, y)) {
            throw new ArrayIndexOutOfBoundsException("X and Y out of bounds.");
        }
        return new Position(x, y);
    }

    /** Given a 1D position on a map, returns the adjacent (up, right, down, left) 1D positions */
    public ArrayList<Integer> adjacent(int p) {
        Position currPos = toPosition(p);
        int currX = currPos.x;
        int currY = currPos.y;

        ArrayList<Position> tmp = new ArrayList<>();
        tmp.add(new Position(currX, currY + 1));
        tmp.add(new Position(currX, currY - 1));
        tmp.add(new Position(currX + 1, currY));
        tmp.add(new Position(currX - 1, currY));

        ArrayList<Integer> adjacent = new ArrayList<>();
        for (Position pos : tmp) {
            if (isValid(pos.x, pos.y)) {
                adjacent.add(to1D(pos));
            }
        }
        return adjacent;
    }

    /** Returns the TETile[][] associated with this object that is to be rendered. */
    public TETile[][] getMap() {
        return (Game.enableFOV > 0) ? fovmap : map;
    }
}