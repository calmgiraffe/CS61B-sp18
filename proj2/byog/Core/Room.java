package byog.Core;

import byog.TileEngine.TETile;
import byog.TileEngine.Tileset;

public class Room {

    private static final TETile wallType = Tileset.WALL;
    private static final TETile[][] map = Map.map;
    private final Position lowerLeft;
    private final Position upperRight;
    private final Position centre;
    private final TETile floorType;

    /**
     * Constructor */
    public Room(Position lowerLeft, Position upperRight) {
        this.lowerLeft = lowerLeft;
        this.upperRight = upperRight;
        this.centre = new Position((lowerLeft.x + upperRight.x) / 2, (lowerLeft.y + upperRight.y) / 2);
        this.floorType = chooseRandomFloorType();
    }

    /**
     * Draws the room of that is associated with this particular Partition onto the map. */
    public void drawRoom() {
        int startX = lowerLeft.x;
        int startY = lowerLeft.y;
        int endX = upperRight.x;
        int endY = upperRight.y;

        // Draw top and bottom walls
        for (int x = startX; x <= endX; x++) {
            map[x][startY] = Room.wallType;
            map[x][endY] = Room.wallType;
        }
        // Draw left and right walls
        for (int y = startY; y <= endY; y++) {
            map[startX][y] = Room.wallType;
            map[endX][y] = Room.wallType;
        }
        // Draw interior
        for (int x = startX + 1; x <= endX - 1; x++) {
            for (int y = startY + 1; y <= endY - 1; y++) {
                map[x][y] = floorType;
            }
        }
    }

    /**
     * Randomly returns either the FLOOR or GRASS Tileset. */
    private TETile chooseRandomFloorType() {
        int choice = Game.random.nextIntInclusive(1);
        if (choice == 0) {
            return Tileset.FLOOR;
        } else {
            return Tileset.GRASS;
        }
    }
}
