package byog.Core;

import edu.princeton.cs.introcs.StdDraw;

import static java.lang.System.exit;

public class InputComponent {
    public char getKeyboardCommand() {
        if (commands == null) {
            // Returns the pressed key, otherwise returns ~
            if (StdDraw.hasNextKeyTyped()) {
                return StdDraw.nextKeyTyped();
            } else {
                return '~';
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
        return '~';
        cmd = getNextCommand();
        mouseX = StdDraw.mouseX();
        mouseY = StdDraw.mouseY();
    }
}
