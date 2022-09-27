package byog.Core;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.PriorityQueue;

/**
 * Inner class to represent an imaginary rectangular partition of the map.
 * Position p is the coordinate of the lower left corner
 */
public class Partition {

    /**
     * Partition class and instance variables
     * All partitions side lengths should be between MIN and MAX.
     * MAX should be at least 2*MIN - 1, because a split on 2*MIN gives 2 partitions of MIN
     */
    static final int MIN = 7;
    static final int MAX = 20;

    private final Position position;
    private final Position centre;
    private final int width;
    private final int height;
    private double distanceToParent;
    private Room room;
    private Partition left;
    private Partition right;
    private PriorityQueue<Partition> pQueue;

    /**
     * Partition constructor
     */
    Partition(Position p, int width, int height) {
        this.position = p;
        this.width = width;
        this.height = height;
        this.centre = new Position(position.x() + width/2, position.y() + height/2);
    }

    /**
     * Makes another partition at a point about border, which is approximately in
     * the middle of the current partition's width so that both partitions are within bounds.
     */
    private static Partition splitHorizontally(Partition p, int border) {
        Position newPos = new Position(p.position.x() + border, p.position.y());
        return new Partition(newPos, p.width - border, p.height);
    }

    /**
     * Makes another partition at a point about border, which is approximately in
     * the middle of the current partition's height so that both partitions are within bounds.
     */
    private static Partition splitVertically(Partition p, int border) {
        Position newPos = new Position(p.position.x(), p.position.y() + border);
        return new Partition(newPos, p.width, p.height - border);
    }

    /**
     * Examine partition and apply either their divideHorizontally or divideVertically method,
     * depending on their dimensions. If both dimensions are greater than MAX, either vertical
     * or horizontal splitting is chosen randomly. If new partitions are made, they are set as the branches
     * of the current partition. Finally, the method traverses the newly created branches.
     */
    public static void splitAndConnect(Partition p) {
        if (p.width > MAX || p.height > MAX) {

            if (p.width <= MAX) {
                int border = Game.random.nextIntInclusive(MIN, p.height - MIN);
                p.left = splitVertically(p, border);
                p.right = new Partition(p.position, p.width, border);

            } else if (p.height <= MAX) {
                int border = Game.random.nextIntInclusive(MIN, p.width - MIN);
                p.left = splitHorizontally(p, border);
                p.right = new Partition(p.position, border, p.height);

            } else {
                int choice = Game.random.nextIntInclusive(1);
                if (choice == 0) {
                    int border = Game.random.nextIntInclusive(MIN, p.height - MIN);
                    p.left = splitVertically(p, border);
                    p.right = new Partition(p.position, p.width, border);

                } else {
                    int border = Game.random.nextIntInclusive(MIN, p.width - MIN);
                    p.left = splitHorizontally(p, border);
                    p.right = new Partition(p.position, border, p.height);
                }
            }
            splitAndConnect(p.left);
            splitAndConnect(p.right);
            connect(p);

        } else { // if leaf
            // generate room
            p.generateRandomRoom();

            // make new PQ, add partition to it
            p.pQueue = new PriorityQueue<>(getDistanceComparator());
            p.distanceToParent = 0;
            p.pQueue.add(p);
        }
    }

    /**
     * Select two partitions, one from the left and right branch respectively, as stored in the left and right
     * pQueues, then draws a path between their centres, thereby connecting them and ensuring a complete graph.
     */
    public static void connect(Partition p) {
        // Make new pQueue
        p.pQueue = new PriorityQueue<>(getDistanceComparator());

        // At parent node, recalculate distance from parent Partition.centre to its leaf nodes' Partition.centre
        // Left branch: iterate through left PQ and recalculate distance to center
        for (Partition par: p.left.pQueue) {
            par.distanceToParent = Position.calculateDistance(par.centre, p.centre);
            p.pQueue.add(par);
        }
        // Right branch: iterate through right PQ and recalculate distance to center
        for (Partition par: p.right.pQueue) {
            par.distanceToParent = Position.calculateDistance(par.centre, p.centre);
            p.pQueue.add(par);
        }
        // Left branch: select partition that is closest to the centre
        // Right branch: select partition that is closest to the centre
        Partition minLeft = p.left.pQueue.peek();
        Partition minRight = p.right.pQueue.peek();

        Room.drawPath(minLeft.room, minRight.room);
    }

    /**
     * Given an ArrayList and a Partition tree p, traverses the tree and adds all leafs to the array.
     */
    public static void addRooms(ArrayList<Room> rooms, Partition p) {
        if (p.left() == null && p.right() == null) {
            rooms.add(p.room());
        } else {
            addRooms(rooms, p.left());
            addRooms(rooms, p.right());
        }
    }

    /**
     * Generates a rectangular Room inside the partition whose area is between MIN x MIN and the exact dimensions
     * of the partition area. A Room is an abstract object consisting of two Positions representing the bottom left
     * and top right corner, a floor type, etc
     */
    public void generateRandomRoom() {
        int lowerLeftX = Game.random.nextIntInclusive(width - MIN);
        int lowerLeftY = Game.random.nextIntInclusive(height - MIN);
        Position lowerLeft = new Position(this.position.x() + lowerLeftX, this.position.y() + lowerLeftY);

        int upperRightX = Game.random.nextIntInclusive(MIN - 1, width - lowerLeftX - 1);
        int upperRightY = Game.random.nextIntInclusive(MIN - 1, height - lowerLeftY - 1);
        Position upperRight = new Position(lowerLeft.x() + upperRightX, lowerLeft.y() + upperRightY);

        this.room = new Room(lowerLeft, upperRight);
    }

    private static class DistanceComparator implements Comparator<Partition> {
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

    public static Comparator<Partition> getDistanceComparator() {
        return new DistanceComparator();
    }

    /**
     * Returns the room associated with this Partition.
     */
    public Room room() {
        return this.room;
    }

    /**
     * Returns the left Partition associated with this Partition.
     */
    public Partition left() {
        return this.left;
    }

    /**
     * Returns the right Paritition associated with this Partition.
     */
    public Partition right() {
        return this.right;
    }

    /**
     * Returns the pQueue associated with this Partition.
     */
    public PriorityQueue<Partition> pQueue() {
        return this.pQueue;
    }
}
