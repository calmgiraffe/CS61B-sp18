package byog.Core.Level;

import byog.Core.Graphics.Tile;
import java.io.Serializable;

public class Room implements Serializable {
    public static final int GRASS_ODDS = 70;

    private final Position lowerLeft;
    private final Position upperRight;
    private final Tile floorType = Tile.FLOOR;
    private final Level level;

    protected Room(Position lowerLeft, Position upperRight, Level level) {
        this.lowerLeft = lowerLeft;
        this.upperRight = upperRight;
        this.level = level;
    }

    /** Recursively draw grass in the room */
    public void drawGrass(int count, int x, int y) {
        if (!level.isValid(x, y)) {
            return;
        }
        if (level.peek(x, y) == floorType && count > 0) {
            if (level.rand.nextInt(100) <= 10) {
                level.place(x, y, Tile.randomFlower(level.rand));
            } else {
                level.place(x, y, Tile.colorVariant(Tile.GRASS, 50, 50, 50, level.rand));
            }
            int up = count - level.rand.nextInt(1, 2);
            int down = count - level.rand.nextInt(1, 2);
            int left = count - level.rand.nextInt(1, 2);
            int right = count - level.rand.nextInt(1, 2);

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
            if (level.peek(x, startY) == Tile.NOTHING) {
                level.place(x, startY, Tile.colorVariant(Tile.WALL, 30, 30, 30, level.rand));
            }
            if (level.peek(x, endY) == Tile.NOTHING) {
                level.place(x, endY, Tile.colorVariant(Tile.WALL, 30, 30, 30, level.rand));
            }
        }
        // Draw left and right walls
        for (int y = startY; y <= endY; y++) {
            if (level.peek(startX, y) == Tile.NOTHING) {
                level.place(startX, y, Tile.colorVariant(Tile.WALL, 30, 30, 30, level.rand));
            }
            if (level.peek(endX, y) == Tile.NOTHING) {
                level.place(endX, y, Tile.colorVariant(Tile.WALL, 30, 30, 30, level.rand));
            }
        }
        // Draw interior
        for (int x = startX + 1; x <= endX - 1; x++) {
            for (int y = startY + 1; y <= endY - 1; y++) {
                level.place(x, y, floorType);
            }
        }
        // Draw grass inside room
        if (level.rand.nextInt(100) < GRASS_ODDS) {
            int size = level.rand.nextInt(5, 7);
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
                level.rand.nextInt(xLower, xUpper),
                level.rand.nextInt(yLower, yUpper));
    }
}
