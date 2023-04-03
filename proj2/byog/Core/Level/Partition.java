package byog.Core.Level;

import byog.RandomTools.RandomInclusive;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Inner class to represent an imaginary rectangular partition of the level.
 * Position p is the coordinate of the lower left corner
 */
public class Partition implements Serializable {
    /* All partitions side lengths should be between MIN and MAX.
     MAX should be at least 2*MIN - 1, because a split on 2*MIN gives 2 partitions of MIN */
    private static final int MIN = 7;
    private static final int MAX = 18;
    private static final int MIN_ROOM = 7;
    private static final int MAX_ROOM = 12;

    /* Private instance variables */
    private final Position position; // position is given to be bottom left corner
    protected final Position centre;
    private final int width;
    private final int height;
    protected Room room;
    protected Partition left;  // left branch
    protected Partition right; // right branch
    protected List<Partition> childPartitions = new ArrayList<>(); // all rooms at and below the current node
    private final RandomInclusive rand;

    Partition(Position p, int width, int height, RandomInclusive rand) {
        this.position = p;
        this.width = width;
        this.height = height;
        this.centre = new Position(position.x + width / 2, position.y + height / 2);
        this.rand = rand;
    }

    /** High level overview: recursively generates partition tree
     * <p>
     * Examines partition and apply either their divideHorizontally or divideVertically method,
     * depending on their dimensions. If both dimensions are greater than MAX, either vertical
     * or horizontal splitting is chosen randomly. If new partitions are made, they are set as
     * the branches of the current partition. Method recursively traverses the newly
     * created branches and repeats the same process.
     */
    public void generateTree(List<Room> rooms) {
        if (width > MAX || height > MAX) {
            if (width <= MAX) {
                int border = rand.nextInt(MIN, height - MIN);
                left = splitVertically(border);
                right = new Partition(position, width, border, rand);

            } else if (height <= MAX) {
                int border = rand.nextInt(MIN, width - MIN);
                left = splitHorizontally(border);
                right = new Partition(position, border, height, rand);

            } else {
                int choice = rand.nextInt(1);
                if (choice == 0) {
                    int border = rand.nextInt(MIN, height - MIN);
                    left = splitVertically(border);
                    right = new Partition(position, width, border, rand);

                } else {
                    int border = rand.nextInt(MIN, width - MIN);
                    left = splitHorizontally(border);
                    right = new Partition(position, border, height, rand);
                }
            }
            left.generateTree(rooms);
            right.generateTree(rooms);
            childPartitions.addAll(left.childPartitions);
            childPartitions.addAll(right.childPartitions);

        } else {
            /* Leaf node logic */
            /* Generate a rectangular Room inside the partition whose area is between MIN x MIN and the
             * exact dimensions of the partition area. A Room is an abstract object consisting of two
             * Positions representing the bottom left and top right corner, a floor type, etc */
            int lowerLeftX = rand.nextInt(width - MIN);
            int lowerLeftY = rand.nextInt(height - MIN);
            Position lowerLeft = new Position(position.x + lowerLeftX, position.y + lowerLeftY);

            int minX = lowerLeft.x + MIN_ROOM - 1;
            int maxX = Math.min(lowerLeft.x + MAX_ROOM - 1, position.x + width - 1);
            int minY = lowerLeft.y + MIN_ROOM - 1;
            int maxY = Math.min(lowerLeft.y + MAX_ROOM - 1, position.y + height - 1);

            int upperRightX = rand.nextInt(minX, maxX);
            int upperRightY = rand.nextInt(minY, maxY);
            Position upperRight = new Position(upperRightX, upperRightY);
            room = new Room(lowerLeft, upperRight, rand);

            rooms.add(room);
            childPartitions.add(this);
        }
    }

    /* Makes another partition at a point about border, which is approximately in
     * the middle of the current partition's width so that both partitions are within bounds.
     * */
    private Partition splitHorizontally(int border) {
        Position newPos = new Position(position.x + border, position.y);
        return new Partition(newPos, width - border, height, rand);
    }

    /* Makes another partition at a point about border, which is approximately in
     * the middle of the current partition's height so that both partitions are within bounds.
     */
    private Partition splitVertically(int border) {
        Position newPos = new Position(position.x, position.y + border);
        return new Partition(newPos, width, height - border, rand);
    }
}