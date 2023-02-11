package byog.Core;

import byog.TileEngine.TETile;
import byog.TileEngine.Tileset;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Comparator;
import java.util.PriorityQueue;

public class Room implements Serializable {

    /* Private class to represent a vertex-distance pair in the pQueue. */
    private static class Node {
        int position;
        int distance;

        Node(int position, int distance) {
            this.position = position;
            this.distance = distance;
        }
    }

    /* Instance variables */
    private final Position lowerLeft;
    private final Position upperRight;
    private final TETile floorType;

    Room(Position lowerLeft, Position upperRight) {
        this.lowerLeft = lowerLeft;
        this.upperRight = upperRight;
        this.floorType = Tileset.FLOOR;
    }

    /*
     * Draws the three wall tiles and floor tile that must be placed when added a new floor tile
     * to a hallway. The overall method works by adding a 'room' of area 1 on a preexisting room. */
    private void drawWalls(Position p, char way) {
        int x = p.x;
        int y = p.y;

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
                if (Game.map.peek(x + i, y) != floorType) {
                    Game.map.placeTile(x + i, y, Tileset.colorVariantWall(Game.rand));
                }
            }
        } else if (way == 'L' || way == 'R') {
            for (int i = 0; i < 3; i += 1) {
                if (Game.map.peek(x, y + i) != floorType) {
                    Game.map.placeTile(x, y + i, Tileset.colorVariantWall(Game.rand));
                }
            }
        }
    }

    /*
     * Given a 1D start & end coordinate, following the child-parent relationships
     * given by the edgeTo array, draws the astar path from start to end. */
    private void drawPath(int[] edgeTo, int start, int end) {
        int curr = edgeTo[end];
        int prev = end;
        char direction = '~';

        while (curr != start) {
            if (curr == prev + Game.map.width) {
                direction = 'U';

            } else if (curr == prev + 1) {
                direction = 'R';

            } else if (curr == prev - Game.map.width) {
                direction = 'D';

            } else if (curr == prev - 1) {
                direction = 'L';
            }
            Position currentPos = Game.map.oneDToPos(curr);
            Game.map.placeTile(currentPos.x, currentPos.y, floorType);
            drawWalls(currentPos, direction);
            prev = curr;
            curr = edgeTo[curr];
        }
    }

    /**
     * Given another room, draws a path (one with hallways bounding floor path) between them on map
     * using the a* algorithm.
     */
    public void astar(Room room) {
        int start = Game.map.posToOneD(this.randomPosition(1));
        int target = Game.map.posToOneD(room.randomPosition(1));

        PriorityQueue<Node> fringe = new PriorityQueue<>(getDistanceComparator());
        int[] edgeTo = new int[Game.map.width * Game.map.height];
        int[] distTo = new int[Game.map.width * Game.map.height];
        Arrays.fill(distTo, Integer.MAX_VALUE);
        distTo[start] = 0;

        // Initially, add start to PQ. Then loop until target found
        fringe.add(new Node(start, 0));
        boolean targetFound = false;
        while (fringe.size() > 0 && !targetFound) {
            int p = fringe.remove().position;

            for (int q : Game.map.adjacent(p)) {
                // If new distance < old distance, update distTo and edgeTo
                // Add neighbour node q to PQ, factoring in heuristic
                if (distTo[p] + 1 < distTo[q]) {
                    distTo[q] = distTo[p] + 1;
                    edgeTo[q] = p;
                    Node n = new Node(q, distTo[q] + Position.manhattan(q, target, Game.map));
                    fringe.add(n);
                }
                if (q == target) {
                    targetFound = true;
                    drawPath(edgeTo, start, q);
                }
            }
        }
    }

    /**
     * Draws the rectangular room that is associated with this particular Partition onto map.
     */
    public void drawRoom() {
        int startX = lowerLeft.x;
        int startY = lowerLeft.y;
        int endX = upperRight.x;
        int endY = upperRight.y;

        // Draw top and bottom walls
        for (int x = startX; x <= endX; x++) {
            if (Game.map.peek(x, startY) == Tileset.NOTHING) {
                Game.map.placeTile(x, startY, Tileset.colorVariantWall(Game.rand));
            }
            if (Game.map.peek(x, endY) == Tileset.NOTHING) {
                Game.map.placeTile(x, endY, Tileset.colorVariantWall(Game.rand));
            }
        }
        // Draw left and right walls
        for (int y = startY; y <= endY; y++) {
            if (Game.map.peek(startX, y) == Tileset.NOTHING) {
                Game.map.placeTile(startX, y, Tileset.colorVariantWall(Game.rand));
            }
            if (Game.map.peek(endX, y) == Tileset.NOTHING) {
                Game.map.placeTile(endX, y, Tileset.colorVariantWall(Game.rand));
            }
        }
        // Draw interior
        for (int x = startX + 1; x <= endX - 1; x++) {
            for (int y = startY + 1; y <= endY - 1; y++) {
                Game.map.placeTile(x, y, floorType);
            }
        }
    }

    /**
     * From a position, draws an irregular room by making new positions at the top, right, bottom,
     * and left of said position, then applying the recursive method on those four new positions.
     * Depending on the location and the count, either a FLOOR or WALL tile is drawn.
     */
    public void drawIrregular(int count, int x, int y) {
        // Base case: count is 0 and able to place a tile on NOTHING
        if (!Game.map.isValid(x, y)) {
            return;
        }
        if (count <= 0) {
            if (Game.map.peek(x, y) == Tileset.NOTHING) {
                Game.map.placeTile(x, y, Tileset.colorVariantWall(Game.rand));
            }
        } else {
            boolean onEdge = (x == 0) || (x == Game.map.width - 1) || (y == 0) || (y == Game.map.height - 1);
            if (onEdge) {
                Game.map.placeTile(x, y, Tileset.colorVariantWall(Game.rand));
            } else {
                Game.map.placeTile(x, y, floorType);
            }
            int up = count - Game.rand.nextIntInclusive(1, 3);
            int down = count - Game.rand.nextIntInclusive(1, 3);
            int left = count - Game.rand.nextIntInclusive(1, 3);
            int right = count - Game.rand.nextIntInclusive(1, 3);

            drawIrregular(up, x, y + 1);
            drawIrregular(down, x, y - 1);
            drawIrregular(left, x - 1, y);
            drawIrregular(right, x + 1, y);
        }
    }

    /**
     * Same as above but with grass and flowers.
     */
    public void drawIrregularGrass(int count, int x, int y) {
        if (!Game.map.isValid(x, y)) {
            return;
        }
        if (Game.map.peek(x, y) == floorType && count > 0) {
            if (Game.rand.nextIntInclusive(100) <= 10) {
                Game.map.placeTile(x, y, Tileset.randomColorFlower(Game.rand));
            } else {
                Game.map.placeTile(x, y, Tileset.colorVariantGrass(Game.rand));
            }
            int up = count - Game.rand.nextIntInclusive(1, 2);
            int down = count - Game.rand.nextIntInclusive(1, 2);
            int left = count - Game.rand.nextIntInclusive(1, 2);
            int right = count - Game.rand.nextIntInclusive(1, 2);

            drawIrregularGrass(up, x, y + 1);
            drawIrregularGrass(down, x, y - 1);
            drawIrregularGrass(left, x - 1, y);
            drawIrregularGrass(right, x + 1, y);
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
                Game.rand.nextIntInclusive(xLower, xUpper),
                Game.rand.nextIntInclusive(yLower, yUpper));
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
