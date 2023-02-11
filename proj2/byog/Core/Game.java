package byog.Core;

import byog.TileEngine.TERenderer;
import byog.TileEngine.TETile;

import edu.princeton.cs.introcs.StdDraw;
import java.awt.Color;
import java.io.*;

import static byog.Core.GUI.*;

public class Game implements Serializable {
    protected static RandInclusive rand;
    private static final int BACKSPACE = 8;
    private static final TERenderer ter = new TERenderer();
    static boolean enableFOV = true;

    /** Public/protected variables */
    protected Map map;
    /* Private variables */
    private long seed;
    private StringBuilder commands;
    private PlayerMover controller;

    /* Public API */

    /**
     * Shows the main menu on the screen. Loops until proper input is received.
     * Method also used for autograding and testing the game code. The input string will be a series
     * of characters (for example, "n123sswwdasdassadwas", "n123sss:q", "lwww". The game should
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
                loadGame();
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
            StdDraw.text(WIDTH_CENTRE, ROW1, "(s to submit, b to go back)");
            StdDraw.text(WIDTH_CENTRE, ROW3, "Seed: " + seed);
            StdDraw.show();

            char c = getNextCommand();
            if ((int) c == BACKSPACE && seed.length() > 0) {
                seed.deleteCharAt(seed.length() - 1);

            } else if (seed.length() < 18 && Character.isDigit(c)) { // max seed length is 18
                seed.append(c);

            } else if (c == 's' && seed.length() > 0) { // s = start
                // Set the seed of Game and enter play() loop
                this.seed = Long.parseLong(seed.toString());
                Game.rand = new RandInclusive(this.seed);
                playGame();

            } else if (c == 'b') { // go back
                break;
            }
        }
    }

    /** Game loop, runs in real time */
    private void playGame() {
        this.map = new Map(WIDTH, HEIGHT - HUD_HEIGHT);
        this.controller = new PlayerMover(map);

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
                saveGame();
            } else if ("wasd".indexOf(next) != -1) {
                colonPressed = false;
                controller.parseCommand(next);
            }
            ter.renderFrame(map.getMap());

            /* HUD logic */
            // Build and show the HUD
            double rawMouseX = StdDraw.mouseX();
            double rawMouseY = StdDraw.mouseY();
            int x = Math.round((float) Math.floor(rawMouseX));
            int y = Math.round((float) Math.floor(rawMouseY));
            StdDraw.setPenColor(Color.WHITE);
            StdDraw.setFont(HUD_FONT);

            // If mouse within the game area, show the name of the current tile in the upper left
            if (x >= 0 && x < map.width && y >= 0 && y < map.height) {
                String description = map.peek(x, y).description();
                StdDraw.textLeft(0.04 * WIDTH, 0.96 * HEIGHT, description);
            }
            // Determine which text to show in HUD depending on flag value
            String centerText;
            if (colonPressed) {
                centerText = "Press q to quit";
            } else {
                centerText = "Seed: " + seed;
            }
            StdDraw.text(WIDTH_CENTRE, 0.96 * HEIGHT, centerText);
            StdDraw.textRight(0.96 * WIDTH, 0.96 * HEIGHT, "Level " + map.level);
            StdDraw.show();
        }
    }

    /** Serializes the current Game object and saves to txt file. Exits with error code (0 or 1). */
    private void saveGame() {
        try {
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

    /** Load a game from stored save file, then sets map and level to the saved objects values.
     * Exits with error code 1 upon unsuccessful load. */
    private void loadGame() {
        Game g;
        try {
            FileInputStream fileIn = new FileInputStream("savefile.txt");
            ObjectInputStream in = new ObjectInputStream(fileIn);
            g = (Game) in.readObject();
            in.close();
            fileIn.close();
            this.map = g.map;
            this.seed = g.seed;
            playGame();
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
        System.exit(status);
    }
}