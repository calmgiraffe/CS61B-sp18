package byog.Core.Level.Map;

import java.io.Serializable;

/**
 * Class to represent an (x,y) coordinate.
 * a Position must be between 0 and width-1, 0 and height-1
 */
public class Position implements Serializable {

    /**
     * Position instance variables
     */
    protected final int x;
    protected final int y;

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
    public static double euclidean(Position a, Position b) {
        return Math.pow((a.x - b.x), 2) + Math.pow((a.y - b.y), 2);
    }

    /**
     * Given two 1D positions v1 & v2 on an x-y plane,
     * calculate the absolute distance between the two.
     */
    public static double euclidean(int v1, int v2, Map map) {
        Position a = map.toPosition(v1);
        Position b = map.toPosition(v2);
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
        Position a = map.toPosition(v1);
        Position b = map.toPosition(v2);
        return manhattan(a, b);
    }
}
