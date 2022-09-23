package byog.Core;

/**
 * Inner class to represent an imaginary rectangular partition of the map.
 * Position p is the coordinate of the lower left corner */
public class Partition {

    /**
     * Partition class and instance variables */
    // All partitions side lengths should be between MIN and MAX.
    // MAX should be at least 2*MIN - 1, because a split on 2*MIN gives 2 partitions of MIN
    static final int MIN = 8;
    static final int MAX = 16;

    private final Position position;
    private final int width;
    private final int height;
    protected Room room;
    protected Partition partitionA;
    protected Partition partitionB;

    /**
     * Partition constructor */
    Partition(Position p, int width, int height) {
        this.position = p;
        this.width = width;
        this.height = height;
    }

    /**
     * Makes another partition at a point about border, which is approximately in
     * the middle of the current partition's width so that both partitions are within bounds.
     * Then, updates the width of the current partition and currents the new partition. */
    private static Partition splitHorizontally(Partition p, int border) {
        Position newPos = new Position(p.position.x + border, p.position.y);
        return new Partition(newPos, p.width - border, p.height);
    }

    /**
     * Makes another partition at a point about border, which is approximately in
     * the middle of the current partition's height so that both partitions are within bounds.
     * Then, updates the height of the current partition and currents the new partition. */
    private static Partition splitVertically(Partition p, int border) {
        Position newPos = new Position(p.position.x, p.position.y + border);
        return new Partition(newPos, p.width, p.height - border);
    }

    /**
     * Examine partition and apply either their divideHorizontally or divideVertically method,
     * depending on their dimensions. If both dimensions are greater than MAX, either vertical
     * or horizontal splitting is chosen randomly. If new partitions are made, they are set as the branches
     * of the current partition. Finally, the method traverses the newly created branches. */
    public static void split(Partition p) {
        if (p.width > MAX || p.height > MAX) {

            if (p.width <= MAX) {
                int border = Game.random.nextIntInclusive(MIN, p.height - MIN);
                p.partitionA = splitVertically(p, border);
                p.partitionB = new Partition(p.position, p.width, border);

            } else if (p.height <= MAX) {
                int border = Game.random.nextIntInclusive(MIN, p.width - MIN);
                p.partitionA = splitHorizontally(p, border);
                p.partitionB = new Partition(p.position, border, p.height);

            } else {
                int choice = Game.random.nextIntInclusive(1);
                if (choice == 0) {
                    int border = Game.random.nextIntInclusive(MIN, p.height - MIN);
                    p.partitionA = splitVertically(p, border);
                    p.partitionB = new Partition(p.position, p.width, border);

                } else {
                    int border = Game.random.nextIntInclusive(MIN, p.width - MIN);
                    p.partitionA = splitHorizontally(p, border);
                    p.partitionB = new Partition(p.position, border, p.height);
                }
            }
            Partition.split(p.partitionA);
            Partition.split(p.partitionB);
        }
        p.generateRandomRoom(); // generate room if leaf
    }

    /**
     * Generates a rectangular Room inside the partition whose area is between MIN x MIN and the exact dimensions
     * of the partition area. A Room is an abstract object consisting of two Positions representing the bottom left
     * and top right corner, a floor type, etc */
    public void generateRandomRoom() {
        int lowerLeftX = Game.random.nextIntInclusive(width - MIN);
        int lowerLeftY = Game.random.nextIntInclusive(height - MIN);
        Position lowerLeft = new Position(this.position.x + lowerLeftX, this.position.y + lowerLeftY);

        int upperRightX = Game.random.nextIntInclusive(MIN - 1, width - lowerLeftX - 1);
        int upperRightY = Game.random.nextIntInclusive(MIN - 1, height - lowerLeftY - 1);
        Position upperRight = new Position(lowerLeft.x + upperRightX, lowerLeft.y + upperRightY);

        this.room = new Room(lowerLeft, upperRight);
    }
}
