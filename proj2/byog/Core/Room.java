package byog.Core;

import byog.TileEngine.TETile;
import byog.TileEngine.Tileset;

import java.util.Arrays;
import java.util.Comparator;
import java.util.PriorityQueue;

public class Room {

    private class Node {
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
    }

    /**
     * Draws the three wall tiles and floor tile that must be placed when added a new floor tile
     * to a hallway. The overall method works by adding a 'room' of area 1 on a preexisting room.
     */
    private static void drawHallway(Position p, int way, Map map, RandomExtra r) {
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
        // Draws the three wall tiles that must be placed when added a new floor tile to a hallway.
        // The overall method works by adding a 'room' of area 1 on a preexisting room.
        for (int i = 0; i < 3; i++) {
            if (map.peek(x + i, y) != Tileset.FLOOR && way % 2 == 0) { // 0 or 2
                map.placeTile(x + i, y, Tileset.colorVariantWall(r));

            } else if (map.peek(x, y + i) != Tileset.FLOOR && way % 2 == 1) { // 1 or 3
                map.placeTile(x, y + i, Tileset.colorVariantWall(r));
            }
        }
        map.placeTile(p, Tileset.FLOOR);
    }

    public void drawPath(int[] edgeTo, int start, int end, Map map, RandomExtra r) {
        int next = end;
        while (next != start) {
            if (edgeTo[next] == next + map.width()) { // up
                drawHallway(map.oneDimensionalToPosition(next), 0, map, r);
            } else if (edgeTo[next] == next + 1) { // right
                drawHallway(map.oneDimensionalToPosition(next), 1, map, r);
            } else if (edgeTo[next] == next - map.width()) { // down
                drawHallway(map.oneDimensionalToPosition(next), 2, map, r);
            } else if (edgeTo[next] == next - 1) { // left
                drawHallway(map.oneDimensionalToPosition(next), 3, map, r);
            }
            next = edgeTo[next];
        }
    }


    /**
     * Given two rooms, draws a path (one with hallways bounding floor path) between them on map.
     */
    public void astar(Room room, RandomExtra r, Map map) {
        Position startPos = Position.randomPositionWithinRadius(this.centre, r);
        Position targetPos = Position.randomPositionWithinRadius(room.centre, r);
        int start = map.positionToOneDimensional(startPos);
        int target = map.positionToOneDimensional(targetPos);

        PriorityQueue<Node> fringe = new PriorityQueue<>(getDistanceComparator());

        int[] edgeTo = new int[map.oneDlength()];
        int[] distTo = new int[map.oneDlength()];
        Arrays.fill(distTo, 2147483647);
        boolean targetFound = false;

        fringe.add(new Node(start, 0));
        distTo[start] = 0;

        while (fringe.size() > 0 && !targetFound) {
            int p = fringe.remove().position;
            for (int q : map.adjacent(p)) {
                if (distTo[p] + 1 < distTo[q]) {
                    distTo[q] = distTo[p] + 1;
                    edgeTo[q] = p;
                    Node n = new Node(q, distTo[q] + (int) Position.euclidean(q, target, map));
                    fringe.add(n);
                }
                if (q == target) {
                    targetFound = true;
                    drawPath(edgeTo, start, q, map, r);
                }
            }
        }
    }

    /**
     * Draws the rectangular room that is associated with this particular Partition onto map.
     */
    public void drawRoom(Map map, RandomExtra r) {
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
    public void drawIrregular(int count, Position p, RandomExtra r, Map map) {
        // Base case: count is 0 and able to place a tile on NOTHING
        if (count <= 0) {
            if (map.peek(p) == Tileset.NOTHING) {
                map.placeTile(p, Tileset.colorVariantWall(r));
            }
        } else {
            if (map.onEdge(p)) {
                map.placeTile(p, Tileset.colorVariantWall(r));
            } else {
                map.placeTile(p, Tileset.FLOOR);
            }
            Position pUp = new Position(p.x(), p.y() + 1);
            Position pRight = new Position(p.x() + 1, p.y());
            Position pDown = new Position(p.x(), p.y() - 1);
            Position pLeft = new Position(p.x() - 1, p.y());

            int n0 = r.nextIntInclusive(1, 3);
            int n1 = r.nextIntInclusive(1, 3);
            int n2 = r.nextIntInclusive(1, 3);
            int n3 = r.nextIntInclusive(1, 3);

            drawIrregular(count - n0, pUp, r, map);
            drawIrregular(count - n1, pRight, r, map);
            drawIrregular(count - n2, pDown, r, map);
            drawIrregular(count - n3, pLeft, r, map);
        }
    }

    /**
     * Same as above but with grass and flowers.
     */
    public void drawIrregularGrass(int count, Position p, RandomExtra r, Map map) {
        if (map.peek(p) == Tileset.FLOOR && count > 0) {

            if (r.nextIntInclusive(0, 100) <= 10) {
                map.placeTile(p, Tileset.randomColorFlower(r));
            } else {
                map.placeTile(p, Tileset.GRASS);
            }

            Position pUp = new Position(p.x(), p.y() + 1);
            Position pRight = new Position(p.x() + 1, p.y());
            Position pDown = new Position(p.x(), p.y() - 1);
            Position pLeft = new Position(p.x() - 1, p.y());

            int n0 = r.nextIntInclusive(1, 2);
            int n1 = r.nextIntInclusive(1, 2);
            int n2 = r.nextIntInclusive(1, 2);
            int n3 = r.nextIntInclusive(1, 2);

            drawIrregularGrass(count - n0, pUp, r, map);
            drawIrregularGrass(count - n1, pRight, r, map);
            drawIrregularGrass(count - n2, pDown, r, map);
            drawIrregularGrass(count - n3, pLeft, r, map);
        }
    }

    /**
     * Pick random location within the room.
     */
    public Position randomPositionInRoom(RandomExtra r, int buffer) {
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
