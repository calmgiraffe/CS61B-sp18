package byog.Core;

import byog.TileEngine.TETile;
import byog.TileEngine.Tileset;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Map object to represent the underlying data type (TETIle[][]),
 * and other invariants like its width, height, numRooms, etc
 */
public class Map implements Serializable {
    /* Static variables */
    private static final int IRREGULAR_ROOM_ODDS = 50;
    private static final int GRASS_ODDS = 70;

    /** Public variables */
    protected final int width;
    protected final int height;
    protected ArrayList<Room> rooms;
    protected int level = 1;
    /* Private variables */
    private final TETile[][] map;
    private final TETile[][] fovmap;
    private final Partition partition;

    /** Map constructor */
    Map(int width, int height) {
        this.map = new TETile[width][height];
        this.fovmap = new TETile[width][height];
        this.width = width;
        this.height = height;
        this.partition = new Partition(new Position(0, 0), width, height);
        generateWorld();
    }

    public void generateWorld() {
        /* Fill both TETile[][] data structure with blank tiles */
        for (int x = 0; x < map.length; x++) {
            for (int y = 0; y < map[0].length; y++) {
                map[x][y] = Tileset.NOTHING;
                fovmap[x][y] = Tileset.NOTHING;
            }
        }
        /* Make binary tree of partitions and draw hallways, making a connected graph */
        Partition.splitAndConnect(this.partition, this);

        /* Traverse partition tree and add leafs to rooms array */
        this.rooms = Partition.returnRooms(this.partition);

        for (Room r : rooms) {
            r.drawRoom(this);
            // 50% chance of drawing an irregular room
            if (Game.rand.nextIntInclusive(100) < IRREGULAR_ROOM_ODDS) {
                int size = Game.rand.nextIntInclusive(5, 8);
                Position randPos = r.randomPosition(0);
                r.drawIrregular(size, randPos.x, randPos.y, this);
            }
            // 70% chance of drawing grass in the room
            if (Game.rand.nextIntInclusive(100) < GRASS_ODDS) {
                int size = Game.rand.nextIntInclusive(5, 7);
                Position randPos = r.randomPosition(1);
                r.drawIrregularGrass(size, randPos.x, randPos.y, this);
            }
        }
    }

    /** Draws the specified tile at the specified x & y.
     * Use this method so you don't get IndexErrors. */
    public void placeTile(int x, int y, TETile tile) {
        if (isValid(x, y)) {
            map[x][y] = tile;
        }
    }

    public void updateFOVmap(List<Position> coordinates) {
        // Fill fovmap with blank
        for (int x = 0; x < map.length; x++) {
            for (int y = 0; y < map[0].length; y++) {
                fovmap[x][y] = Tileset.NOTHING;
            }
        }
        for (Position pos : coordinates) {
            fovmap[pos.x][pos.y] = map[pos.x][pos.y];
        }
    }

    /** Returns true if x and y are within the dimensions of the TETile[][] matrix. */
    public boolean isValid(int x, int y) {
        return (0 <= x && x < width) && (0 <= y && y < height);
    }

    /** Given a xy coordinate on the map (range of 0 to width-1, 0 to height-1), converts this
     * to a corresponding 1D coordinate. This process can be imagined as lining up the rows of the
     * map into one long line. */
    public int posToOneD(Position p) {
        if (!isValid(p.x, p.y)) {
            throw new ArrayIndexOutOfBoundsException("Position out of bounds.");
        }
        return width * p.y + p.x;
    }

    /** Given a 1D position on the map, converts this to a corresponding new Position. */
    public Position oneDToPos(int position) {
        int x = position % width;
        int y = position / width;
        if (!isValid(x, y)) {
            throw new ArrayIndexOutOfBoundsException("X and Y out of bounds.");
        }
        return new Position(x, y);
    }

    /** Given a 1D position on a map, returns the adjacent (up, right, down, left) 1D positions */
    public ArrayList<Integer> adjacent(int p) {
        Position currPos = oneDToPos(p);
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
                adjacent.add(posToOneD(pos));
            }
        }
        return adjacent;
    }

    /** Returns the TETile[][] associated with this object that is to be rendered. */
    public TETile[][] getMap() {
        if (Game.enableFOV) {
            return fovmap;
        }
        return map;
    }

    /** Returns the tile at specified x and y coordinates on the map, but does not remove the tile.
     * If out of bounds, returns null. */
    public TETile peek(int x, int y) {
        if (isValid(x, y)) {
            return map[x][y];
        }
        return null;
    }
}