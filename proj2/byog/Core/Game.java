package byog.Core;

import byog.TileEngine.TERenderer;
import byog.TileEngine.TETile;

import edu.princeton.cs.introcs.StdDraw;
import java.awt.Color;
import java.io.*;

import static byog.Core.GUI.*;

public class Game implements Serializable {
    public static RandomInclusive rand;
    public static Map map;
    public static int enableFOV = 1;
    private static final int BACKSPACE = 8;
    private static final TERenderer ter = new TERenderer();

    /* Private variables */
    private long seed;
    private StringBuilder commands;
    private MapController controller;
    /* temp private variables */
    private Map m;
    private RandomInclusive r;

    /* Public API */

    /**
     * Shows the main menu on the screen. Loops until proper input is received.
     * Method also used for autograding and testing the game code. The input string will be a series
     * of characters (for example, "n123sswwdasdassadwas", "n123sss:q", "lwww"). The game should
     * behave exactly as if the user typed these characters into the game after playing
     * playWithKeyboard. If the string ends in ":q", the same world should be returned as if the
     * string did not end with q. For example "n123sss" and "n123sss:q" should return the same
     * world. However, the behavior is slightly different. After playing with "n123sss:q", the game
     * should save, and thus if we then called playWithInputString with the string "l", we'd expect
     * to get the exact same world back again, since this corresponds to loading the saved game.
     */
    public void start(String cmdString) {
        if (cmdString != null) {
            // Additional instructions to enable cmd string parsing
            commands = new StringBuilder();
            commands.append(cmdString.toLowerCase());
        }
        ter.initialize(WIDTH, HEIGHT);

        while (true) {
            // Draw title screen
            StdDraw.clear(Color.BLACK);
            StdDraw.setPenColor(Color.WHITE);
            StdDraw.setFont(TITLE);
            StdDraw.text(WIDTH_CENTRE, HEADER, "CS61B: The Game");
            StdDraw.setFont(OPTION);
            StdDraw.text(WIDTH_CENTRE, ROW1, "New Game (N)");
            StdDraw.text(WIDTH_CENTRE, ROW2, "Load Game (L)");
            StdDraw.text(WIDTH_CENTRE, ROW3, "Quit (Q)");
            StdDraw.show();

            char next = getNextCommand();
            if (next == 'n') {
                inputSeed();
            } else if (next == 'l') {
                load();
            } else if (next == 'q') {
                exit(0);
            }
        }
    }

    /* Private internal methods */

    /** Displays the seed input screen; s to submit the seed and generate a new map.
     * Can only put in numbers for a seed. */
    private void inputSeed() {
        StringBuilder seed = new StringBuilder();
        while (true) {
            /* Show seed input screen & show current built seed string */
            StdDraw.clear(Color.BLACK);
            StdDraw.setPenColor(Color.WHITE);
            StdDraw.setFont(TITLE);
            StdDraw.text(WIDTH_CENTRE, HEADER, "CS61B: The Game");
            StdDraw.setFont(OPTION);
            StdDraw.text(WIDTH_CENTRE, ROW1, "s = submit, b = go back");
            StdDraw.text(WIDTH_CENTRE, ROW3, "Seed: " + seed);
            StdDraw.show();

            // Build seed string; max seed length is 18
            char c = getNextCommand();
            if ((int) c == BACKSPACE && seed.length() > 0) {
                seed.deleteCharAt(seed.length() - 1);

            } else if (seed.length() < 18 && Character.isDigit(c)) {
                seed.append(c);

            } else if (c == 's' && seed.length() > 0) { // s = start
                // Set the seed of Game and enter play() loop
                this.seed = Long.parseLong(seed.toString());
                Game.rand = new RandomInclusive(this.seed);
                Game.map = new Map(WIDTH, HEIGHT - HUD_HEIGHT);
                Game.map.generate();
                this.controller = new MapController();
                play();

            } else if (c == 'b') { // b = go back
                break;
            }
        }
    }

    /** Game loop, runs in real time */
    private void play() {
        boolean colonPressed = false;
        while (true) {
            StdDraw.clear(Color.BLACK);

            /* Get the next command (keyboard or string) and render updated map.
            If ':' pressed, raise flag
            Elif flag raised and 'q' is pressed, save and quit
            Reset flag if 'q' is not pressed */
            char next = getNextCommand();
            if (next == ':') {
                colonPressed = true;
            } else if (next == 'q' && colonPressed) {
                save();
            } else if ("wasd".indexOf(next) != -1) {
                colonPressed = false;
                controller.parseCommand(next); // controller manipulates Map
            }
            ter.renderFrame(map.getMap());

            /* HUD logic */
            double rawMouseX = StdDraw.mouseX(); // leave this separate - could potentially use in future
            double rawMouseY = StdDraw.mouseY();
            int x = Math.round((float) Math.floor(rawMouseX));
            int y = Math.round((float) Math.floor(rawMouseY));

            // If mouse within the game area, show the name of the current tile in the upper left
            StdDraw.setPenColor(Color.WHITE);
            StdDraw.setFont(HUD_FONT);
            if (map.isValid(x, y)) {
                String description = map.peek(x, y, enableFOV).description();
                StdDraw.textLeft(0.04 * WIDTH, 0.96 * HEIGHT, description);
            }
            // Determine which text to show in HUD depending on flag value
            String centerText = (colonPressed) ? "Press q to quit" : "Seed: " + seed;
            StdDraw.text(WIDTH_CENTRE, 0.96 * HEIGHT, centerText);
            StdDraw.textRight(0.96 * WIDTH, 0.96 * HEIGHT, "Level " + map.level);
            StdDraw.show();
        }
    }

    /** Serializes the current Game object and saves to txt file. Exits with error code (0 or 1). */
    private void save() {
        try {
            m = map; // save to temp variables
            r = rand;
            FileOutputStream fileOut = new FileOutputStream("savefile.txt");
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(this);
            out.close();
            fileOut.close();
            exit(0);
        } catch (IOException i)  {
            i.printStackTrace();
            exit(1);
        }
    }

    /** Load a game from stored save file, restores previous state, and starts play() loop.
     * Exits with error code 1 upon unsuccessful load. */
    private void load() {
        Game g;
        try {
            FileInputStream fileIn = new FileInputStream("savefile.txt");
            ObjectInputStream in = new ObjectInputStream(fileIn);
            g = (Game) in.readObject();
            in.close();
            fileIn.close();
            this.seed = g.seed; // need these or seed will not show
            this.controller = g.controller;
            rand = g.r; // restore from temp variables
            map = g.m;
            play();
        } catch (IOException i) {
            i.printStackTrace();
            exit(1);
        } catch (ClassNotFoundException c) {
            System.out.println("Game class not found");
            c.printStackTrace();
            exit(1);
        }
    }

    /** Parse the next command (char) from the user. Works for both keyboard and string modes.
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

    /** Quit and close the game. Wrapper for System.exit() */
    private void exit(int status) {
        if (commands != null) {
            System.out.println(TETile.toString(map.getMap()));
        }
        // This is an example of a fault class exception
        // Gives control to kernel which then aborts program
        System.exit(status);
    }
}