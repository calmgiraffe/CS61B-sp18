package byog.Core;

import org.junit.Test;
import static org.junit.Assert.*;

/* Class to represent an (x,y) coordinate.
 * a Position must be between 0 and width-1, 0 and height-1 */
public class Position {

    /* Position instance variables */
    protected int x;
    protected int y;

    /* Position constructor */
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
}



