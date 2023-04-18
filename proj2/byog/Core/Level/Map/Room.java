package byog.Core.Level.Map;

import byog.Core.Level.Tile;

import java.io.Serializable;

public class Room implements Serializable {
    public static final int GRASS_ODDS = 70;

    private final Position lowerLeft;
    private final Position upperRight;
    private final Tile floorType = Tile.FLOOR;
    private final Map map;

    protected Room(Position lowerLeft, Position upperRight, Map map) {
        this.lowerLeft = lowerLeft;
        this.upperRight = upperRight;
        this.map = map;
    }

    /** Recursively draw grass in the room */
    public void drawGrass(int count, int x, int y) {
        if (!map.isValid(x, y)) {
            return;
        }
        if (map.peek(x, y) == floorType && count > 0) {
            if (map.rand.nextInt(100) <= 10) {
                map.place(x, y, Tile.randomFlower(map.rand));
            } else {
                map.place(x, y, Tile.colorVariant(Tile.GRASS, 50, 50, 50, map.rand));
            }
            int up = count - map.rand.nextInt(1, 2);
            int down = count - map.rand.nextInt(1, 2);
            int left = count - map.rand.nextInt(1, 2);
            int right = count - map.rand.nextInt(1, 2);

            drawGrass(up, x, y + 1);
            drawGrass(down, x, y - 1);
            drawGrass(left, x - 1, y);
            drawGrass(right, x + 1, y);
        }
    }

    /* Draws the rectangular room that is associated with this particular Partition onto level. */
    public void drawRoom() {
        int startX = lowerLeft.x;
        int startY = lowerLeft.y;
        int endX = upperRight.x;
        int endY = upperRight.y;

        // Draw top and bottom walls
        for (int x = startX; x <= endX; x++) {
            if (map.peek(x, startY) == Tile.NOTHING) {
                map.place(x, startY, Tile.colorVariant(Tile.WALL, 30, 30, 30, map.rand));
            }
            if (map.peek(x, endY) == Tile.NOTHING) {
                map.place(x, endY, Tile.colorVariant(Tile.WALL, 30, 30, 30, map.rand));
            }
        }
        // Draw left and right walls
        for (int y = startY; y <= endY; y++) {
            if (map.peek(startX, y) == Tile.NOTHING) {
                map.place(startX, y, Tile.colorVariant(Tile.WALL, 30, 30, 30, map.rand));
            }
            if (map.peek(endX, y) == Tile.NOTHING) {
                map.place(endX, y, Tile.colorVariant(Tile.WALL, 30, 30, 30, map.rand));
            }
        }
        // Draw interior
        for (int x = startX + 1; x <= endX - 1; x++) {
            for (int y = startY + 1; y <= endY - 1; y++) {
                map.place(x, y, floorType);
            }
        }
        // Draw grass inside room
        if (map.rand.nextInt(100) < GRASS_ODDS) {
            int size = map.rand.nextInt(5, 7);
            Position randPos = this.randomPosition(1);
            this.drawGrass(size, randPos.x, randPos.y);
        }
    }

    /** Pick random location within the room, int buffer indicating the margin from the room edge. */
    public Position randomPosition(int buffer) {
        int xLower = lowerLeft.x + buffer;
        int xUpper = upperRight.x - buffer;
        int yLower = lowerLeft.y + buffer;
        int yUpper = upperRight.y - buffer;

        return new Position(
                map.rand.nextInt(xLower, xUpper),
                map.rand.nextInt(yLower, yUpper));
    }
}
