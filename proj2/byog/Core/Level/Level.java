package byog.Core.Level;

import byog.Core.Graphics.Tile;
import byog.Core.Renderable;
import byog.RandomTools.RandomInclusive;

import java.io.Serializable;
import java.util.*;
import java.util.List;

/**
 * Level object to represent the underlying data type (TETIle[][]) representing the world,
 * and other variables/invariants like its current level, width, height, rooms, etc
 */
public class Level implements Serializable, Renderable {
    /* Instance variables */
    protected final int width;
    protected final int height;
    protected final RandomInclusive rand;
    private final ArrayList<Room> rooms = new ArrayList<>();
    private final Tile[][] tilemap;
    private Player player;
    private List<Tile> wallTiles = new ArrayList<>();
    private int angle = 0;

    public Level(int width, int height, RandomInclusive rand) {
        this.width = width;
        this.height = height;
        this.tilemap = new Tile[width][height];
        Partition partition = new Partition(new Position(0, 0), width, height, this);
        this.rand = rand;

        /* Fill Tile[][] data structure with blank tiles */
        // Todo: potentially move into separate method
        for (int x = 0; x < tilemap.length; x++) {
            for (int y = 0; y < tilemap[0].length; y++) {
                place(x, y, Tile.NOTHING);
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

    public void updateEntities(char cmd) { // Todo: possibly refactor
        if (player == null) {
            int i = rand.nextInt(0, rooms.size() - 1);
            Position playerStart = rooms.get(i).randomPosition(1);
            player = new Player(playerStart.x, playerStart.y, this);
        }
        player.move(cmd);
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
        if (isValid(x, y)) {
            tilemap[x][y] = tile;

            if (tile.character() == '#') {
                wallTiles.add(tile);
            }
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


    @Override
    public List<Renderable> getRenderableData() {
        return null;
    }

    @Override
    public void update() {

    }
}