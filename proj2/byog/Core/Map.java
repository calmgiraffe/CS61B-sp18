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
    protected static RandInclusive rand;
    private final TETile[][] map;
    private final TETile[][] fovmap;
    protected final int width;
    protected final int height;
    protected final int numTiles;
    private final Partition partition;
    protected final PlayerMover playerMover;

    /**
     * Map constructor
     */
    Map(int width, int height, long seed) {
        rand = new RandInclusive(seed);
        this.map = new TETile[width][height];
        this.fovmap = new TETile[width][height];
        this.width = width;
        this.height = height;
        this.numTiles = width * height;
        this.partition = new Partition(new Position(0, 0), width, height);
        this.playerMover = new PlayerMover(this);
        this.clear();
    }

    /**
     * Randomly generates a complete world
     */
    public void generateWorld() {
        // make binary tree of partitions and draw hallways, making a connected graph
        Partition.splitAndConnect(this.partition, this);

        // traverse partition tree and add leafs to rooms array
        ArrayList<Room> rooms = Partition.returnRooms(this.partition);

        for (Room r : rooms) {
            r.drawRoom(this);

            // 50% chance of drawing an irregular room
            if (Map.rand.nextIntInclusive(100) < 50) {
                int size = Map.rand.nextIntInclusive(5, 8);
                Position randPos = r.randomPosition(0);
                r.drawIrregular(size, randPos.x, randPos.y, this);
            }
            // 70% chance of drawing grass in the room
            if (Map.rand.nextIntInclusive(100) < 70) {
                int size = Map.rand.nextIntInclusive(5, 7);
                Position randPos = r.randomPosition(1);
                r.drawIrregularGrass(size, randPos.x, randPos.y, this);
            }
        }
        // Pick a room and place character in it
        int i = rand.nextIntInclusive(rooms.size() - 1);
        Position playerPos = rooms.get(i).randomPosition(1);
        playerMover.setPosition(playerPos);

        // Pick a room and place door in it
        i = rand.nextIntInclusive(rooms.size() - 1);
        Position doorPos = rooms.get(i).randomPosition(1);
        placeTile(map, doorPos.x, doorPos.y, Tileset.UNLOCKED_DOOR);
        placeTile(fovmap, doorPos.x, doorPos.y, Tileset.UNLOCKED_DOOR);

        // Update the player
        updatePlayer('~');
    }

    /**
     * Given one of "wasd", moves the player in that direction. Updates FOV if feature enabled.
     * @param direction any char
     */
    public void updatePlayer(char direction) {
        if ("wasd".indexOf(direction) != -1) {
            playerMover.movePlayer(direction);
        }
        if (Game.enableFOV) {
            fill(fovmap, Tileset.NOTHING);
            for (Position p : playerMover.getFOV()) {
                placeTile(fovmap, p.x, p.y, peek(p.x, p.y));
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

    public void clear() {
        fill(map, Tileset.NOTHING);
        fill(fovmap, Tileset.NOTHING);
        playerMover.clear();
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
    public boolean onEdge(int x, int y) {
        return x == 0 || x == width - 1 || y == 0 || y == height - 1;
    }

    /**
     * Given a 1D position on a map, returns the adjacent (up, right, down, left) 1D positions
     */
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

    /**
     * Returns the TETile[][] associated with this object that is to be rendered.
     */
    public TETile[][] getMap() {
        if (Game.enableFOV) {
            return fovmap;
        } else {
            return map;
        }
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
}
