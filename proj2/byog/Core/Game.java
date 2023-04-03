package byog.Core;

import byog.Core.State.MainMenuState;
import byog.Core.State.State;
import byog.Core.Graphics.Renderer;

import edu.princeton.cs.introcs.StdDraw;
import java.io.*;

import static java.lang.System.exit;

public class Game implements Serializable {
    public static final int WIDTH = 50;
    public static final int HEIGHT = 40;
    public static final int HUD_HEIGHT = 4;

    // Instance variables
    private StringBuilder commands;
    private final Renderer renderer = new Renderer();
    private boolean quitGame = false;
    private State state;

    // Constructor
    public Game(String cmdString) {
        renderer.initialize(WIDTH, HEIGHT);

        // Additional instructions to enable cmd string parsing
        if (cmdString != null) {
            this.commands = new StringBuilder(cmdString.toLowerCase());
        }
        // Set initial state
        this.state = new MainMenuState(this);
    }

    public void start() throws InterruptedException {
        while (!quitGame) {
            // Get keyboard and mouse inputs
            char cmd = getNextCommand();
            double mouseX = StdDraw.mouseX();
            double mouseY = StdDraw.mouseY();

            state.nextFrame(cmd, mouseX, mouseY);
            renderer.render(state);
            Thread.sleep(15); // Todo: make sleep time variable (see Game loop pattern)
        }
        exit(0);
    }

    public void quit() {
        this.quitGame = true;
    }

    public void setContext(State state) {
        this.state = state;
    }

    /* Parse the next command (char) from the user. Works for both keyboard and string modes.
     * Returns '~' or exits with error code 0 upon no keyboard input or empty StringBuilder. */
    private char getNextCommand() {
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
    }
}