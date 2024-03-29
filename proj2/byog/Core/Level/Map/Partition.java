package byog.Core.Level.Map;

import byog.Core.GameObject.Tile;
import byog.Core.Graphics.Sprite;
import byog.Core.Position;

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
    private final Position centre;
    private final int width;
    private final int height;
    private final Map map;
    private final List<Partition> childPartitions = new ArrayList<>(); // all rooms at and below the current node
    private Room room;
    private Partition left;  // left branch
    private Partition right; // right branch

    protected Partition(Position p, int width, int height, Map map) {
        this.position = p;
        this.width = width;
        this.height = height;
        this.centre = new Position(position.ix() +  width / 2, position.iy() + height / 2);
        this.map = map;
    }

    /** High level overview: recursively generates partition tree w/ rooms, adding latter to List
     * <p>
     * Examines partition and apply either their divideHorizontally or divideVertically method,
     * depending on their dimensions. If both dimensions are greater than MAX, either vertical
     * or horizontal splitting is chosen randomly. If new partitions are made, they are set as
     * the branches of the current partition. Method recursively traverses the newly
     * created branches and repeats the same process.
     */
    protected void generateTree(List<Room> rooms) {
        if (width > MAX || height > MAX) {
            if (width <= MAX) {
                int border = map.rand.nextInt(MIN, height - MIN);
                left = splitVertically(border);
                right = new Partition(position, width, border, map);

            } else if (height <= MAX) {
                int border = map.rand.nextInt(MIN, width - MIN);
                left = splitHorizontally(border);
                right = new Partition(position, border, height, map);

            } else {
                int choice = map.rand.nextInt(1);
                if (choice == 0) {
                    int border = map.rand.nextInt(MIN, height - MIN);
                    left = splitVertically(border);
                    right = new Partition(position, width, border, map);

                } else {
                    int border = map.rand.nextInt(MIN, width - MIN);
                    left = splitHorizontally(border);
                    right = new Partition(position, border, height, map);
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
            int lowerLeftX = map.rand.nextInt(width - MIN);
            int lowerLeftY = map.rand.nextInt(height - MIN);
            Position lowerLeft = new Position(position.ix() + lowerLeftX, position.iy() + lowerLeftY);

            int minX = lowerLeft.ix() + MIN_ROOM - 1;
            int maxX = Math.min(lowerLeft.ix() + MAX_ROOM - 1, position.ix() + width - 1);
            int minY = lowerLeft.iy() + MIN_ROOM - 1;
            int maxY = Math.min(lowerLeft.iy() + MAX_ROOM - 1, position.iy() + height - 1);

            int upperRightX = map.rand.nextInt(minX, maxX);
            int upperRightY = map.rand.nextInt(minY, maxY);
            Position upperRight = new Position(upperRightX, upperRightY);
            room = new Room(lowerLeft, upperRight, map);

            rooms.add(room);
            childPartitions.add(this);
        }
    }

    /* Makes another partition at a point about border, which is approximately in
     * the middle of the current partition's width so that both partitions are within bounds.
     * */
    private Partition splitHorizontally(int border) {
        Position newPos = new Position(position.ix() + border, position.iy());
        return new Partition(newPos, width - border, height, map);
    }

    /* Makes another partition at a point about border, which is approximately in
     * the middle of the current partition's height so that both partitions are within bounds.
     */
    private Partition splitVertically(int border) {
        Position newPos = new Position(position.ix(), position.iy() + border);
        return new Partition(newPos, width, height - border, map);
    }

    /** Select two partitions, one from the left and right branch respectively,
     * as stored in the left and right lists, then draws a path between their centres,
     * thereby connecting them and ensuring a complete graph.
     */
    protected void connectPartitions() {
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
        int start = map.to1D(a.ix(), a.iy()), target = map.to1D(b.ix(), b.iy());

        PriorityQueue<Node> fringe = new PriorityQueue<>(getDistanceComparator());
        int[] edgeTo = new int[map.width * map.height];
        int[] distTo = new int[map.width * map.height];
        Arrays.fill(distTo, Integer.MAX_VALUE);
        distTo[start] = 0;

        // Initially, add start to PQ. Then loop until target found
        fringe.add(new Node(start, 0));
        boolean targetFound = false;
        while (!fringe.isEmpty() && !targetFound) {
            int p = fringe.remove().position;

            for (int q : map.adjacent(p)) {
                // If new distance < old distance, update distTo and edgeTo
                // Add neighbour node q to PQ, factoring in heuristic
                if (distTo[p] + 1 < distTo[q]) {
                    distTo[q] = distTo[p] + 1;
                    edgeTo[q] = p;
                    fringe.add(new Node(q, (int) (distTo[q] + Position.manhattan(q, target, map))));
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
            if (curr == prev + map.width) {
                way = 'U';
            }
            else if (curr == prev + 1) {
                way = 'R';
            }
            else if (curr == prev - map.width) {
                way = 'D';
            }
            else if (curr == prev - 1) {
                way = 'L';
            }
            Position currentPos = map.toPosition(curr);
            map.place(new Tile(currentPos.ix(), currentPos.iy(), Sprite.FLOOR));

            /* Draws the three wall tiles and floor sprite that must be placed when adding a new floor
            sprite to hallway. Overall method works by adding a room of area 1 on a preexisting room. */
            int x = currentPos.ix(), y = currentPos.iy();
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
                    if (map.peek(x + i, y).getSprite() != Sprite.FLOOR) {
                        map.place(new Tile(x + i, y, Sprite.colorVariant(Sprite.WALL, 30, 30, 30, map.rand)));
                    }
                }
            }
            else if (way == 'L' || way == 'R') {
                for (int i = 0; i < 3; i += 1) {
                    if (map.peek(x, y + i).getSprite() != Sprite.FLOOR) {
                        map.place(new Tile(x, y + i, Sprite.colorVariant(Sprite.WALL, 30, 30, 30, map.rand)));
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