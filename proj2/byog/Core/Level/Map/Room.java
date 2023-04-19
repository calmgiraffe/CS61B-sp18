package byog.Core.Level.Map;

import byog.Core.Graphics.Sprite;
import byog.Core.Position;

import java.io.Serializable;

public class Room implements Serializable {
    private static final int GRASS_ODDS = 70;
    private static final int FLOWER_ODDS = 10;

    private final Position lowerLeft;
    private final Position upperRight;
    private final Sprite floorType = Sprite.FLOOR;
    private final Map map;

    protected Room(Position lowerLeft, Position upperRight, Map map) {
        this.lowerLeft = lowerLeft;
        this.upperRight = upperRight;
        this.map = map;
    }

    /* Recursively draw grass in the room */
    private void drawGrass(int count, int x, int y) {
        if (!map.isValid(x, y)) {
            return;
        }
        if (map.peek(x, y).getSprite() == floorType && count > 0) {
            if (map.rand.nextInt(100) <= FLOWER_ODDS) {
                map.place(new Tile(x, y, Sprite.randomFlower(map.rand)));
            } else {
                map.place(new Tile(x, y, Sprite.colorVariant(Sprite.GRASS, 50, 50, 50, map.rand)));
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

    /** Draws the rectangular room that is associated with this particular Partition onto level. */
    protected void drawRoom() {
        int startX = lowerLeft.ix();
        int startY = lowerLeft.iy();
        int endX = upperRight.ix();
        int endY = upperRight.iy();

        // Draw top and bottom walls
        for (int x = startX; x <= endX; x++) {
            if (map.peek(x, startY).getSprite() == Sprite.NOTHING) {
                map.place(new Tile(x, startY, Sprite.colorVariant(Sprite.WALL, 30, 30, 30, map.rand)));
            }
            if (map.peek(x, endY).getSprite() == Sprite.NOTHING) {
                map.place(new Tile(x, endY, Sprite.colorVariant(Sprite.WALL, 30, 30, 30, map.rand)));
            }
        }
        // Draw left and right walls
        for (int y = startY; y <= endY; y++) {
            if (map.peek(startX, y).getSprite() == Sprite.NOTHING) {
                map.place(new Tile(startX, y, Sprite.colorVariant(Sprite.WALL, 30, 30, 30, map.rand)));
            }
            if (map.peek(endX, y).getSprite() == Sprite.NOTHING) {
                map.place(new Tile(endX, y, Sprite.colorVariant(Sprite.WALL, 30, 30, 30, map.rand)));
            }
        }
        // Draw interior
        for (int x = startX + 1; x <= endX - 1; x++) {
            for (int y = startY + 1; y <= endY - 1; y++) {
                map.place(new Tile(x, y, floorType));
            }
        }
        // Draw grass inside room
        if (map.rand.nextInt(100) < GRASS_ODDS) {
            int size = map.rand.nextInt(5, 7);
            Position randPos = this.randomPosition(1);
            this.drawGrass(size, randPos.ix(), randPos.iy());
        }
    }

    /** Pick random location within the room, int buffer indicating the margin from the room edge. */
    protected Position randomPosition(int buffer) {
        int xLower = lowerLeft.ix() + buffer;
        int xUpper = upperRight.ix() - buffer;
        int yLower = lowerLeft.iy() + buffer;
        int yUpper = upperRight.iy() - buffer;

        return new Position(
                map.rand.nextInt(xLower, xUpper),
                map.rand.nextInt(yLower, yUpper));
    }

    protected Position getCenter() {
        return new Position(
                (lowerLeft.ix() + upperRight.ix()) / 2,
                (lowerLeft.iy() + upperRight.iy()) / 2);
    }
}
