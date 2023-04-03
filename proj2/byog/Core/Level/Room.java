package byog.Core.Level;

import byog.Core.Graphics.TETile;
import byog.Core.Graphics.Tileset;
import byog.RandomTools.RandomInclusive;

import java.io.Serializable;


public class Room implements Serializable {
    /* Instance variables */
    private final Position lowerLeft;
    private final Position upperRight;
    private final TETile floorType = Tileset.FLOOR;
    private final RandomInclusive rand;

    Room(Position lowerLeft, Position upperRight, RandomInclusive rand) {
        this.lowerLeft = lowerLeft;
        this.upperRight = upperRight;
        this.rand = rand;
    }

    /** From a position, draws an irregular room by making new positions at the top, right, bottom,
     * and left of said position, then applying the recursive method on those four new positions.
     * Depending on the location and the count, either a FLOOR or WALL tile is drawn. */
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
            // Todo: refactor onEdge into base case
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

    /** Same as above but with grass and flowers. */
    public void drawIrregularGrass(int count, int x, int y, Level level) {
        if (!level.isValid(x, y)) {
            return;
        }
        if (level.peek(x, y) == floorType && count > 0) {
            if (rand.nextInt(100) <= 10) {
                level.place(x, y, Tileset.randomFlower(rand));
            } else {
                level.place(x, y, Tileset.colorVariantGrass(rand));
            }
            int up = count - rand.nextInt(1, 2);
            int down = count - rand.nextInt(1, 2);
            int left = count - rand.nextInt(1, 2);
            int right = count - rand.nextInt(1, 2);

            drawIrregularGrass(up, x, y + 1, level);
            drawIrregularGrass(down, x, y - 1, level);
            drawIrregularGrass(left, x - 1, y, level);
            drawIrregularGrass(right, x + 1, y, level);
        }
    }

    /* Draws the rectangular room that is associated with this particular Partition onto level. */
    public void drawRoom(Level level) {
        int startX = lowerLeft.x;
        int startY = lowerLeft.y;
        int endX = upperRight.x;
        int endY = upperRight.y;

        // Draw top and bottom walls
        for (int x = startX; x <= endX; x++) {
            if (level.peek(x, startY) == Tileset.NOTHING) {
                level.place(x, startY, Tileset.colorVariantWall(rand));
            }
            if (level.peek(x, endY) == Tileset.NOTHING) {
                level.place(x, endY, Tileset.colorVariantWall(rand));
            }
        }
        // Draw left and right walls
        for (int y = startY; y <= endY; y++) {
            if (level.peek(startX, y) == Tileset.NOTHING) {
                level.place(startX, y, Tileset.colorVariantWall(rand));
            }
            if (level.peek(endX, y) == Tileset.NOTHING) {
                level.place(endX, y, Tileset.colorVariantWall(rand));
            }
        }
        // Draw interior
        for (int x = startX + 1; x <= endX - 1; x++) {
            for (int y = startY + 1; y <= endY - 1; y++) {
                level.place(x, y, floorType);
            }
        }
    }

    /** Pick random location within the room, int buffer indicating the margin from the room edge. */
    public Position randomPosition(int buffer) {
        int xLower = lowerLeft.x + buffer;
        int xUpper = upperRight.x - buffer;
        int yLower = lowerLeft.y + buffer;
        int yUpper = upperRight.y - buffer;

        return new Position(
                rand.nextInt(xLower, xUpper),
                rand.nextInt(yLower, yUpper));
    }
}
