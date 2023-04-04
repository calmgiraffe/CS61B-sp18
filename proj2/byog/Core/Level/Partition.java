package byog.Core.Level;

import byog.Core.Graphics.Tileset;

import java.io.Serializable;
import java.util.*;

/**
 * Inner class to represent an imaginary rectangular partition of the level.
 * Position p is the coordinate of the lower left corner
 */
public class Partition implements Serializable {
    /* Private class to represent a vertex-distance pair in the pQueue. */
    private static class Node {
        int position;
        int distance;

        Node(int position, int distance) {
            this.position = position;
            this.distance = distance;
        }
    }
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
    protected final List<Partition> childPartitions = new ArrayList<>(); // all rooms at and below the current node
    private final Level level;

    protected Partition(Position p, int width, int height, Level level) {
        this.position = p;
        this.width = width;
        this.height = height;
        this.centre = new Position(position.x + width / 2, position.y + height / 2);
        this.level = level;
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
                int border = level.rand.nextInt(MIN, height - MIN);
                left = splitVertically(border);
                right = new Partition(position, width, border, level);

            } else if (height <= MAX) {
                int border = level.rand.nextInt(MIN, width - MIN);
                left = splitHorizontally(border);
                right = new Partition(position, border, height, level);

            } else {
                int choice = level.rand.nextInt(1);
                if (choice == 0) {
                    int border = level.rand.nextInt(MIN, height - MIN);
                    left = splitVertically(border);
                    right = new Partition(position, width, border, level);

                } else {
                    int border = level.rand.nextInt(MIN, width - MIN);
                    left = splitHorizontally(border);
                    right = new Partition(position, border, height, level);
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
            int lowerLeftX = level.rand.nextInt(width - MIN);
            int lowerLeftY = level.rand.nextInt(height - MIN);
            Position lowerLeft = new Position(position.x + lowerLeftX, position.y + lowerLeftY);

            int minX = lowerLeft.x + MIN_ROOM - 1;
            int maxX = Math.min(lowerLeft.x + MAX_ROOM - 1, position.x + width - 1);
            int minY = lowerLeft.y + MIN_ROOM - 1;
            int maxY = Math.min(lowerLeft.y + MAX_ROOM - 1, position.y + height - 1);

            int upperRightX = level.rand.nextInt(minX, maxX);
            int upperRightY = level.rand.nextInt(minY, maxY);
            Position upperRight = new Position(upperRightX, upperRightY);
            room = new Room(lowerLeft, upperRight, level);

            rooms.add(room);
            childPartitions.add(this);
        }
    }

    /* Makes another partition at a point about border, which is approximately in
     * the middle of the current partition's width so that both partitions are within bounds.
     * */
    private Partition splitHorizontally(int border) {
        Position newPos = new Position(position.x + border, position.y);
        return new Partition(newPos, width - border, height, level);
    }

    /* Makes another partition at a point about border, which is approximately in
     * the middle of the current partition's height so that both partitions are within bounds.
     */
    private Partition splitVertically(int border) {
        Position newPos = new Position(position.x, position.y + border);
        return new Partition(newPos, width, height - border, level);
    }

    /** Select two partitions, one from the left and right branch respectively,
     * as stored in the left and right lists, then draws a path between their centres,
     * thereby connecting them and ensuring a complete graph.
     */
    public void connectPartitions() {
        if (left == null && right == null) {
            return;
        }
        left.connectPartitions();
        right.connectPartitions();

        double leftDist = Double.MAX_VALUE, rightDist = Double.MAX_VALUE;
        Partition closestLeft = null, closestRight = null;
        double currDist;

        for (Partition p : left.childPartitions) {
            currDist = Position.euclidean(p.centre, centre);
            if (currDist < leftDist) {
                leftDist = currDist;
                closestLeft = p;
            }
        }
        for (Partition p : right.childPartitions) {
            currDist = Position.euclidean(p.centre, centre);
            if (currDist < rightDist) {
                rightDist = currDist;
                closestRight = p;
            }
        }
        // Draws a hallway between the two rooms of the two partitions
        astar(closestLeft.room, closestRight.room);
    }

    /* Draw a completed hallway between A and B, picking a random location in the Room */
    private void astar(Room roomA, Room roomB) {
        Position a = roomA.randomPosition(1), b = roomB.randomPosition(1);
        int start = level.to1D(a.x, a.y), target = level.to1D(b.x, b.y);

        PriorityQueue<Node> fringe = new PriorityQueue<>(getDistanceComparator());
        int[] edgeTo = new int[level.width * level.height];
        int[] distTo = new int[level.width * level.height];
        Arrays.fill(distTo, Integer.MAX_VALUE);
        distTo[start] = 0;

        // Initially, add start to PQ. Then loop until target found
        fringe.add(new Node(start, 0));
        boolean targetFound = false;
        while (!fringe.isEmpty() && !targetFound) {
            int p = fringe.remove().position;

            for (int q : level.adjacent(p)) {
                // If new distance < old distance, update distTo and edgeTo
                // Add neighbour node q to PQ, factoring in heuristic
                if (distTo[p] + 1 < distTo[q]) {
                    distTo[q] = distTo[p] + 1;
                    edgeTo[q] = p;
                    fringe.add(new Node(q, distTo[q] + Position.manhattan(q, target, level)));
                }
                if (q == target) {
                    targetFound = true;
                    this.drawPath(edgeTo, start, q);
                }
            }
        }
        // Draw rooms with grass after a hallway is made
        roomA.drawRoom();
        roomB.drawRoom();
    }

    /* Given a 1D start & end coordinate, by following the child-parent relationships
     * given by the edgeTo array, draws the astar path from start to end. */
    private void drawPath(int[] edgeTo, int start, int end) {
        int curr = edgeTo[end], prev = end;
        char way = '~';

        while (curr != start) {
            if (curr == prev + level.width) {
                way = 'U';

            } else if (curr == prev + 1) {
                way = 'R';

            } else if (curr == prev - level.width) {
                way = 'D';

            } else if (curr == prev - 1) {
                way = 'L';
            }
            Position currentPos = level.toPosition(curr);
            level.place(currentPos.x, currentPos.y, Tileset.FLOOR);

            /* Draws the three wall tiles and floor tile that must be placed when adding a new floor
            tile to hallway. Overall method works by adding a room of area 1 on a preexisting room. */
            int x = currentPos.x, y = currentPos.y;
            switch (way) {
                case 'U' -> {
                    x -= 1;
                    y += 1;
                }
                case 'R' -> {
                    x += 1;
                    y -= 1;
                }
                default -> {
                    x -= 1;
                    y -= 1;
                }
            }
            if (way == 'U' || way == 'D') {
                for (int i = 0; i < 3; i += 1) {
                    if (level.peek(x + i, y) != Tileset.FLOOR) {
                        level.place(x + i, y, Tileset.colorVariantWall(level.rand));
                    }
                }
            } else if (way == 'L' || way == 'R') {
                for (int i = 0; i < 3; i += 1) {
                    if (level.peek(x, y + i) != Tileset.FLOOR) {
                        level.place(x, y + i, Tileset.colorVariantWall(level.rand));
                    }
                }
            }
            /* End drawWalls() */
            prev = curr;
            curr = edgeTo[curr];
        }
    }

    private static class DistanceComparator implements Comparator<Node> {
        public int compare(Node a, Node b) {
            return a.distance - b.distance;
        }
    }

    private static Comparator<Node> getDistanceComparator() {
        return new DistanceComparator();
    }
}