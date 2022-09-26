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
        this.centre = new Position((lowerLeft.x() + upperRight.x()) / 2, (lowerLeft.y() + upperRight.y()) / 2);
        this.floorType = chooseRandomFloorType();
    }

    /**
     * Draws a FLOOR tile at Position p on the map.
     */
    private static void drawFloor(Position p) {
        map[p.x()][p.y()] = Tileset.FLOOR;
    }

    /**
     * Draws a FLOOR tile at cursor, then increments the cursor in a direction towards the target.
     */
    private static void moveCursor(Position start, Position target, int[] choices) {
        boolean aligned = (choices[0] == choices[1]);

        while (!(start.equals(target))) {
            Room.drawFloor(start); // draw floor at cursor
            int index = Game.random.nextIntInclusive(1); // choose index 0 or 1

            if (choices[index] == 0) {
                start.moveUp();
            } else if (choices[index] == 1) {
                start.moveRight();
            } else if (choices[index] == 2) {
                start.moveDown();
            } else {
                start.moveLeft();
            }

            if (!aligned) {
                if (start.verticallyAligned(target)) {
                    choices[1] = choices[0];
                    aligned = true;
                } else if (start.horizontallyAligned(target)) {
                    choices[0] = choices[1];
                    aligned = true;
                }
            }
        }
    }

    /**
     * Given two rooms, draws a floor path between them. The method first examines the centres of the two rooms,
     * and depending on their orientation, passes in different values to int[] choices. The first element of this
     * array is up (0) or down (2), the second element is right (1) or left (3). If the two rooms are positioned so
     * that their centers have the same x or y coordinate, both elements of the array are set as the same number.
     */
    public static void drawPath(Room roomA, Room roomB) {
        Position start = roomA.centre;
        Position goal = roomB.centre;
        int[] directions;

        if (start.verticallyAligned(goal)) {
            if (start.y() < goal.y()) {
                directions = new int[]{0, 0}; // move up
            } else {
                directions = new int[]{2, 2}; // move down
            }
        } else if (start.horizontallyAligned(goal)) {
            if (start.x() < goal.x()) {
                directions = new int[]{1, 1}; // move right
            } else {
                directions = new int[]{3, 3}; // move left
            }
        } else if (start.x() < goal.x() && start.y() < goal.y()) {
            directions = new int[]{0, 1}; // choose randomly between right or up

        } else if (start.x() < goal.x()) {
            directions = new int[]{2, 1}; // choose randomly between right or down

        } else if (start.y() < goal.y()) {
            directions = new int[]{0, 3}; // choose between left and up

        } else {
            directions = new int[]{2, 3}; // choose between left and down
        }
        moveCursor(start, goal, directions);
    }

    /**
     * Draws the room of that is associated with this particular Partition onto the map.
     */
    public void drawRoom() {
        int startX = lowerLeft.x();
        int startY = lowerLeft.y();
        int endX = upperRight.x();
        int endY = upperRight.y();
        
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
        int choice = Game.random.nextIntInclusive(0);
        if (choice == 0) {
            return Tileset.GRASS;
        } else {
            return Tileset.FLOOR;
        }
    }
}
