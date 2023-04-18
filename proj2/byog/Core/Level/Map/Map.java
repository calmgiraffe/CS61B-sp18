package byog.Core.Level.Map;

import byog.Core.Level.Tile;
import byog.Core.Visitable;
import byog.Core.Renderer;
import byog.Core.Visitor;
import byog.RandomTools.RandomInclusive;

import java.io.Serializable;
import java.util.*;
import java.util.List;

/**
 * Map object to represent the underlying data type (TETIle[][]) representing the world.
 */
public class Map implements Serializable, Visitable {
    /* Instance variables */
    protected final int width;
    protected final int height;
    protected final RandomInclusive rand;
    private final ArrayList<Room> rooms = new ArrayList<>();
    private final Tile[][] tilemap;
    private int angle = 0;
    private List<Visitable> visitables = new ArrayList<>();

    public Map(int width, int height, RandomInclusive rand) {
        this.width = width;
        this.height = height;
        this.tilemap = new Tile[width][height];
        Partition partition = new Partition(new Position(0, 0), width, height, this);
        this.rand = rand;

        /* Fill Tile[][] data structure with blank tiles */
        for (int x = 0; x < tilemap.length; x++) {
            for (int y = 0; y < tilemap[0].length; y++) {
                place(x, y, Tile.NOTHING);
            }
        }
        /* Generate dungeon and draw grass */
        partition.generateTree(rooms);
        partition.connectPartitions();

        /* Add renderable objects */
        for (int x = 0; x < tilemap.length; x++) {
            for (int y = 0; y < tilemap[0].length; y++) {
                visitables.add(peek(x, y));
            }
        }
    }

    /* Update the state of the level, this includes changing color of tiles */
    public void nextFrame() {
        angle = (angle + 5) % 360;
        // Todo: complete rest of method
        // Update how different tiles look
    }

    /** Returns the tile at specified x and y coordinates on the level, but does not remove the tile.
     * If out of bounds, returns null. */
    public Tile peek(int x, int y) {
        if (isValid(x, y)) {
            return tilemap[x][y];
        }
        return null;
    }

    /** Draws the specified tile at the specified x & y.
     * Use this method so you don't get IndexErrors. */
    public void place(int x, int y, Tile tile) {
        // Todo: keep a set of different tile types
        // See Flyweight design pattern
        if (isValid(x, y)) {
            tilemap[x][y] = tile;
        }
    }

    /** Returns true if x and y are within the dimensions of the Tile[][] matrix. */
    public boolean isValid(int x, int y) {
        return (0 <= x && x < width) && (0 <= y && y < height);
    }

    /** Given a xy coordinate on the level (range of 0 to width-1, 0 to height-1), converts this
     * to a corresponding 1D coordinate. This process can be imagined as lining up the rows of the
     * level into one long line. */
    public int to1D(int x, int y) {
        if (!isValid(x, y)) {
            throw new ArrayIndexOutOfBoundsException("Position out of bounds.");
        }
        return width * y + x;
    }

    /** Given a 1D pos1D on the level, converts this to a corresponding new Position. */
    public Position toPosition(int pos1D) {
        int x = pos1D % width;
        int y = pos1D / width;
        if (!isValid(x, y)) {
            throw new ArrayIndexOutOfBoundsException("X and Y out of bounds.");
        }
        return new Position(x, y);
    }

    /** Given a 1D position on a level, returns the adjacent (up, right, down, left) 1D positions */
    public List<Position> adjacent(Position p) {
        ArrayList<Position> adj = new ArrayList<>();
        int[][] arr = {{0, 1}, {0, -1}, {1, 0}, {-1, 0}};

        for (int[] pair : arr) {
            if (isValid(p.x + pair[0], p.y + pair[1])) {
                adj.add(new Position(p.x + pair[0], p.y + pair[1]));
            }
        }
        return adj;
    }

    /** Given a 1D position on a level, returns the adjacent (up, right, down, left) 1D positions */
    public List<Integer> adjacent(int p) {
        Position pos = toPosition(p);
        int[][] arr = {{0, 1}, {0, -1}, {1, 0}, {-1, 0}};

        ArrayList<Integer> adjacent = new ArrayList<>();
        for (int[] pair : arr) {
            if (isValid(pos.x + pair[0], pos.y + pair[1])) {
                adjacent.add(to1D(pos.x + pair[0], pos.y + pair[1]));
            }
        }
        return adjacent;
    }

    public Tile[][] getTilemap() {
        return tilemap;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    @Override
    public List<Visitable> getVisitables() {
        return visitables;
    }
}