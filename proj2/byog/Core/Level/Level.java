package byog.Core.Level;

import byog.Core.Graphics.TETile;
import byog.Core.Graphics.Tileset;
import byog.RandomTools.RandomInclusive;

import java.awt.*;
import java.io.Serializable;
import java.util.*;
import java.util.List;

import static byog.Core.Graphics.Colors.*;

/**
 * Level object to represent the underlying data type (TETIle[][]) representing the world,
 * and other variables/invariants like its current level, width, height, rooms, etc
 */
public class Level implements Serializable {
    protected final int width;
    protected final int height;
    protected final RandomInclusive rand;
    private final ArrayList<Room> rooms = new ArrayList<>();
    private final TETile[][] tilemap;
    private Player player;
    private List<TETile> wallTiles = new ArrayList<>();
    private int angle = 0;

    public Level(int width, int height, RandomInclusive rand) {
        this.width = width;
        this.height = height;
        this.tilemap = new TETile[width][height];
        Partition partition = new Partition(new Position(0, 0), width, height, this);
        this.rand = rand;

        /* Fill TETile[][] data structure with blank tiles */
        for (int x = 0; x < tilemap.length; x++) {
            for (int y = 0; y < tilemap[0].length; y++) {
                place(x, y, Tileset.NOTHING);
            }
        }
        /* Generates dungeon and draws grass */
        partition.generateTree(rooms);
        partition.connectPartitions();
        // Place entities
        this.updateEntities('~');
    }

    /* Update the state of the level, this includes changing color of tiles */
    public void nextFrame() {
        angle = (angle + 5) % 360;
        return;
    }

    public void updateEntities(char cmd) {
        if (player == null) {
            int i = rand.nextInt(0, rooms.size() - 1);
            Position playerStart = rooms.get(i).randomPosition(1);
            player = new Player(playerStart.x, playerStart.y, this);
        }
        player.move(cmd);
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

            if (tile.character() == '#') {
                wallTiles.add(tile);
            }
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