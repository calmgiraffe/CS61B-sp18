package byog.Core;

import byog.TileEngine.TETile;

/**
 * Class to represent an (x,y) coordinate.
 * a Position must be between 0 and width-1, 0 and height-1
 */
public class Position {

    /**
     * Position instance variables
     */
    private int x;
    private int y;

    /**
     * Position constructor
     */
    Position(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Given two positions a & b on an x-y plane, calculate the absolute distance between the two.
     */
    public static double calculateDistance(Position a, Position b) {
        return Math.sqrt(Math.pow((a.x - b.x), 2) + Math.pow((a.y - b.y), 2));
    }

    /**
     * Given a Position p, returns a new position within a 5x5 box radius surrounding the original Position.
     */
    public static Position randomPositionWithinRadius(Position p, RandomExtra r) {
        int lowerX = p.x() - 2;
        int upperX = p.x() + 2;
        int lowerY = p.y() - 2;
        int upperY = p.y() + 2;

        int x = r.nextIntInclusive(lowerX, upperX);
        int y = r.nextIntInclusive(lowerY, upperY);
        return new Position(x, y);
    }

    /**
     * Moves Position up one unit.
     */
    public void moveUp() {
        y += 1;
    }

    /**
     * Moves Position down one unit.
     */
    public void moveDown() {
        y -= 1;
    }

    /**
     * Moves Position right one unit.
     */
    public void moveRight() {
        x += 1;
    }

    /**
     * Moves Position left one unit.
     */
    public void moveLeft() {
        x -= 1;
    }

    /**
     * Returns the position's x value.
     */
    public int x() {
        return x;
    }

    /**
     * Returns the position's y value.
     */
    public int y() {
        return y;
    }

    /**
     * Returns true if positions are at same spot.
     */
    public boolean equals(Position p) {
        return (x == p.x && y == p.y);
    }

    /**
     * Returns true if positions horizontally aligned.
     */
    public boolean horizontallyAligned(Position p) {
        return (y == p.y);
    }

    /**
     * Returns true if positions vertically aligned.
     */
    public boolean verticallyAligned(Position p) {
        return (x == p.x);
    }

    /**
     * Returns true if the position is on the map edge, false otherwise.
     */
    public boolean onMapEdge(TETile[][] map) {
        return x == 0 || x == map.length - 1 || y == 0 || y == map[0].length - 1;
    }

}



