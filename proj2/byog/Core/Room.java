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
     * Draws a FLOOR tile at Position p on the map.
     */
    private static void drawFloor(Position p) {
        map[p.x][p.y] = Tileset.FLOOR;
    }

    /**
     * Draws a FLOOR tile at cursor, then increments the cursor in a direction towards the target.
     */
    private static void moveCursor(Room a, Room b, int[] choices) {
        Position cursor = new Position(a.centre.x, a.centre.y);

        while (!(cursor.x == b.centre.x && cursor.y == b.centre.y)) {

            Room.drawFloor(cursor); // draw floor at cursor

            int index = Game.random.nextIntInclusive(1); // choose index 0 or 1

            if (choices[index] == 0) {
                cursor.moveUp();
            } else if (choices[index] == 1) {
                cursor.moveRight();
            } else if (choices[index] == 2) {
                cursor.moveDown();
            } else {
                cursor.moveLeft();
            }

            if (cursor.x == b.centre.x) {
                choices[1] = choices[0];
            } else if (cursor.y == b.centre.y) {
                choices[0] = choices[1];
            }
        }
    }

    /**
     * Given two rooms, draws a floor path between them. The method first selects a position that is
     * up, right, down, or left of a center, making sure that whichever position is chosen decreases
     * the distance between the two centers. When the method gets to the next center, the loop stops.
     */
    public static void drawPath(Room a, Room b) {

        if (a.centre.x == b.centre.x) {
            if (a.centre.y < b.centre.y) {
                moveCursor(a, b, new int[]{0, 0}); // move up
            } else {
                moveCursor(a, b, new int[]{2, 2}); // move down
            }
        } else if (a.centre.y == b.centre.y) {
            if (a.centre.x < b.centre.x) {
                moveCursor(a, b, new int[]{1, 1}); // move right
            } else {
                moveCursor(a, b, new int[]{3, 3}); // move left
            }
        } else if (a.centre.x < b.centre.x && a.centre.y < b.centre.y) {
            moveCursor(a, b, new int[]{0, 1}); // choose randomly between right or up

        } else if (a.centre.x < b.centre.x) {
            moveCursor(a, b, new int[]{2, 1}); // choose randomly between right or down

        } else if (a.centre.y < b.centre.y) {
            moveCursor(a, b, new int[]{0, 3}); // choose between left and up

        } else {
            moveCursor(a, b, new int[]{2, 3}); // choose between left and down
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
