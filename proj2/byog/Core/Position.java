package byog.Core;

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
    public static int euclidean(Position a, Position b) {
        return (int) (Math.pow((a.x - b.x), 2) + Math.pow((a.y - b.y), 2));
    }

    public static int euclidean(int v1, int v2, Map map) {
        Position a = map.oneDimensionalToPosition(v1);
        Position b = map.oneDimensionalToPosition(v2);
        return euclidean(a, b);
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
