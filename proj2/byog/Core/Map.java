package byog.Core;

import byog.TileEngine.TETile;
import byog.TileEngine.Tileset;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * Map object to represent the underlying data type (TETIle[][]),
 * and other invariants like its width, height, numRooms, etc
 */
public class Map implements Serializable {

    /**
     * Map instance variables
     */
    private final boolean enableFOV;
    protected static RandomExtra r;
    private final TETile[][] map;
    private final TETile[][] FOVmap;
    protected final int width;
    protected final int height;
    protected final int oneDlength;
    private final Partition partition;
    private final ArrayList<Room> rooms = new ArrayList<>();
    private final PlayerMover playerMover;

    /**
     * Map constructor
     */
    Map(int width, int height, long seed, boolean enableFOV) {
        this.enableFOV = enableFOV;
        r = new RandomExtra(seed);
        this.map = new TETile[width][height];
        this.FOVmap = new TETile[width][height];
        this.width = width;
        this.height = height;
        this.oneDlength = width * height;
        this.partition = new Partition(new Position(0, 0), width, height);
        this.playerMover = new PlayerMover(this);
        fill(map, Tileset.NOTHING);
    }

    /**
     * Randomly generates a complete world
     */
    public void generateWorld() {
        // make binary tree of partitions and draw hallways, making a connected graph
        Partition.splitAndConnect(this.partition, this);

        // traverse partition tree and add leafs to rooms array
        Partition.addRooms(rooms, this.partition);

        for (Room r : rooms) {
            r.drawRoom(this);

            if (Map.r.nextIntInclusive(1, 100) < 50) { // 50% chance
                int size = Map.r.nextIntInclusive(5, 8);
                r.drawIrregular(size, r.randomPosition(0), this);
            }
            if (Map.r.nextIntInclusive(1, 100) < 60) { // 60% chance
                int size = Map.r.nextIntInclusive(5, 7);
                r.drawIrregularGrass(size, r.randomPosition(1), this);
            }
        }
        // Pick a room and place character in center
        int i = r.nextIntInclusive(0, rooms.size() - 1);
        Position playerPos = rooms.get(i).randomPosition(1);
        playerMover.setPosition(playerPos);

        // Update the player
        updatePlayer('~');
    }

    /**
     * Given one of "wasd", moves the player in that direction. Updates FOV if feature enabled.
     * @param direction any char
     */
    public void updatePlayer(char direction) {
        if (direction == 'w' || direction == 'a' || direction == 's' || direction == 'd') {
            playerMover.movePlayer(direction);
        }
        if (enableFOV) {
            fill(FOVmap, Tileset.NOTHING);
            for (Position p : playerMover.getFOV()) {
                placeTile(FOVmap, p.x, p.y, peek(p));
            }
        }
    }

    /**
     * Fill the given TETile[][] with given TETile.
     */
    private static void fill(TETile[][] map, TETile tile) {
        for (int x = 0; x < map.length; x++) {
            for (int y = 0; y < map[0].length; y++) {
                map[x][y] = tile;
            }
        }
    }

    /**
     * Draws the specified tile at the specified x & y.
     * Use this method so you don't get IndexErrors.
     */
    public static void placeTile(TETile[][] map, int x, int y, TETile tile) {
        if ((0 <= x && x < map.length) && (0 <= y && y < map[0].length)) {
            map[x][y] = tile;
        }
    }

    /**
     * Draws the specified tile at the specified x & y.
     * Use this method so you don't get IndexErrors.
     */
    public void placeTile(int x, int y, TETile tile) {
        if (isValid(x, y)) {
            map[x][y] = tile;
        }
    }

    /**
     * Returns true if x and y are within the dimensions of the TETile[][] matrix.
     */
    public boolean isValid(int x, int y) {
        return (0 <= x && x < width) && (0 <= y && y < height);
    }

    /**
     * Given a xy coordinate on the map (range of 0 to width-1, 0 to height-1), converts this
     * to a corresponding 1D coordinate. This process can be imagined as lining up the rows of the
     * map into one long line.
     */
    public int posToOneD(Position p) {
        if (!isValid(p.x, p.y)) {
            throw new ArrayIndexOutOfBoundsException("Position out of bounds.");
        }
        return width * p.y + p.x;
    }

    /**
     * Given a 1D position on the map, converts this to a corresponding new Position.
     */
    public Position oneDToPos(int position) {
        int x = position % width;
        int y = position / width;
        if (!isValid(x, y)) {
            throw new ArrayIndexOutOfBoundsException("X and Y out of bounds.");
        }
        return new Position(x, y);
    }

    /**
     * Returns true if the given position is on the map edge, false otherwise.
     */
    public boolean onEdge(Position p) {
        return p.x == 0 || p.x == width - 1 || p.y == 0 || p.y == height - 1;
    }

    /**
     * Given a 1D position on a map, returns the adjacent (up, right, down, left) nodes
     */
    public ArrayList<Integer> adjacent(int p) {
        ArrayList<Position> tmp = adjacent(oneDToPos(p));
        ArrayList<Integer> adjacents = new ArrayList<>();

        for (Position pos : tmp) {
            adjacents.add(posToOneD(pos));
        }
        return adjacents;
    }

    /**
     * Given a Position on a map, returns the adjacent (up, right, down, left) nodes
     */
    public ArrayList<Position> adjacent(Position p) {
        Position pUp = new Position(p.x, p.y + 1);
        Position pRight = new Position(p.x + 1, p.y);
        Position pDown = new Position(p.x, p.y - 1);
        Position pLeft = new Position(p.x - 1, p.y);

        ArrayList<Position> tmp = new ArrayList<>();
        tmp.add(pUp);
        tmp.add(pRight);
        tmp.add(pDown);
        tmp.add(pLeft);

        ArrayList<Position> adjacent = new ArrayList<>();
        for (Position pos: tmp) {
            if (isValid(pos.x, pos.y)) {
                adjacent.add(pos);
            }
        }
        return adjacent;
    }

    /*
     * Getter methods
     */

    /**
     * Returns the TETile[][] associated with this object that is to be rendered.
     */
    public TETile[][] getMap() {
        if (enableFOV) {
            return FOVmap;
        }
        return map;
    }

    /**
     * Returns the tile at specified x and y coordinates on the map, but does not remove the tile.
     * If out of bounds, returns null.
     */
    public TETile peek(int x, int y) {
        if (isValid(x, y)) {
            return map[x][y];
        } else {
            return null;
        }
    }

    /**
     * Returns the tile at specified Position on the map, but does not remove the tile.
     * If out of bounds, returns null.
     */
    public TETile peek(Position p) {
        if (isValid(p.x, p.y)) {
            return map[p.x][p.y];
        } else {
            return null;
        }
    }
}
