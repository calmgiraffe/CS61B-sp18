package byog.Core;

import byog.TileEngine.TETile;
import byog.TileEngine.Tileset;

public class Room {

    private final Position lowerLeft;
    private final Position upperRight;
    private final Position centre;
    private final TETile floorType;

    /**
     * Constructor
     */
    public Room(Position lowerLeft, Position upperRight, RandomExtra r) {
        this.lowerLeft = lowerLeft;
        this.upperRight = upperRight;
        this.centre = new Position(
                (lowerLeft.x() + upperRight.x()) / 2,
                (lowerLeft.y() + upperRight.y()) / 2);
        this.floorType = chooseRandomFloorType(r);
    }

    /**
     * Draws the three wall tiles and floor tile that must be placed when added a new floor tile
     * to a hallway. The overall method works by adding a 'room' of area 1 on a preexisting room.
     */
    private static void drawHallway(Position p, int way, TETile[][] map, RandomExtra r) {
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
                Map.placeTile(map, x + i, y, Tileset.colorVariantWall(r));

            } else if (Map.peek(map, x, y + i) != Tileset.FLOOR && way % 2 == 1) { // 1 or 3
                Map.placeTile(map, x, y + i, Tileset.colorVariantWall(r));
            }
        }
        Map.placeTile(map, p, Tileset.FLOOR);
    }

    /**
     * The method examines the centres of the two rooms, and depending on their orientation,
     * passes in different values to int[] choices. The first element of this array is up (0)
     * or down (2), the second element is right (1) or left (3). If the two rooms are positioned so
     * that their centers have the same x or y coordinate, both elements of the array are set as
     * the same number.
     */
    private static int[] getDirections(Position start, Position goal) {
        if (start.verticallyAligned(goal)) {
            if (start.y() < goal.y()) {
                return new int[]{0, 0}; // move up
            } else {
                return new int[]{2, 2}; // move down
            }
        } else if (start.horizontallyAligned(goal)) {
            if (start.x() < goal.x()) {
                return new int[]{1, 1}; // move right
            } else {
                return new int[]{3, 3}; // move left
            }
        } else if (start.x() < goal.x() && start.y() < goal.y()) {
            return new int[]{0, 1}; // choose between right or up

        } else if (start.x() < goal.x()) {
            return new int[]{2, 1}; // choose between right or down

        } else if (start.y() < goal.y()) {
            return new int[]{0, 3}; // choose between left and up

        } else {
            return new int[]{2, 3}; // choose between left and down
        }
    }

    /**
     * Given two rooms, draws a path (one with hallways bounding floor path) between them on map.
     */
    public static void drawPath(Room roomA, Room roomB, RandomExtra r, TETile[][] map) {
        // Todo: change to A*
        Position start = Position.randomPositionWithinRadius(roomA.centre, r);
        Position goal = Position.randomPositionWithinRadius(roomB.centre, r);
        int[] choices = getDirections(start, goal);

        boolean notAligned = (choices[0] != choices[1]);
        while (!(start.equals(goal))) {
            // Choose one of two directions to move in, corresponding to the index of choices
            int choice = choices[r.nextIntInclusive(1)];

            // Example: if up (0), move cursor up one space.
            // Then, draw a floor at this space, and draw 3 wall tiles above this space.
            if (choice == 0) {
                start.moveUp();
            } else if (choice == 1) {
                start.moveRight();
            } else if (choice == 2) {
                start.moveDown();
            } else if (choice == 3) {
                start.moveLeft();
            }
            drawHallway(start, choice, map, r);

            // If inline with goal, edit choices so subsequent tiles all go in same direct line
            if (notAligned && start.verticallyAligned(goal)) {
                choices[1] = choices[0];
                notAligned = false;
            } else if (notAligned && start.horizontallyAligned(goal)) {
                choices[0] = choices[1];
                notAligned = false;
            }
        }
    }

    /**
     * Draws the rectangular room that is associated with this particular Partition onto map.
     */
    public void drawRoom(TETile[][] map, RandomExtra r) {
        int startX = lowerLeft.x();
        int startY = lowerLeft.y();
        int endX = upperRight.x();
        int endY = upperRight.y();

        // Draw top and bottom walls
        for (int x = startX; x <= endX; x++) {
            if (Map.peek(map, x, startY) == Tileset.NOTHING) {
                Map.placeTile(map, x, startY, Tileset.colorVariantWall(r));
            }
            if (Map.peek(map, x, endY) == Tileset.NOTHING) {
                Map.placeTile(map, x, endY, Tileset.colorVariantWall(r));
            }
        }
        // Draw left and right walls
        for (int y = startY; y <= endY; y++) {
            if (Map.peek(map, startX, y) == Tileset.NOTHING) {
                Map.placeTile(map, startX, y, Tileset.colorVariantWall(r));
            }
            if (Map.peek(map, endX, y) == Tileset.NOTHING) {
                Map.placeTile(map, endX, y, Tileset.colorVariantWall(r));
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
     * From a position, draws an irregular room by making new positions at the top, right, bottom,
     * and left of said position, then applying the recursive method on those four new positions.
     * Depending on the location and the count, either a FLOOR or WALL tile is drawn.
     */
    public void drawIrregular(int count, Position p, RandomExtra r, TETile[][] map) {
        // Base case: count is 0 and able to place a tile on NOTHING
        if (count <= 0) {
            if (Map.peek(map, p) == Tileset.NOTHING) {
                Map.placeTile(map, p, Tileset.colorVariantWall(r));
            }
        } else {
            if (p.onMapEdge(map)) {
                Map.placeTile(map, p, Tileset.colorVariantWall(r));
            } else {
                Map.placeTile(map, p, Tileset.FLOOR);
            }
            Position pUp = new Position(p.x(), p.y() + 1);
            Position pRight = new Position(p.x() + 1, p.y());
            Position pDown = new Position(p.x(), p.y() - 1);
            Position pLeft = new Position(p.x() - 1, p.y());

            int n0 = r.nextIntInclusive(1, 3);
            int n1 = r.nextIntInclusive(1, 3);
            int n2 = r.nextIntInclusive(1, 3);
            int n3 = r.nextIntInclusive(1, 3);

            drawIrregular(count - n0, pUp, r, map);
            drawIrregular(count - n1, pRight, r, map);
            drawIrregular(count - n2, pDown, r, map);
            drawIrregular(count - n3, pLeft, r, map);
        }
    }

    /**
     * Same as above but with grass and flowers.
     */
    public void drawIrregularGrass(int count, Position p, RandomExtra r, TETile[][] map) {
        if (Map.peek(map, p) == Tileset.FLOOR && count > 0) {

            if (r.nextIntInclusive(0, 100) <= 10) {
                Map.placeTile(map, p, Tileset.randomColorFlower(r));
            } else {
                Map.placeTile(map, p, Tileset.GRASS);
            }

            Position pUp = new Position(p.x(), p.y() + 1);
            Position pRight = new Position(p.x() + 1, p.y());
            Position pDown = new Position(p.x(), p.y() - 1);
            Position pLeft = new Position(p.x() - 1, p.y());

            int n0 = r.nextIntInclusive(1, 2);
            int n1 = r.nextIntInclusive(1, 2);
            int n2 = r.nextIntInclusive(1, 2);
            int n3 = r.nextIntInclusive(1, 2);

            drawIrregularGrass(count - n0, pUp, r, map);
            drawIrregularGrass(count - n1, pRight, r, map);
            drawIrregularGrass(count - n2, pDown, r, map);
            drawIrregularGrass(count - n3, pLeft, r, map);
        }
    }

    /**
     * Pick random location within the room.
     */
    public Position randomPositionInRoom(RandomExtra r, int buffer) {
        int xLower = lowerLeft.x() + buffer;
        int xUpper = upperRight.x() - buffer;
        int yLower = lowerLeft.y() + buffer;
        int yUpper = upperRight.y() - buffer;

        return new Position(
                r.nextIntInclusive(xLower, xUpper),
                r.nextIntInclusive(yLower, yUpper));
    }

    /**
     * Randomly returns either the FLOOR or GRASS Tileset.
     */
    private TETile chooseRandomFloorType(RandomExtra r) {
        int choice = r.nextIntInclusive(0, 0);
        if (choice == 0) {
            return Tileset.FLOOR;
        } else if (choice == 1) {
            return Tileset.GRASS;
        } else if (choice == 2) {
            return Tileset.FLOWER;
        } else if (choice == 3) {
            return Tileset.WATER;
        } else {
            return Tileset.NOTHING;
        }
    }
}
