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
     * Constructor
     */
    public Room(Position lowerLeft, Position upperRight) {
        this.lowerLeft = lowerLeft;
        this.upperRight = upperRight;
        this.centre = new Position((lowerLeft.x + upperRight.x) / 2, (lowerLeft.y + upperRight.y) / 2);
        this.floorType = chooseRandomFloorType();
    }

    /**
     * Given two rooms, draws a floor path between them. The method first selects a position that is
     * up, right, down, or left of a center, making sure that whichever position is chosen decreases
     * the distance between the two centers. When the method gets to the next center, the loop stops.
     */
    public static void drawPath(Room a, Room b) {
        Position cursor = new Position(a.centre.x, a.centre.y);
        while (!(cursor.x == b.centre.x && cursor.y == b.centre.y)) {
            //pick one of four directions
        }

    }

    /**
     * Draws the room of that is associated with this particular Partition onto the map.
     */
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
     * Randomly returns either the FLOOR or GRASS Tileset.
     */
    private TETile chooseRandomFloorType() {
        int choice = Game.random.nextIntInclusive(1);
        if (choice == 0) {
            return Tileset.FLOOR;
        } else {
            return Tileset.GRASS;
        }
    }
}
