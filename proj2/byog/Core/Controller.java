package byog.Core;

import edu.princeton.cs.introcs.StdDraw;

public class Controller {

    /* Parse the next command (char) from the user. Works for both keyboard and string modes.
     * Returns null char or exits with error code 0 upon no keyboard input or empty StringBuilder. */
    public char getNextCommand() {
        if (StdDraw.hasNextKeyTyped()) {
            return StdDraw.nextKeyTyped();
        } else {
            return '\u0000'; // null character
        }
    }

    /* Return x coordinate of cursor */
    public double getMouseX() { return StdDraw.mouseX(); }

    /* Return y coordinate of cursor */
    public double getMouseY() { return StdDraw.mouseY(); }
}
