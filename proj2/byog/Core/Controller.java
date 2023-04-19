package byog.Core;

import edu.princeton.cs.introcs.StdDraw;

import static java.lang.System.exit;

public class Controller {
    private StringBuilder commands;

    Controller(String cmdString) {
        if (cmdString != null) {
            this.commands = new StringBuilder(cmdString.toLowerCase());
        }
    }

    /* Parse the next command (char) from the user. Works for both keyboard and string modes.
     * Returns null char or exits with error code 0 upon no keyboard input or empty StringBuilder. */
    public char getNextCommand() {
        if (commands == null) {
            // Returns the pressed key, otherwise returns null
            if (StdDraw.hasNextKeyTyped()) {
                return StdDraw.nextKeyTyped();
            } else {
                return '\u0000'; // null character
            }
        } else {
            // Return first char in StringBuilder commands
            if (!commands.isEmpty()) {
                char next = commands.charAt(0);
                commands.deleteCharAt(0);
                return next;
            }
            exit(0);
        }
        return '\u0000'; // null character
    }

    /* Return x coordinate of cursor */
    public double getMouseX() { return StdDraw.mouseX(); }

    /* Return y coordinate of cursor */
    public double getMouseY() { return StdDraw.mouseY(); }
}
