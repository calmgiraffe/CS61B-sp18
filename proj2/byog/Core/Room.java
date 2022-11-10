package byog.Core;

import byog.TileEngine.TETile;
import byog.TileEngine.Tileset;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Comparator;
import java.util.PriorityQueue;

public class Room implements Serializable {

    /**
     * Private class to represent a vertex-distance pair in the pQueue.
     */
    private static class Node {
        int position;
        int distance;

        Node(int position, int distance) {
            this.position = position;
            this.distance = distance;
        }
    }

    /**
     * Instance variables
     */
    private static final int UP = 0;
    private static final int RIGHT = 1;
    private static final int DOWN = 2;
    private static final int LEFT = 3;
    private final Position lowerLeft;
    private final Position upperRight;
    private final TETile floorType;

    /**
     * Constructor
     */
    Room(Position lowerLeft, Position upperRight) {
        this.lowerLeft = lowerLeft;
        this.upperRight = upperRight;
        this.floorType = Tileset.FLOOR;
    }

    /**
     * Draws the three wall tiles and floor tile that must be placed when added a new floor tile
     * to a hallway. The overall method works by adding a 'room' of area 1 on a preexisting room.
     */
    private void drawWalls(Position p, int way, Map map) {
        int x = p.x;
        int y = p.y;

        switch (way) {
            case UP:
                x -= 1;
                y += 1;
                break;
            case RIGHT:
                x += 1;
                y -= 1;
                break;
            default:
                x -= 1;
                y -= 1;
                break;
        }
        for (int i = 0; i < 3; i++) {
            if (map.peek(x + i, y) != floorType && (way == UP || way == DOWN)) {
                map.placeTile(x + i, y, Tileset.colorVariantWall(Map.r));

            } else if (map.peek(x, y + i) != floorType && (way == LEFT || way == RIGHT)) {
                map.placeTile(x, y + i, Tileset.colorVariantWall(Map.r));
            }
        }
    }

    /**
     * Given a 1D start & end coordinate, following the child-parent relationships
     * given by the edgeTo array, draws the a* path from start to end.
     */
    private void drawPath(int[] edgeTo, int start, int end, Map map) {
        int curr = edgeTo[end];
        int prev = end;
        int direction = -1;

        while (curr != start) {
            if (curr == prev + map.width) {
                direction = UP;

            } else if (curr == prev + 1) {
                direction = RIGHT;

            } else if (curr == prev - map.width) {
                direction = DOWN;

            } else if (curr == prev - 1) {
                direction = LEFT;
            }
            Position currentPos = map.oneDToPos(curr);
            map.placeTile(currentPos.x, currentPos.y, floorType);
            drawWalls(currentPos, direction, map);
            prev = curr;
            curr = edgeTo[curr];
        }
    }

    /**
     * Given another room, draws a path (one with hallways bounding floor path) between them on map
     * using the a* algorithm.
     */
    public void astar(Room room, Map map) {
        int start = map.posToOneD(this.randomPosition(1));
        int target = map.posToOneD(room.randomPosition(1));

        PriorityQueue<Node> fringe = new PriorityQueue<>(getDistanceComparator());
        int[] edgeTo = new int[map.oneDlength];
        int[] distTo = new int[map.oneDlength];
        Arrays.fill(distTo, Integer.MAX_VALUE);
        distTo[start] = 0;

        // Initially, add start to PQ. Then loop until target found
        fringe.add(new Node(start, 0));
        boolean targetFound = false;
        while (fringe.size() > 0 && !targetFound) {
            int p = fringe.remove().position;

            for (int q : map.adjacent(p)) {
                // If new distance < old distance, update distTo and edgeTo
                // Add neighbour node q to PQ, factoring in heuristic
                if (distTo[p] + 1 < distTo[q]) {
                    distTo[q] = distTo[p] + 1;
                    edgeTo[q] = p;
                    Node n = new Node(q, distTo[q] + Position.manhattan(q, target, map));
                    fringe.add(n);
                }
                if (q == target) {
                    targetFound = true;
                    drawPath(edgeTo, start, q, map);
                }
            }
        }
    }

    /**
     * Draws the rectangular room that is associated with this particular Partition onto map.
     */
    public void drawRoom(Map map) {
        int startX = lowerLeft.x;
        int startY = lowerLeft.y;
        int endX = upperRight.x;
        int endY = upperRight.y;

        // Draw top and bottom walls
        for (int x = startX; x <= endX; x++) {
            if (map.peek(x, startY) == Tileset.NOTHING) {
                map.placeTile(x, startY, Tileset.colorVariantWall(Map.r));
            }
            if (map.peek(x, endY) == Tileset.NOTHING) {
                map.placeTile(x, endY, Tileset.colorVariantWall(Map.r));
            }
        }
        // Draw left and right walls
        for (int y = startY; y <= endY; y++) {
            if (map.peek(startX, y) == Tileset.NOTHING) {
                map.placeTile(startX, y, Tileset.colorVariantWall(Map.r));
            }
            if (map.peek(endX, y) == Tileset.NOTHING) {
                map.placeTile(endX, y, Tileset.colorVariantWall(Map.r));
            }
        }
        // Draw interior
        for (int x = startX + 1; x <= endX - 1; x++) {
            for (int y = startY + 1; y <= endY - 1; y++) {
                map.placeTile(x, y, floorType);
            }
        }
    }

    /**
     * From a position, draws an irregular room by making new positions at the top, right, bottom,
     * and left of said position, then applying the recursive method on those four new positions.
     * Depending on the location and the count, either a FLOOR or WALL tile is drawn.
     */
    public void drawIrregular(int count, Position p, Map map) {
        // Base case: count is 0 and able to place a tile on NOTHING
        if (count <= 0) {
            if (map.peek(p) == Tileset.NOTHING) {
                map.placeTile(p.x, p.y, Tileset.colorVariantWall(Map.r));
            }
        } else {
            if (map.onEdge(p)) {
                map.placeTile(p.x, p.y, Tileset.colorVariantWall(Map.r));
            } else {
                map.placeTile(p.x, p.y, floorType);
            }
            for (Position a : map.adjacent(p)) {
                drawIrregular(count - Map.r.nextIntInclusive(1, 3), a, map);
            }
        }
    }

    /**
     * Same as above but with grass and flowers.
     */
    public void drawIrregularGrass(int count, Position p, Map map) {
        if (map.peek(p) == floorType && count > 0) {

            if (Map.r.nextIntInclusive(0, 100) <= 10) {
                map.placeTile(p.x, p.y, Tileset.randomColorFlower(Map.r));
            } else {
                map.placeTile(p.x, p.y, Tileset.colorVariantGrass(Map.r));
            }
            for (Position a : map.adjacent(p)) {
                drawIrregularGrass(count - Map.r.nextIntInclusive(1, 2), a, map);
            }
        }
    }

    /**
     * Pick random location within the room, int buffer indicating the margin from the room edge.
     */
    public Position randomPosition(int buffer) {
        int xLower = lowerLeft.x + buffer;
        int xUpper = upperRight.x - buffer;
        int yLower = lowerLeft.y + buffer;
        int yUpper = upperRight.y - buffer;

        return new Position(
                Map.r.nextIntInclusive(xLower, xUpper),
                Map.r.nextIntInclusive(yLower, yUpper));
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
