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

    Partition(Position p, int width, int height) {
        this.position = p;
        this.width = width;
        this.height = height;
        this.centre = new Position(position.x + width / 2, position.y + height / 2);
    }

    /* Makes another partition at a point about border, which is approximately in
     * the middle of the current partition's width so that both partitions are within bounds.
     * */
    private Partition splitHorizontally(int border) {
        Position newPos = new Position(position.x + border, position.y);
        return new Partition(newPos, width - border, height);
    }

    /* Makes another partition at a point about border, which is approximately in
     * the middle of the current partition's height so that both partitions are within bounds.
     */
    private Partition splitVertically(int border) {
        Position newPos = new Position(position.x, position.y + border);
        return new Partition(newPos, width, height - border);
    }

    /** Examine partition and apply either their divideHorizontally or divideVertically method,
     * depending on their dimensions. If both dimensions are greater than MAX, either vertical
     * or horizontal splitting is chosen randomly. If new partitions are made, they are set as
     * the branches of the current partition. Finally, method traverses the newly created branches.
     */
    public void splitAndConnect() {
        if (width > MAX || height > MAX) {

            if (width <= MAX) {
                int border = Game.rand.nextInt(MIN, height - MIN);
                left = splitVertically(border);
                right = new Partition(position, width, border);

            } else if (height <= MAX) {
                int border = Game.rand.nextInt(MIN, width - MIN);
                left = splitHorizontally(border);
                right = new Partition(position, border, height);

            } else {
                int choice = Game.rand.nextInt(1);
                if (choice == 0) {
                    int border = Game.rand.nextInt(MIN, height - MIN);
                    left = splitVertically(border);
                    right = new Partition(position, width, border);

                } else {
                    int border = Game.rand.nextInt(MIN, width - MIN);
                    left = splitHorizontally(border);
                    right = new Partition(position, border, height);
                }
            }
            left.splitAndConnect();
            right.splitAndConnect();
            this.connectLeftAndRight();

        } else { // if leaf
            // generate random room
            generateRandomRoom();

            // make new PQ, add partition to it
            this.pQueue = new PriorityQueue<>(getDistanceComparator());
            distanceToParent = 0;
            pQueue.add(this);
        }
    }

    /* Select two partitions, one from the left and right branch respectively,
     * as stored in the left and right pQueues, then draws a path between their centres,
     * thereby connecting them and ensuring a complete graph.
     */
    private void connectLeftAndRight() {
        // Make new pQueue
        this.pQueue = new PriorityQueue<>(getDistanceComparator());

        // At parent node, recalculate distance from parent centre to its leaf nodes' centre
        // Left branch: iterate through left PQ and recalculate distance to center
        for (Partition par: left.pQueue) {
            par.distanceToParent = Position.manhattan(par.centre, centre);
            pQueue.add(par);
        }
        // Right branch: iterate through right PQ and recalculate distance to center
        for (Partition par: right.pQueue) {
            par.distanceToParent = Position.manhattan(par.centre, centre);
            pQueue.add(par);
        }
        // Left branch: select partition that is closest to the centre
        // Right branch: select partition that is closest to the centre
        Partition minLeft = left.pQueue.peek();
        Partition minRight = right.pQueue.peek();

        minLeft.room.astar(minRight.room);
    }

    /** Given some root, traverses tree and returns list of all leafs. */
    public ArrayList<Room> returnRooms() {
        ArrayList<Room> rooms = new ArrayList<>();
        returnRoomsHelper(rooms);
        return rooms;
    }

    private void returnRoomsHelper(ArrayList<Room> rooms) {
        if (left == null && right == null) {
            rooms.add(room);
        } else {
            left.returnRoomsHelper(rooms);
            right.returnRoomsHelper(rooms);
        }
    }

    /* Generates a rectangular Room inside the partition whose area is between MIN x MIN and the
     * exact dimensions of the partition area. A Room is an abstract object consisting of two
     * Positions representing the bottom left and top right corner, a floor type, etc
     */
    private void generateRandomRoom() {
        int lowerLeftX = Game.rand.nextInt(width - MIN);
        int lowerLeftY = Game.rand.nextInt(height - MIN);
        Position lowerLeft = new Position(position.x + lowerLeftX, position.y + lowerLeftY);

        int minX = lowerLeft.x + MIN_ROOM - 1;
        int maxX = Math.min(lowerLeft.x + MAX_ROOM - 1, position.x + width - 1);
        int minY = lowerLeft.y + MIN_ROOM - 1;
        int maxY = Math.min(lowerLeft.y + MAX_ROOM - 1, position.y + height - 1);

        int upperRightX = Game.rand.nextInt(minX, maxX);
        int upperRightY = Game.rand.nextInt(minY, maxY);
        Position upperRight = new Position(upperRightX, upperRightY);

        this.room = new Room(lowerLeft, upperRight);
    }

    /* Comparator based on the difference between the two partitions distanceToParent.
     * Returns 1, 0, or -1 because distanceToParent is a double. */
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