package byog.Core;

import byog.TileEngine.TETile;
import byog.TileEngine.Tileset;

/**
 * Inner class to represent an imaginary rectangular partition of the map.
 * Position p is the coordinate of the lower left corner */
public class Partition {

    /* Partition class and instance variables */
    // All partitions side lengths should be between MIN and MAX.
    // MAX = 2*MIN - 1, because if MAX = 2*MIN, it can be split into 2 partitions of MIN
    static final int MIN = 8;
    static final int MAX = 2*MIN - 1;

    private final Position p;
    private int width;
    private int height;
    private final RandomExtra random;
    private final TETile[][] map;
    private Room room;

    /**
     * Partition constructor */
    Partition(Position p, int width, int height, RandomExtra random, TETile[][] map) {
        this.p = p;
        this.width = width;
        this.height = height;
        this.random = random;
        this.map = map;
    }

    /**
     * Makes another partition at a point about border, which is approximately in
     * the middle of the current partition's width so that both partitions are within bounds.
     * Then, updates the width of the current partition and currents the new partition. */
    public Partition splitHorizontally(int border) {
        Position newPos = new Position(p.X() + border, p.Y());
        Partition newPartition = new Partition(newPos, width - border, height, random, map);

        // Update the existing partition and return new Partition
        width = border;
        return newPartition;
    }

    /**
     * Makes another partition at a point about border, which is approximately in
     * the middle of the current partition's height so that both partitions are within bounds.
     * Then, updates the height of the current partition and currents the new partition. */
    public Partition splitVertically(int border) {
        Position newPos = new Position(p.X(), p.Y() + border);
        Partition newPartition = new Partition(newPos, width, height - border, random, map);

        // Update the existing partition and return new Partition
        height = border;
        return newPartition;
    }

    /**
     * Examines the dimensions of the partition, and either splits it horizontally or vertically, depending
     * on whether a dimension is greater than MAX. If both dimensions are greater than MAX, either vertical
     * or horizontal splitting is chosen randomly. */
    public Partition split() {
        if (widthWithinBounds() && !heightWithinBounds()) {
            int border = random.nextInt(MIN, height - MIN);
            return this.splitVertically(border);

        } else if (!widthWithinBounds() && heightWithinBounds()) {
            int border = random.nextInt(MIN, width - MIN);
            return this.splitHorizontally(border);

        } else if (widthWithinBounds() && heightWithinBounds()) {
            return null;

        } else {
            int choice = random.nextInt(2);
            if (choice == 0) {
                int border = random.nextInt(MIN, width - MIN);
                return this.splitHorizontally(border);

            } else {
                int border = random.nextInt(MIN, height - MIN);
                return this.splitVertically(border);
            }
        }
    }

    /**
     * Randomly returns either the FLOOR or GRASS Tileset. */
    private TETile chooseRandomFloorType() {
        int choice = random.nextInt(1);
        if (choice == 0) {
            return Tileset.FLOOR;
        } else {
            return Tileset.GRASS;
        }
    }

    /**
     * Generates a rectangular Room inside the partition whose area is between MIN x MIN and the exact dimensions
     * of the partition area. A Room is an abstract object consisting of two Positions representing the bottom left
     * and top right corner, a floor type, etc */
    public void generateRandomRoom() {
        int lowerLeftX = random.nextInt(width - MIN);
        int lowerLeftY = random.nextInt(height - MIN);
        Position lowerP = new Position(p.X() + lowerLeftX, p.Y() + lowerLeftY);

        int upperRightX = random.nextInt(MIN - 1, width - lowerLeftX - 1);
        int upperRightY = random.nextInt(MIN - 1, height - lowerLeftY - 1);
        Position upperP = new Position(lowerP.X() + upperRightX, lowerP.Y() + upperRightY);

        this.room = new Room(lowerP, upperP, chooseRandomFloorType());
    }

    /**
     * Draws the room of that is associated with this particular Partition onto the map. */
    public void drawRoom() {
        int startX = room.lowerLeft().X();
        int startY = room.lowerLeft().Y();
        int endX = room.upperRight().X();
        int endY = room.upperRight().Y();

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
                map[x][y] = room.floorType();
            }
        }
    }

    /**
     * Returns whether width is between MIN (inclusive) and MAX (inclusive) */
    private boolean widthWithinBounds() {
        return width >= MIN && width <= MAX;
    }

    /**
     * Returns whether height is between MIN (inclusive) and MAX (inclusive) */
    private boolean heightWithinBounds() {
        return height >= MIN && height <= MAX;
    }
}
