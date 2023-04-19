package byog.Core.Level.Map;

import byog.Core.Graphics.Sprite;
import byog.Core.Position;
import byog.RandomTools.RandomInclusive;

import java.io.Serializable;
import java.util.*;
import java.util.List;

/**
 * Map object to represent the underlying data type (TETIle[][]) representing the world.
 */
public class Map implements Serializable {
    /* Private class to represent a room-distance pair in the pQueue. */
    private static class Node {
        Room room;
        int distance;

        Node(Room room, int distance) {
            this.room = room;
            this.distance = distance;
        }
    }

    /* Instance variables */
    protected final int width;
    protected final int height;
    protected final RandomInclusive rand;
    private final ArrayList<Room> rooms = new ArrayList<>();
    private final Tile[][] tileGrid;
    private Position start, exit;

    public Map(int width, int height, RandomInclusive rand) {
        this.width = width;
        this.height = height;
        this.tileGrid = new Tile[width][height];
        Partition partition = new Partition(new Position(0, 0), width, height, this);
        this.rand = rand;

        /* Fill Sprite[][] data structure with blank tiles */
        for (int x = 0; x < tileGrid.length; x++) {
            for (int y = 0; y < tileGrid[0].length; y++) {
                place(new Tile(x, y, Sprite.NOTHING));
            }
        }
        /* Generate dungeon and draw grass */
        partition.generateTree(rooms);
        partition.connectPartitions();

        setPortals();
    }

    /** Returns the sprite at specified x and y coordinates on the level, but does not remove the sprite.
     * If out of bounds, returns null. */
    public Tile peek(int x, int y) {
        if (isValid(x, y)) {
            return tileGrid[x][y];
        }
        return null;
    }

    /** Draws the specified sprite at the specified x & y.
     * Use this method so you don't get IndexErrors. */
    public void place(Tile tile) {
        int ix = tile.getX(), iy = tile.getY();
        if (isValid(ix, iy)) {
            tileGrid[ix][iy] = tile;
        }
    }

    /** Returns true if x and y are within the dimensions of the Sprite[][] matrix. */
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
            if (isValid(p.ix() + pair[0], p.iy() + pair[1])) {
                adj.add(new Position(p.ix() + pair[0], p.iy() + pair[1]));
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
            if (isValid(pos.ix() + pair[0], pos.iy() + pair[1])) {
                adjacent.add(to1D(pos.ix() + pair[0], pos.iy() + pair[1]));
            }
        }
        return adjacent;
    }

    private void setPortals() {
        PriorityQueue<Node> pQueue = new PriorityQueue<>(getDistanceComparator());
        Position start = rooms.get(0).getCenter();
        this.start = start;
        place(new Tile(start.ix(), start.iy(), Sprite.LADDER));

        int len = rooms.size();
        for (int i = 1; i < len; i++) {
            int distance = (int) Position.euclidean(start, rooms.get(i).getCenter());
            pQueue.add(new Node(rooms.get(i), distance));
        }
        Node farthest = pQueue.remove();
        Position exit = farthest.room.getCenter();
        this.exit = exit;
        place(new Tile(exit.ix(), exit.iy(), Sprite.UNLOCKED_DOOR));
    }

    private static class DistanceComparator implements Comparator<Node> {
        public int compare(Node a, Node b) {
            return b.distance - a.distance;
        }
    }

    private static Comparator<Node> getDistanceComparator() {
        return new DistanceComparator();
    }
}