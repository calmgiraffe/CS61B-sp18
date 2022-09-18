package byog.Core;

/* Class to represent an (x,y) coordinate.
 * a Position must be between 0 and width-1, 0 and height-1 */
public class Position {

    /* Position instance variables */
    protected final int x;
    protected final int y;

    /* Position constructor */
    Position(int x, int y) {
        this.x = x;
        this.y = y;
    }
}

