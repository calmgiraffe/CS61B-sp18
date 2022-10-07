package byog.Core;

import java.io.Serializable;

/**
 * Class to represent an (x,y) coordinate.
 * a Position must be between 0 and width-1, 0 and height-1
 */
public class Position implements Serializable {

    /**
     * Position instance variables
     */
    private final int x;
    private final int y;

    /**
     * Position constructor
     */
    Position(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Given two positions a & b on an x-y plane,
     * calculate the absolute distance between the two.
     */
    public static int euclidean(Position a, Position b) {
        return (int) (Math.pow((a.x - b.x), 2) + Math.pow((a.y - b.y), 2));
    }

    /**
     * Given two 1D positions v1 & v2 on an x-y plane,
     * calculate the absolute distance between the two.
     */
    public static int euclidean(int v1, int v2, Map map) {
        Position a = map.oneDimensionalToPosition(v1);
        Position b = map.oneDimensionalToPosition(v2);
        return euclidean(a, b);
    }

    /**
     * Given two positions a & b on an x-y plane,
     * calculate the manhattan distance between the two.
     */
    public static int manhattan(Position a, Position b) {
        return Math.abs(a.x - b.x) + Math.abs(a.y - b.y);
    }

    /**
     * Given two 1D positions v1 & v2 on an x-y plane,
     * calculate the manhattan distance between the two.
     */
    public static int manhattan(int v1, int v2, Map map) {
        Position a = map.oneDimensionalToPosition(v1);
        Position b = map.oneDimensionalToPosition(v2);
        return manhattan(a, b);
    }

    /**
     * Given a Position p, returns a new position within a 5x5 box radius
     * surrounding the original Position.
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
}
