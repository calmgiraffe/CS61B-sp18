package byog.Core;

/* Class to represent an (x,y) coordinate.
 * a Position must be between 0 and width-1, 0 and height-1 */
public class Position {
    /* Position instance variables */
    private final int x;
    private final int y;

    /* Position constructor */
    Position(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int X() {
        return x;
    }

    public int Y() {
        return y;
    }
}

