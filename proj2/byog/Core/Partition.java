package byog.Core;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.PriorityQueue;

/**
 * Inner class to represent an imaginary rectangular partition of the map.
 * Position p is the coordinate of the lower left corner
 */
public class Partition implements Serializable {

    /* All partitions side lengths should be between MIN and MAX.
     * MAX should be at least 2*MIN - 1, because a split on 2*MIN gives 2 partitions of MIN */
    private static final int MIN = 7;
    private static final int MAX = 18;
    private static final int MIN_ROOM = 7;
    private static final int MAX_ROOM = 12;

    /* Private instance variables */
    private final Position position;
    private final Position centre;
    private final int width;
    private final int height;
    private double distanceToParent;
    private Room room;
    private Partition left;
    private Partition right;
    private PriorityQueue<Partition> pQueue;

    /** Constructor for subpartitions */
    Partition(Position p, int width, int height) {
        this.position = p;
        this.width = width;
        this.height = height;
        this.centre = new Position(position.x + width / 2, position.y + height / 2);
    }

    /* Makes another partition at a point about border, which is approximately in
     * the middle of the current partition's width so that both partitions are within bounds.
     * */
    private static Partition splitHorizontally(Partition p, int border) {
        Position newPos = new Position(p.position.x + border, p.position.y);
        return new Partition(newPos, p.width - border, p.height);
    }

    /* Makes another partition at a point about border, which is approximately in
     * the middle of the current partition's height so that both partitions are within bounds.
     */
    private static Partition splitVertically(Partition p, int border) {
        Position newPos = new Position(p.position.x, p.position.y + border);
        return new Partition(newPos, p.width, p.height - border);
    }

    /** Examine partition and apply either their divideHorizontally or divideVertically method,
     * depending on their dimensions. If both dimensions are greater than MAX, either vertical
     * or horizontal splitting is chosen randomly. If new partitions are made, they are set as
     * the branches of the current partition. Finally, method traverses the newly created branches.
     */
    public static void splitAndConnect(Partition p) {
        if (p.width > MAX || p.height > MAX) {

            if (p.width <= MAX) {
                int border = Game.rand.nextIntInclusive(MIN, p.height - MIN);
                p.left = splitVertically(p, border);
                p.right = new Partition(p.position, p.width, border);

            } else if (p.height <= MAX) {
                int border = Game.rand.nextIntInclusive(MIN, p.width - MIN);
                p.left = splitHorizontally(p, border);
                p.right = new Partition(p.position, border, p.height);

            } else {
                int choice = Game.rand.nextIntInclusive(1);
                if (choice == 0) {
                    int border = Game.rand.nextIntInclusive(MIN, p.height - MIN);
                    p.left = splitVertically(p, border);
                    p.right = new Partition(p.position, p.width, border);

                } else {
                    int border = Game.rand.nextIntInclusive(MIN, p.width - MIN);
                    p.left = splitHorizontally(p, border);
                    p.right = new Partition(p.position, border, p.height);
                }
            }
            splitAndConnect(p.left);
            splitAndConnect(p.right);
            connectLeftAndRight(p);

        } else { // if leaf
            // generate random room
            p.generateRandomRoom();

            // make new PQ, add partition to it
            p.pQueue = new PriorityQueue<>(getDistanceComparator());
            p.distanceToParent = 0;
            p.pQueue.add(p);
        }
    }

    /* Select two partitions, one from the left and right branch respectively,
     * as stored in the left and right pQueues, then draws a path between their centres,
     * thereby connecting them and ensuring a complete graph.
     */
    private static void connectLeftAndRight(Partition p) {
        // Make new pQueue
        p.pQueue = new PriorityQueue<>(getDistanceComparator());

        // At parent node, recalculate distance from parent centre to its leaf nodes' centre
        // Left branch: iterate through left PQ and recalculate distance to center
        for (Partition par: p.left.pQueue) {
            par.distanceToParent = Position.manhattan(par.centre, p.centre);
            p.pQueue.add(par);
        }
        // Right branch: iterate through right PQ and recalculate distance to center
        for (Partition par: p.right.pQueue) {
            par.distanceToParent = Position.manhattan(par.centre, p.centre);
            p.pQueue.add(par);
        }
        // Left branch: select partition that is closest to the centre
        // Right branch: select partition that is closest to the centre
        Partition minLeft = p.left.pQueue.peek();
        Partition minRight = p.right.pQueue.peek();

        minLeft.room.astar(minRight.room);
    }

    /** Given some root, traverses tree and returns list of all leafs. */
    public static ArrayList<Room> returnRooms(Partition p) {
        ArrayList<Room> rooms = new ArrayList<>();
        returnRoomsHelper(rooms, p);
        return rooms;
    }

    private static void returnRoomsHelper(ArrayList<Room> rooms, Partition p) {
        if (p.left == null && p.right == null) {
            rooms.add(p.room);
        } else {
            returnRoomsHelper(rooms, p.left);
            returnRoomsHelper(rooms, p.right);
        }
    }

    /* Generates a rectangular Room inside the partition whose area is between MIN x MIN and the
     * exact dimensions of the partition area. A Room is an abstract object consisting of two
     * Positions representing the bottom left and top right corner, a floor type, etc
     */
    private void generateRandomRoom() {
        int lowerLeftX = Game.rand.nextIntInclusive(width - MIN);
        int lowerLeftY = Game.rand.nextIntInclusive(height - MIN);
        Position lowerLeft = new Position(position.x + lowerLeftX, position.y + lowerLeftY);

        int minX = lowerLeft.x + MIN_ROOM - 1;
        int maxX = Math.min(lowerLeft.x + MAX_ROOM - 1, position.x + width - 1);
        int minY = lowerLeft.y + MIN_ROOM - 1;
        int maxY = Math.min(lowerLeft.y + MAX_ROOM - 1, position.y + height - 1);

        int upperRightX = Game.rand.nextIntInclusive(minX, maxX);
        int upperRightY = Game.rand.nextIntInclusive(minY, maxY);
        Position upperRight = new Position(upperRightX, upperRightY);

        this.room = new Room(lowerLeft, upperRight);
    }

    /* Comparator based on the difference between the two partitions distanceToParent.
     * Returns 1, 0, or -1 because distanceToParent is a double.
     */
    private static class DistanceComparator implements Comparator<Partition>, Serializable {
        public int compare(Partition a, Partition b) {
            if (a.distanceToParent - b.distanceToParent > 0) {
                return 1;
            } else if (a.distanceToParent == b.distanceToParent) {
                return 0;
            } else {
                return -1;
            }
        }
    }

    private static Comparator<Partition> getDistanceComparator() {
        return new DistanceComparator();
    }
}
