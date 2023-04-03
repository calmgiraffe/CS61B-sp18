package byog.Core.Level;

import byog.Core.State.PlayState;
import byog.Core.Graphics.TETile;
import byog.Core.Graphics.Tileset;
import byog.RandomTools.RandomInclusive;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Level object to represent the underlying data type (TETIle[][]) representing the world,
 * and other variables/invariants like its current level, width, height, rooms, etc */
public class Level implements Serializable {
    private final int width;
    private final int height;
    private final ArrayList<Room> rooms = new ArrayList<>();
    private final TETile[][] tilemap;
    private final Partition partition;
    private final RandomInclusive rand;

    public Level(int width, int height, RandomInclusive rand) {
        this.width = width;
        this.height = height;
        this.tilemap = new TETile[width][height];
        this.partition = new Partition(new Position(0, 0), width, height, rand);
        this.rand = rand;
    }

    /** Generates dungeon and draws irregular rooms, grass */
    public void generate() {
        /* Fill both TETile[][] data structure with blank tiles */
        for (int x = 0; x < tilemap.length; x++) {
            for (int y = 0; y < tilemap[0].length; y++) {
                place(x, y, Tileset.NOTHING);
            }
        }
        /* Make binary tree of partitions and populate this.rooms with leaf rooms */
        partition.generateTree();
        connectPartitions(partition);


        // Todo: redo the method of creating rooms
        for (Room r : rooms) {
            r.drawRoom();
            // 50% chance of drawing an irregular room
            if (rand.nextInt(100) < PlayState.IRREGULAR_ODDS) {
                int size = rand.nextInt(5, 7);
                Position randPos = r.randomPosition(0);
                r.drawIrregular(size, randPos.x, randPos.y);
            }
            // 70% chance of drawing grass in the room
            if (rand.nextInt(100) < PlayState.GRASS_ODDS) {
                int size = rand.nextInt(5, 7);
                Position randPos = r.randomPosition(1);
                r.drawIrregularGrass(size, randPos.x, randPos.y);
            }
        }
    }

    /** Select two partitions, one from the left and right branch respectively,
     * as stored in the left and right lists, then draws a path between their centres,
     * thereby connecting them and ensuring a complete graph.
     */
    public void connectPartitions(Partition curr) {
        if (curr.left == null && curr.right == null) {
            return;
        }
        connectPartitions(curr.left);
        connectPartitions(curr.right);

        double leftDist = Double.MAX_VALUE, rightDist = Double.MAX_VALUE;
        Partition closestLeft = curr, closestRight = curr;
        double currDist;

        for (Partition p : curr.left.childPartitions) {
            currDist = Position.euclidean(p.centre, curr.centre);
            if (currDist < leftDist) {
                leftDist = currDist;
                closestLeft = p;
            }
        }
        for (Partition p : curr.right.childPartitions) {
            currDist = Position.euclidean(p.centre, curr.centre);
            if (currDist < rightDist) {
                rightDist = currDist;
                closestRight = p;
            }
        }
        // Draws a hallway between the two rooms of the two partitions
        closestLeft.room.astar(closestRight.room);
    }

    /** Returns the tile at specified x and y coordinates on the level, but does not remove the tile.
     * If out of bounds, returns null. */
    public TETile peek(int x, int y) {
        if (isValid(x, y)) {
            return tilemap[x][y];
        }
        return null;
    }

    /** Draws the specified tile at the specified x & y.
     * Use this method so you don't get IndexErrors. */
    public void place(int x, int y, TETile tile) {
        if (isValid(x, y)) {
            tilemap[x][y] = tile;
        }
    }

    /** Returns true if x and y are within the dimensions of the TETile[][] matrix. */
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

    /** Given a 1D position on the level, converts this to a corresponding new Position. */
    public Position toPosition(int position) {
        int x = position % width;
        int y = position / width;
        if (!isValid(x, y)) {
            throw new ArrayIndexOutOfBoundsException("X and Y out of bounds.");
        }
        return new Position(x, y);
    }

    /** Given a 1D position on a level, returns the adjacent (up, right, down, left) 1D positions */
    public ArrayList<Integer> adjacent(int p) {
        Position currPos = toPosition(p);

        ArrayList<Position> tmp = new ArrayList<>();
        tmp.add(new Position(currPos.x, currPos.y + 1));
        tmp.add(new Position(currPos.x, currPos.y - 1));
        tmp.add(new Position(currPos.x + 1, currPos.y));
        tmp.add(new Position(currPos.x - 1, currPos.y));

        ArrayList<Integer> adjacent = new ArrayList<>();
        for (Position pos : tmp) {
            if (isValid(pos.x, pos.y)) {
                adjacent.add(to1D(pos.x, pos.y));
            }
        }
        return adjacent;
    }

    /** Returns the TETile[][] associated with this object that is to be rendered. */
    public TETile[][] getTilemap() {
        return tilemap;
    }
}