package byog.Core;

import byog.TileEngine.TERenderer;
import byog.TileEngine.TETile;

import byog.TileEngine.Tileset;
import edu.princeton.cs.introcs.StdDraw;
import java.awt.Color;
import java.io.*;

import static byog.Core.GUI.*;
import static java.lang.System.exit;

public class Game implements Serializable {
    private static final int BACKSPACE = 8;
    private static final TERenderer ter = new TERenderer();
    static boolean enableFOV = false;

    /* Instance variables */
    private Map map;
    private long seed;
    private int level = 1;
    private StringBuilder commands;

    /** Game loop, runs in real time */
    private void playGame() {
        // Generate a new world (TETile[][] matrix), then render this world
        map = new Map(WIDTH, HEIGHT - HUDHEIGHT, this.seed);
        map.generateWorld();
        ter.renderFrame(map.getMap());

        boolean colonPressed = false;
        while (true) {
            // Reset screen to black
            StdDraw.clear(Color.BLACK);

            /* Get the next command (keyboard or string) and render updated map
            If ':' pressed, raise flag
            Else if flag raised and q is pressed, save and quit
            Reset flag if q is not pressed */
            char next = getNextCommand();
            if (next == ':') {
                colonPressed = true;
            } else if (next == 'q' && colonPressed) {
                saveGame();
                exit(0);
            } else if ("wasd".indexOf(next) != -1) {
                colonPressed = false;
                map.updatePlayer(next);
            }
            // If character moves to open door, generate next level
            TETile currentTile = map.playerMover.prevTile();
            if (currentTile.character() == '▢') {
                map.clear();
                map.generateWorld();
                level += 1;
            }
            ter.renderFrame(map.getMap());

            /* HUD logic */
            // Build and show the HUD
            double rawMouseX = StdDraw.mouseX();
            double rawMouseY = StdDraw.mouseY();
            int x = Math.round((float) Math.floor(rawMouseX));
            int y = Math.round((float) Math.floor(rawMouseY));
            StdDraw.setPenColor(Color.WHITE);
            StdDraw.setFont(HUDFONT);

            // If mouse within the game area, show the name of the current tile in the upper left
            if (x >= 0 && x < map.width && y >= 0 && y < map.height) {
                String description = map.peek(x, y).description();
                StdDraw.textLeft(0.04 * WIDTH, 0.96 * HEIGHT, description);
            }
            // Determine which text to show depending on flag value
            String centerText;
            if (colonPressed) {
                centerText = "Press q to quit";
            } else {
                centerText = "Seed: " + seed;
            }
            StdDraw.text(WIDTHCENTRE, 0.96 * HEIGHT, centerText);
            StdDraw.textRight(0.96 * WIDTH, 0.96 * HEIGHT, "Level " + level);
            StdDraw.show();
        }
    }

    /** Shows the main menu on the screen. Loops until proper input is received */
    public void mainMenu() {
        ter.initialize(WIDTH, HEIGHT);

        while (true) {
            // Draw title screen
            StdDraw.clear(Color.BLACK);
            StdDraw.setPenColor(Color.WHITE);
            StdDraw.setFont(TITLE);
            StdDraw.text(WIDTHCENTRE, HEADER, "CS61B: The Game");
            StdDraw.setFont(OPTION);
            StdDraw.text(WIDTHCENTRE, ROW1, "New Game (N)");
            StdDraw.text(WIDTHCENTRE, ROW2, "Load Game (L)");
            StdDraw.text(WIDTHCENTRE, ROW3, "Quit (Q)");
            StdDraw.show();

            char next = getNextCommand();
            if (next == 'n') {
                inputSeed();
            } else if (next == 'l') {
                loadGame();
                playGame();
            } else if (next == 'q') {
                exit(0);
            }
        }
    }

    /**
     * Displays the seed input screen; s to submit the seed and generate a new map.
     * Can only put in numbers for a seed.
     */
    private void inputSeed() {
        StringBuilder seed = new StringBuilder();

        while (true) {
            /* Show seed input screen & show current built seed string */
            StdDraw.clear(Color.BLACK);
            StdDraw.setPenColor(Color.WHITE);
            StdDraw.setFont(TITLE);
            StdDraw.text(WIDTHCENTRE, HEADER, "CS61B: The Game");
            StdDraw.setFont(OPTION);
            StdDraw.text(WIDTHCENTRE, ROW1, "(s to submit, b to go back)");
            StdDraw.text(WIDTHCENTRE, ROW3, "Seed: " + seed);
            StdDraw.show();

            char c = getNextCommand();
            if ((int) c == BACKSPACE && seed.length() > 0) {
                seed.deleteCharAt(seed.length() - 1);
            } else if (seed.length() < 18 && Character.isDigit(c)) { // max seed length is 18
                seed.append(c);
            } else if (c == 's' && seed.length() > 0) { // start
                this.seed = Long.parseLong(seed.toString());
                playGame();
            } else if (c == 'b') { // go back
                break;
            }
        }
    }

    /** Method used for autograding and testing the game code. The input string will be a series
     * of characters (for example, "n123sswwdasdassadwas", "n123sss:q", "lwww". The game should
     * behave exactly as if the user typed these characters into the game after playing
     * playWithKeyboard. If the string ends in ":q", the same world should be returned as if the
     * string did not end with q. For example "n123sss" and "n123sss:q" should return the same
     * world. However, the behavior is slightly different. After playing with "n123sss:q", the game
     * should save, and thus if we then called playWithInputString with the string "l", we'd expect
     * to get the exact same world back again, since this corresponds to loading the saved game.
     * @param input the input string to feed to your program
     * @return the 2D TETile[][] representing the state of the world */
    public TETile[][] playWithInputString(String input) {
        /*
         * Fill out this method to run the game using the input passed in,
         * and return a 2D tile representation of the world that would have been
         * drawn if the same inputs had been given to playWithKeyboard().
         */
        commands = new StringBuilder();
        commands.append(input.toLowerCase());

        mainMenu();
        return map.getMap();
    }

    /** Serializes the current Game object and saves to txt file. */
    private void saveGame() {
        // todo: fix saving (does not save state correctly)
        try {
            FileOutputStream fileOut = new FileOutputStream("savefile.txt");
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(this);
            out.close();
            fileOut.close();
        } catch (IOException i)  {
            i.printStackTrace();
            exit(1);
        }
    }

    /** Load a game from stored save file, then sets map and level to the saved objects values. */
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
            this.level = g.level;
        } catch (IOException i) {
            i.printStackTrace();
        } catch (ClassNotFoundException c) {
            System.out.println("Game class not found");
            c.printStackTrace();
        }
    }

    /** Parse the next command (char) from the user. Works for both keyboard and string modes.
     *  Returns '~' on no keyboard input or no more commands. */
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
            // Likewise, need some arbitrary value to return
            return '~';
        }
    }
}