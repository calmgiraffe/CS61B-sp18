package byog.Core;

import byog.TileEngine.TETile;
import byog.TileEngine.Tileset;

import java.util.Arrays;
import java.util.Comparator;
import java.util.PriorityQueue;

public class Room {

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

    private final Position lowerLeft;
    private final Position upperRight;
    private final Position centre;
    private final TETile floorType;
    private final RandomExtra r;

    /**
     * Constructor
     */
    Room(Position lowerLeft, Position upperRight, RandomExtra r) {
        this.lowerLeft = lowerLeft;
        this.upperRight = upperRight;
        this.centre = new Position(
                (lowerLeft.x() + upperRight.x()) / 2,
                (lowerLeft.y() + upperRight.y()) / 2);
        this.floorType = Tileset.FLOOR;
        this.r = r;
    }

    /**
     * Draws the three wall tiles and floor tile that must be placed when added a new floor tile
     * to a hallway. The overall method works by adding a 'room' of area 1 on a preexisting room.
     */
    private void drawHallway(Position p, int way, Map map) {
        int x = p.x();
        int y = p.y();

        if (way == 0) { // up
            x -= 1;
            y += 1;
        } else if (way == 1) { // right
            x += 1;
            y -= 1;
        } else { // down or left
            x -= 1;
            y -= 1;
        }
        for (int i = 0; i < 3; i++) {
            if (map.peek(x + i, y) != floorType && (way % 2 == 0)) { // up or down
                map.placeTile(x + i, y, Tileset.colorVariantWall(r));

            } else if (map.peek(x, y + i) != floorType && (way % 2 == 1)) { // left or right
                map.placeTile(x, y + i, Tileset.colorVariantWall(r));
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
        while (curr != start) {
            map.placeTile(map.oneDimensionalToPosition(curr), floorType);

            if (curr == prev + map.width()) { // up
                drawHallway(map.oneDimensionalToPosition(curr), 0, map);

            } else if (curr == prev + 1) { // right
                drawHallway(map.oneDimensionalToPosition(curr), 1, map);

            } else if (curr == prev - map.width()) { // down
                drawHallway(map.oneDimensionalToPosition(curr), 2, map);

            } else if (curr == prev - 1) { // left
                drawHallway(map.oneDimensionalToPosition(curr), 3, map);
            }
            prev = curr;
            curr = edgeTo[curr];
        }
    }

    /**
     * Given another room, draws a path (one with hallways bounding floor path) between them on map
     * using the a* algorithm.
     */
    public void astar(Room room, Map map) {
        int start = map.positionToOneDimensional(
                Position.randomPositionWithinRadius(this.centre, r));
        int target = map.positionToOneDimensional(
                Position.randomPositionWithinRadius(room.centre, r));

        PriorityQueue<Node> fringe = new PriorityQueue<>(getDistanceComparator());
        int[] edgeTo = new int[map.oneDlength()];
        int[] distTo = new int[map.oneDlength()];
        Arrays.fill(distTo, 2147483647);
        boolean targetFound = false;

        fringe.add(new Node(start, 0));
        while (fringe.size() > 0 && !targetFound) {
            int p = fringe.remove().position;
            for (int q : map.adjacent(p)) {
                if (distTo[p] + 1 < distTo[q]) {
                    distTo[q] = distTo[p] + 1;
                    edgeTo[q] = p;
                    Node n = new Node(q, distTo[q] + Position.euclidean(q, target, map));
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
        int startX = lowerLeft.x();
        int startY = lowerLeft.y();
        int endX = upperRight.x();
        int endY = upperRight.y();

        // Draw top and bottom walls
        for (int x = startX; x <= endX; x++) {
            if (map.peek(x, startY) == Tileset.NOTHING) {
                map.placeTile(x, startY, Tileset.colorVariantWall(r));
            }
            if (map.peek(x, endY) == Tileset.NOTHING) {
                map.placeTile(x, endY, Tileset.colorVariantWall(r));
            }
        }
        // Draw left and right walls
        for (int y = startY; y <= endY; y++) {
            if (map.peek(startX, y) == Tileset.NOTHING) {
                map.placeTile(startX, y, Tileset.colorVariantWall(r));
            }
            if (map.peek(endX, y) == Tileset.NOTHING) {
                map.placeTile(endX, y, Tileset.colorVariantWall(r));
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
                map.placeTile(p, Tileset.colorVariantWall(r));
            }
        } else {
            if (map.onEdge(p)) {
                map.placeTile(p, Tileset.colorVariantWall(r));
            } else {
                map.placeTile(p, floorType);
            }
            Position pUp = new Position(p.x(), p.y() + 1);
            Position pRight = new Position(p.x() + 1, p.y());
            Position pDown = new Position(p.x(), p.y() - 1);
            Position pLeft = new Position(p.x() - 1, p.y());

            drawIrregular(count - r.nextIntInclusive(1, 3), pUp, map);
            drawIrregular(count - r.nextIntInclusive(1, 3), pRight, map);
            drawIrregular(count - r.nextIntInclusive(1, 3), pDown, map);
            drawIrregular(count - r.nextIntInclusive(1, 3), pLeft, map);
        }
    }

    /**
     * Same as above but with grass and flowers.
     */
    public void drawIrregularGrass(int count, Position p, Map map) {
        if (map.peek(p) == floorType && count > 0) {

            if (r.nextIntInclusive(0, 100) <= 10) {
                map.placeTile(p, Tileset.randomColorFlower(r));
            } else {
                map.placeTile(p, Tileset.GRASS);
            }
            Position pUp = new Position(p.x(), p.y() + 1);
            Position pRight = new Position(p.x() + 1, p.y());
            Position pDown = new Position(p.x(), p.y() - 1);
            Position pLeft = new Position(p.x() - 1, p.y());

            drawIrregularGrass(count - r.nextIntInclusive(1, 2), pUp, map);
            drawIrregularGrass(count - r.nextIntInclusive(1, 2), pRight, map);
            drawIrregularGrass(count - r.nextIntInclusive(1, 2), pDown, map);
            drawIrregularGrass(count - r.nextIntInclusive(1, 2), pLeft, map);
        }
    }

    /**
     * Pick random location within the room.
     */
    public Position randomPositionInRoom(int buffer) {
        int xLower = lowerLeft.x() + buffer;
        int xUpper = upperRight.x() - buffer;
        int yLower = lowerLeft.y() + buffer;
        int yUpper = upperRight.y() - buffer;

        return new Position(
                r.nextIntInclusive(xLower, xUpper),
                r.nextIntInclusive(yLower, yUpper));
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
