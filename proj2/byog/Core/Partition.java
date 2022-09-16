package byog.Core;

import byog.TileEngine.TETile;
import byog.TileEngine.Tileset;

import java.util.Random;

/* Inner class to represent an imaginary rectangular partition of the map.
 * Position p is the coordinate of the lower left corner */
public class Partition {

    /* Partition class variables */
    static final int MIN = 4;
    static final int MAX = 12;

    /* Partition instance variables */
    private final Position p;
    private int width;
    private int height;

    /* Partition constructor */
    Partition(Position p, int width, int height) {
        this.p = p;
        this.width = width;
        this.height = height;
    }

    /* Makes another partition about a point about border,
     * which is approximately in the middle of current partition's width.
     * and adds to list. Then, updates the width of the current partition. */
    public Partition splitHorizontally(int border) {
        Position newPos = new Position(p.X() + border, p.Y());
        Partition newPartition = new Partition(newPos, width - border, height);

        // Update the existing partition and return new Partition
        this.updateWidth(border);
        return newPartition;
    }

    /* Same as above but about the current partition's height */
    public Partition splitVertically(int border) {
        Position newPos = new Position(p.X(), p.Y() + border);
        Partition newPartition = new Partition(newPos, width, height - border);

        // Update the existing partition and return new Partition
        this.updateHeight(border);
        return newPartition;
    }

    /* Draws a room inside the partition whose area is between 4x4 and the exact dimensions of the partition area.
    *  Because a room */
    public void generateRoom2(TETile[][] map) {
        int widthRange = width - 4;
        int heightRange = height - 4;
    }

    /* Draws a rectangular room of given width and height, and places its bottom corner at position.
     * Tileset.WALL is used as the wall tile, and the room is filled with Tileset.FLOOR */
    public void generateRoom(int width, int height, Position p, TETile[][] map) {
        int startX = p.X();
        int startY = p.Y();
        int endX = startX + width - 1;
        int endY = startY + height - 1;

        // Draw top and bottom walls
        for (int x = startX; x <= endX; x++) {
            map[x][startY] = Tileset.WALL;
            map[x][endY] = Tileset.WALL;
        }
        // Draw left and right walls
        for (int y = startY; y <= endY; y++) {
            map[startX][y] = Tileset.WALL;
            map[endX][y] = Tileset.WALL;
        }
        // Draw interior
        for (int x = startX + 1; x <= endX - 1; x++) {
            for (int y = startY + 1; y <= endY - 1; y++) {
                map[x][y] = Tileset.FLOOR;
            }
        }
    }

    /* Draws a rectangular room of width and height that fits exactly within the current partition.
     * Tileset.WALL is used as the wall tile, and the room is filled with Tileset.FLOOR */
    public void generateRoom(TETile[][] map) {
        int startX = p.X();
        int startY = p.Y();
        int endX = startX + width - 1;
        int endY = startY + height - 1;

        // Draw top and bottom walls
        for (int x = startX; x <= endX; x++) {
            map[x][startY] = Tileset.WALL;
            map[x][endY] = Tileset.WALL;
        }
        // Draw left and right walls
        for (int y = startY; y <= endY; y++) {
            map[startX][y] = Tileset.WALL;
            map[endX][y] = Tileset.WALL;
        }
        // Draw interior
        for (int x = startX + 1; x <= endX - 1; x++) {
            for (int y = startY + 1; y <= endY - 1; y++) {
                map[x][y] = Tileset.FLOOR;
            }
        }
    }

    /* Pass in random object */
    public Partition split(Random r) {
        // if MIN <= width <= MAX and height > MAX, split vertically
        if (width >= MIN && width <= MAX && height > MAX) {
            int border = r.nextInt(height - (2*MIN - 1)) + MIN;
            return this.splitVertically(border);
        }
        // if MIN <= height <= MAX and width > MAX, split horizontally
        else if (height >= MIN && height <= MAX && width > MAX) {
            int border = r.nextInt(width - (2*MIN - 1)) + MIN;
            return this.splitHorizontally(border);
        }
        // if width and height between MIN and MAX, do nothing
        else if (height >= MIN && height <= MAX && width >= MIN && width <= MAX) {
            return null;
        }
        // Do either
        else {
            int choice = r.nextInt(2);
            if (choice == 0) {
                int border = r.nextInt(width - 7) + 4;
                return this.splitHorizontally(border);

            } else {
                int border = r.nextInt(height - 7) + 4;
                return this.splitVertically(border);
            }
        }
    }

    public void updateWidth(int newWidth) {
        width = newWidth;
    }

    public void updateHeight(int newHeight) {
        height = newHeight;
    }

    public int width() {
        return width;
    }

    public int height() {
        return height;
    }
}