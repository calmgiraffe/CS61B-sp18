package byog.Core.Level;

import byog.Core.Graphics.Tile;
import byog.Core.Graphics.Tileset;
import byog.Core.State.PlayState;

import java.io.Serializable;


public class Room implements Serializable {
    /* Instance variables */
    private final Position lowerLeft;
    private final Position upperRight;
    private final Tile floorType = Tileset.FLOOR;
    private final Level level;

    protected Room(Position lowerLeft, Position upperRight, Level level) {
        this.lowerLeft = lowerLeft;
        this.upperRight = upperRight;
        this.level = level;
    }

    /* From a position, draws an irregular room by making new positions at the top, right, bottom,
     * and left of said position, then applying the recursive method on those four new positions.
     * Depending on the location and the count, either a FLOOR or WALL tile is drawn. */
    /*
    public void drawIrregular(int count, int x, int y, Level level) {
        if (!level.isValid(x, y)) {
            return;
        }
        // Base case: count is <= 0 and able to place a tile on NOTHING
        if (count <= 0) {
            if (level.peek(x, y) == Tileset.NOTHING) {
                level.place(x, y, Tileset.colorVariantWall(rand));
            }
        } else {
            boolean onEdge = (x == 0) || (x == level.width - 1) || (y == 0) || (y == level.height - 1);
            if (onEdge) {
                level.place(x, y, Tileset.colorVariantWall(rand));
            } else {
                level.place(x, y, floorType);
            }
            drawIrregular(count - rand.nextInt(1, 3), x, y + 1, level);
            drawIrregular(count - rand.nextInt(1, 3), x, y - 1, level);
            drawIrregular(count - rand.nextInt(1, 3), x - 1, y, level);
            drawIrregular(count - rand.nextInt(1, 3), x + 1, y, level);
        }
    }
    */

    public void drawGrass(int count, int x, int y) {
        if (!level.isValid(x, y)) {
            return;
        }
        if (level.peek(x, y) == floorType && count > 0) {
            if (level.rand.nextInt(100) <= 10) {
                level.place(x, y, Tileset.randomFlower(level.rand));
            } else {
                level.place(x, y, Tileset.colorVariantGrass(level.rand));
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
            if (level.peek(x, startY) == Tileset.NOTHING) {
                level.place(x, startY, Tileset.colorVariantWall(level.rand));
            }
            if (level.peek(x, endY) == Tileset.NOTHING) {
                level.place(x, endY, Tileset.colorVariantWall(level.rand));
            }
        }
        // Draw left and right walls
        for (int y = startY; y <= endY; y++) {
            if (level.peek(startX, y) == Tileset.NOTHING) {
                level.place(startX, y, Tileset.colorVariantWall(level.rand));
            }
            if (level.peek(endX, y) == Tileset.NOTHING) {
                level.place(endX, y, Tileset.colorVariantWall(level.rand));
            }
        }
        // Draw interior
        for (int x = startX + 1; x <= endX - 1; x++) {
            for (int y = startY + 1; y <= endY - 1; y++) {
                level.place(x, y, floorType);
            }
        }
        // Draw grass inside room
        if (level.rand.nextInt(100) < PlayState.GRASS_ODDS) {
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
