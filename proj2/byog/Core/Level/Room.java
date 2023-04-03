package byog.Core.Level;

import byog.Core.Game;
import byog.Core.State.PlayState;
import byog.Core.Graphics.TETile;
import byog.Core.Graphics.Tileset;
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
    private final TETile floorType = Tileset.FLOOR;

    Room(Position lowerLeft, Position upperRight) {
        this.lowerLeft = lowerLeft;
        this.upperRight = upperRight;
    }

    /** Given another room, draws a fully drawn hallway with floor and wall tiles between them on
     * the level using the astar algorithm.
     * <p>
     * Helper methods used by this method are drawPath() and drawWalls(). */
    public void astar(Room room) {
        Position a = this.randomPosition(1);
        Position b = room.randomPosition(1);
        int start = Game.level.to1D(a.x, a.y);
        int target = Game.level.to1D(b.x, b.y);

        PriorityQueue<Node> fringe = new PriorityQueue<>(getDistanceComparator());
        int[] edgeTo = new int[PlayState.WIDTH * PlayState.HEIGHT];
        int[] distTo = new int[PlayState.WIDTH * PlayState.HEIGHT];
        Arrays.fill(distTo, Integer.MAX_VALUE);
        distTo[start] = 0;

        // Initially, add start to PQ. Then loop until target found
        fringe.add(new Node(start, 0));
        boolean targetFound = false;
        while (fringe.size() > 0 && !targetFound) {
            int p = fringe.remove().position;

            for (int q : Game.level.adjacent(p)) {
                // If new distance < old distance, update distTo and edgeTo
                // Add neighbour node q to PQ, factoring in heuristic
                if (distTo[p] + 1 < distTo[q]) {
                    distTo[q] = distTo[p] + 1;
                    edgeTo[q] = p;
                    Node n = new Node(q, distTo[q] + Position.manhattan(q, target));
                    fringe.add(n);
                }
                if (q == target) {
                    targetFound = true;
                    this.drawPath(edgeTo, start, q);
                }
            }
        }
    }

    /* Given a 1D start & end coordinate, by following the child-parent relationships
     * given by the edgeTo array, draws the astar path from start to end. */
    private void drawPath(int[] edgeTo, int start, int end) {
        int curr = edgeTo[end];
        int prev = end;
        char direction = '~';

        while (curr != start) {
            if (curr == prev + PlayState.WIDTH) {
                direction = 'U';

            } else if (curr == prev + 1) {
                direction = 'R';

            } else if (curr == prev - PlayState.WIDTH) {
                direction = 'D';

            } else if (curr == prev - 1) {
                direction = 'L';
            }
            Position currentPos = Game.level.toPosition(curr);
            Game.level.place(currentPos.x, currentPos.y, floorType);
            this.drawWalls(currentPos, direction);
            prev = curr;
            curr = edgeTo[curr];
        }
    }

    /* Draws the three wall tiles and floor tile that must be placed when added a new floor tile
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
                if (Game.level.peek(x + i, y) != floorType) {
                    Game.level.place(x + i, y, Tileset.colorVariantWall(PlayState.rand));
                }
            }
        } else if (way == 'L' || way == 'R') {
            for (int i = 0; i < 3; i += 1) {
                if (Game.level.peek(x, y + i) != floorType) {
                    Game.level.place(x, y + i, Tileset.colorVariantWall(PlayState.rand));
                }
            }
        }
    }




    /** From a position, draws an irregular room by making new positions at the top, right, bottom,
     * and left of said position, then applying the recursive method on those four new positions.
     * Depending on the location and the count, either a FLOOR or WALL tile is drawn. */
    public void drawIrregular(int count, int x, int y) {
        if (!Game.level.isValid(x, y)) {
            return;
        }
        // Base case: count is <= 0 and able to place a tile on NOTHING
        if (count <= 0) {
            if (Game.level.peek(x, y) == Tileset.NOTHING) {
                Game.level.place(x, y, Tileset.colorVariantWall(PlayState.rand));
            }
        } else {
            // Todo: refactor onEdge into base case
            boolean onEdge = (x == 0) || (x == PlayState.WIDTH - 1) || (y == 0) || (y == PlayState.HEIGHT - 1);
            if (onEdge) {
                Game.level.place(x, y, Tileset.colorVariantWall(PlayState.rand));
            } else {
                Game.level.place(x, y, floorType);
            }
            drawIrregular(count - PlayState.rand.nextInt(1, 3), x, y + 1);
            drawIrregular(count - PlayState.rand.nextInt(1, 3), x, y - 1);
            drawIrregular(count - PlayState.rand.nextInt(1, 3), x - 1, y);
            drawIrregular(count - PlayState.rand.nextInt(1, 3), x + 1, y);
        }
    }

    /** Same as above but with grass and flowers. */
    public void drawIrregularGrass(int count, int x, int y) {
        if (!Game.level.isValid(x, y)) {
            return;
        }
        if (Game.level.peek(x, y) == floorType && count > 0) {
            if (PlayState.rand.nextInt(100) <= 10) {
                Game.level.place(x, y, Tileset.randomFlower(PlayState.rand));
            } else {
                Game.level.place(x, y, Tileset.colorVariantGrass(PlayState.rand));
            }
            int up = count - PlayState.rand.nextInt(1, 2);
            int down = count - PlayState.rand.nextInt(1, 2);
            int left = count - PlayState.rand.nextInt(1, 2);
            int right = count - PlayState.rand.nextInt(1, 2);

            drawIrregularGrass(up, x, y + 1);
            drawIrregularGrass(down, x, y - 1);
            drawIrregularGrass(left, x - 1, y);
            drawIrregularGrass(right, x + 1, y);
        }
    }

    /* Draws the rectangular room that is associated with this particular Partition onto level. */
    public void drawRoom() {
        int startX = lowerLeft.x;
        int startY = lowerLeft.y;
        int endX = upperRight.x;
        int endY = upperRight.y;

        // Draw top and bottom walls
        for (int x = startX; x <= endX; x++) {
            if (Game.level.peek(x, startY, 0) == Tileset.NOTHING) {
                Game.level.place(x, startY, Tileset.colorVariantWall(PlayState.rand));
            }
            if (Game.level.peek(x, endY) == Tileset.NOTHING) {
                Game.level.place(x, endY, Tileset.colorVariantWall(PlayState.rand));
            }
        }
        // Draw left and right walls
        for (int y = startY; y <= endY; y++) {
            if (Game.level.peek(startX, y) == Tileset.NOTHING) {
                Game.level.place(startX, y, Tileset.colorVariantWall(PlayState.rand));
            }
            if (Game.level.peek(endX, y) == Tileset.NOTHING) {
                Game.level.place(endX, y, Tileset.colorVariantWall(PlayState.rand));
            }
        }
        // Draw interior
        for (int x = startX + 1; x <= endX - 1; x++) {
            for (int y = startY + 1; y <= endY - 1; y++) {
                Game.level.place(x, y, floorType);
            }
        }
    }

    /** Pick random location within the room, int buffer indicating the margin from the room edge. */
    public Position randomPosition(int buffer) {
        int xLower = lowerLeft.x + buffer;
        int xUpper = upperRight.x - buffer;
        int yLower = lowerLeft.y + buffer;
        int yUpper = upperRight.y - buffer;

        return new Position(
                PlayState.rand.nextInt(xLower, xUpper),
                PlayState.rand.nextInt(yLower, yUpper));
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
