package byog.Core;

import byog.TileEngine.TETile;
import byog.TileEngine.Tileset;

public class Room {

    private static final TETile wallType = Tileset.WALL;
    private static final TETile[][] map = Map.getMap();
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
     * Draws the three wall tiles and floor tile that must be placed when added a new floor tile to a hallway.
     * The overall method works by adding a 'room' of area 1 on a preexisting room.
     */
    private static void drawHallway(Position p, int way) {
        int x = p.x();
        int y = p.y();

        if (way == 0) { // up
            x -= 1;
            y += 1;
        } else if (way == 1) { // right
            x += 1;
            y -= 1;
        } else { // down or left
            x -= 1;
            y -= 1;
        }
        // Draws the three wall tiles that must be placed when added a new floor tile to a hallway.
        // The overall method works by adding a 'room' of area 1 on a preexisting room.
        for (int i = 0; i < 3; i++) {
            if (Map.peek(map, x + i, y) != Tileset.FLOOR && way % 2 == 0) { // 0 or 2
                Map.placeTile(map, x + i, y, Tileset.WALL);

            } else if (Map.peek(map, x, y + i) != Tileset.FLOOR && way % 2 == 1) { // 1 or 3
                Map.placeTile(map, x, y + i, Tileset.WALL);
            }
        }
        Map.placeTile(map, p, Tileset.FLOOR);
    }

    /**
     * Draws a FLOOR tile at cursor, then increments the cursor in a direction towards the target.
     */
    private static void moveCursor(Position cursor, Position target, int[] choices) {
        boolean notAligned = (choices[0] != choices[1]);

        while (!(cursor.equals(target))) {
            // Choose one of two directions to move in, corresponding to the index of choices
            int choice = choices[Game.random.nextIntInclusive(1)];

            // Example: if up (0), move cursor up one space.
            // Then, draw a floor at this space, and draw 3 wall tiles above this space.
            if (choice == 0) {
                cursor.moveUp();
            } else if (choice == 1) {
                cursor.moveRight();
            } else if (choice == 2) {
                cursor.moveDown();
            } else if (choice == 3) {
                cursor.moveLeft();
            }
            drawHallway(cursor, choice);

            if (notAligned && cursor.verticallyAligned(target)) {
                choices[1] = choices[0];
                notAligned = false;
            } else if (notAligned && cursor.horizontallyAligned(target)) {
                choices[0] = choices[1];
                notAligned = false;
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
        // Todo: change to A*
        Position start = Position.randomPositionWithinRadius(roomA.centre);
        Position goal = Position.randomPositionWithinRadius(roomB.centre);
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
            directions = new int[]{0, 1}; // choose between right or up

        } else if (start.x() < goal.x()) {
            directions = new int[]{2, 1}; // choose between right or down

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
            if (Map.peek(map, x, startY) == Tileset.NOTHING) {
                Map.placeTile(map, x, startY, Room.wallType);
            }
            if (Map.peek(map, x, endY) == Tileset.NOTHING) {
                Map.placeTile(map, x, endY, Room.wallType);
            }
        }
        // Draw left and right walls
        for (int y = startY; y <= endY; y++) {
            if (Map.peek(map, startX, y) == Tileset.NOTHING) {
                Map.placeTile(map, startX, y, Room.wallType);
            }
            if (Map.peek(map, endX, y) == Tileset.NOTHING) {
                Map.placeTile(map, endX, y, Room.wallType);
            }
        }
        // Draw interior
        for (int x = startX + 1; x <= endX - 1; x++) {
            for (int y = startY + 1; y <= endY - 1; y++) {
                Map.placeTile(map, x, y, floorType);
            }
        }
    }

    /**
     * From a position, recursively draws an irregular room by making new positions at the top, right, bottom,
     * and left of said position, then applying the recursive method on those four new positions.
     * Depending on the location and the count, either a FLOOR or WALL tile is drawn.
     */
    public void drawIrregular(int count, Position p) {
        // Base case: count is 0 and able to place a tile on NOTHING
        if (count <= 0) {
            if (Map.peek(map, p) == Tileset.NOTHING) {
                Map.placeTile(map, p, Tileset.WALL);
            }
        } else {
            if (p.onMapEdge(map)) {
                Map.placeTile(map, p, Tileset.WALL);
            } else {
                Map.placeTile(map, p, Tileset.FLOOR);
            }
            Position pUp = new Position(p.x(), p.y() + 1);
            Position pRight = new Position(p.x() + 1, p.y());
            Position pDown = new Position(p.x(), p.y() - 1);
            Position pLeft = new Position(p.x() - 1, p.y());

            int n0 = Game.random.nextIntInclusive(1, 3);
            int n1 = Game.random.nextIntInclusive(1, 3);
            int n2 = Game.random.nextIntInclusive(1, 3);
            int n3 = Game.random.nextIntInclusive(1, 3);

            drawIrregular(count - n0, pUp);
            drawIrregular(count - n1, pRight);
            drawIrregular(count - n2, pDown);
            drawIrregular(count - n3, pLeft);
        }
    }

    /**
     * Randomly returns either the FLOOR or GRASS Tileset.
     */
    private TETile chooseRandomFloorType() {
        int choice = Game.random.nextIntInclusive(1, 1);
        if (choice == 0) {
            return Tileset.GRASS;
        } else {
            return Tileset.FLOOR;
        }
    }

    /**
     * Returns the lowerLeft Position.
     */
    public Position lowerLeft() {
        return lowerLeft;
    }

    /**
     * Returns the upperRight Position.
     */
    public Position upperRight() {
        return upperRight;
    }
}
