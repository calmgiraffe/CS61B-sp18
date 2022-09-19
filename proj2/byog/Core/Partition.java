package byog.Core;

import byog.TileEngine.TETile;
import byog.TileEngine.Tileset;

/**
 * Inner class to represent an imaginary rectangular partition of the map.
 * Position p is the coordinate of the lower left corner */
public class Partition {

    /* Partition class and instance variables */
    // All partitions side lengths should be between MIN and MAX.
    // MAX should be at least 2*MIN - 1, because a split on 2*MIN gives 2 partitions of MIN
    static final int MIN = 8;
    static final int MAX = 20;

    private final Position position;
    private int width;
    private int height;
    private final TETile[][] map;
    private final RandomExtra random;
    private Room room;

    /**
     * Partition constructor */
    Partition(Position p, int width, int height, TETile[][] map, RandomExtra r) {
        this.position = p;
        this.width = width;
        this.height = height;
        this.map = map;
        this.random = r;
    }

    /**
     * Makes another partition at a point about border, which is approximately in
     * the middle of the current partition's width so that both partitions are within bounds.
     * Then, updates the width of the current partition and currents the new partition. */
    private static Partition splitHorizontally(Partition p, int border) {
        Position newPos = new Position(p.position.x + border, p.position.y);
        Partition newPartition = new Partition(newPos, p.width - border, p.height, p.map, p.random);

        // Update the existing partition and return new Partition
        p.width = border;
        return newPartition;
    }

    /**
     * Makes another partition at a point about border, which is approximately in
     * the middle of the current partition's height so that both partitions are within bounds.
     * Then, updates the height of the current partition and currents the new partition. */
    private static Partition splitVertically(Partition p, int border) {
        Position newPos = new Position(p.position.x, p.position.y + border);
        Partition newPartition = new Partition(newPos, p.width, p.height - border, p.map, p.random);

        // Update the existing partition and return new Partition
        p.height = border;
        return newPartition;
    }

    /**
     * Examines the dimensions of the partition, and either splits it horizontally or vertically, depending
     * on whether a dimension is greater than MAX. If both dimensions are greater than MAX, either vertical
     * or horizontal splitting is chosen randomly. */
    public static Partition split(Partition p) {
        if (p.widthWithinBounds() && !p.heightWithinBounds()) {
            int border = p.random.nextIntInclusive(MIN, p.height - MIN);
            return splitVertically(p, border);

        } else if (!p.widthWithinBounds() && p.heightWithinBounds()) {
            int border = p.random.nextIntInclusive(MIN, p.width - MIN);
            return splitHorizontally(p, border);

        } else if (p.widthWithinBounds() && p.heightWithinBounds()) {
            return null;

        } else {
            int choice = p.random.nextIntInclusive(1);
            if (choice == 0) {
                int border = p.random.nextIntInclusive(MIN, p.width - MIN);
                return splitHorizontally(p, border);

            } else {
                int border = p.random.nextIntInclusive(MIN, p.height - MIN);
                return splitVertically(p, border);
            }
        }
    }

    /**
     * Randomly returns either the FLOOR or GRASS Tileset. */
    private TETile chooseRandomFloorType() {
        int choice = random.nextIntInclusive(1);
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
        int lowerLeftX = random.nextIntInclusive(width - MIN);
        int lowerLeftY = random.nextIntInclusive(height - MIN);
        Position lowerLeft = new Position(this.position.x + lowerLeftX, this.position.y + lowerLeftY);

        int upperRightX = random.nextIntInclusive(MIN - 1, width - lowerLeftX - 1);
        int upperRightY = random.nextIntInclusive(MIN - 1, height - lowerLeftY - 1);
        Position upperRight = new Position(lowerLeft.x + upperRightX, lowerLeft.y + upperRightY);

        this.room = new Room(lowerLeft, upperRight, this.chooseRandomFloorType());
    }

    /**
     * Draws the room of that is associated with this particular Partition onto the map. */
    public void drawRoom() {
        int startX = room.lowerLeft.x;
        int startY = room.lowerLeft.y;
        int endX = room.upperRight.x;
        int endY = room.upperRight.y;

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
                map[x][y] = room.floorType;
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
