package byog.Core;

import byog.TileEngine.TERenderer;
import byog.TileEngine.TETile;

import edu.princeton.cs.introcs.StdDraw;
import java.awt.Color;
import java.io.*;

import static byog.Core.GUI.*;

public class Game implements Serializable {
    private static final int BACKSPACE = 8;
    private static final TERenderer ter = new TERenderer();

    private Map map;
    private long seed;
    private int level = 1;
    private StringBuilder commands;
    private boolean enableFOV = true;
    private boolean quitMenu = false;
    private boolean quitGame = false;
    private boolean colonPressed = false;

    /**
     * Method used for playing a fresh game using the keyboard.
     */
    public void playWithKeyboard() {
        mainMenu();
        System.exit(0);
    }

    /**
     * Shows the main menu on the screen. Loops until proper input is received
     */
    private void mainMenu() {
        ter.initialize(WIDTH, HEIGHT);

        while (!quitMenu) {
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
                quitMenu = true;
                loadGame();
            } else if (next == 'q') {
                quitMenu = true;
                quitGame = true;
                break;
            }
        }
        playGame();
    }

    /**
     * Displays the seed input screen; s to submit the seed and generate a new map.
     * Can only put in numbers for a seed.
     */
    private void inputSeed() {
        StringBuilder seed = new StringBuilder();

        while (true) {
            // Show seed input screen
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
            } else if (seed.length() < 16 && Character.isDigit(c)) {
                seed.append(c);
            } else if (c == 's' && seed.length() > 0) {
                generateWorld(seed.toString());
                quitMenu = true;
                break;
            } else if (c == 'b') {
                break;
            }
        }
    }

    /**
     * Game loop, runs in real time
     */
    public void playGame() {
        while (!quitGame) {
            // reset everything to black
            StdDraw.clear(Color.BLACK);

            // Get the next command (keyboard or string) and render updated map
            // If ':' pressed, raise flag
            // Else if flag raised and q is pressed, save and quit
            // Reset flag if q is not pressed
            char next = getNextCommand();
            if (next == ':') {
                colonPressed = true;
            } else if (next == 'q' && colonPressed) {
                saveGame();
                quitGame = true;
            } else if (next == 'w' || next == 'a' || next == 's' || next == 'd') {
                colonPressed = false;
                map.updatePlayer(next);
            }
            ter.renderFrame(map.getMap());

            // Build and show the HUD
            double rawMouseX = StdDraw.mouseX();
            double rawMouseY = StdDraw.mouseY();
            long x = Math.round(Math.floor(rawMouseX));
            long y = Math.round(Math.floor(rawMouseY));
            showHUD((int) x, (int) y);
        }
    }

    /**
     * Method used for autograding and testing the game code. The input string will be a series
     * of characters (for example, "n123sswwdasdassadwas", "n123sss:q", "lwww". The game should
     * behave exactly as if the user typed these characters into the game after playing
     * playWithKeyboard. If the string ends in ":q", the same world should be returned as if the
     * string did not end with q. For example "n123sss" and "n123sss:q" should return the same
     * world. However, the behavior is slightly different. After playing with "n123sss:q", the game
     * should save, and thus if we then called playWithInputString with the string "l", we'd expect
     * to get the exact same world back again, since this corresponds to loading the saved game.
     * @param input the input string to feed to your program
     * @return the 2D TETile[][] representing the state of the world
     */
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

    /**
     * Given an x and y coordinate corresponding to TETile[x][y], displays the description of
     * the hovered tile in the HUD as well as the current level.
     */
    private void showHUD(int x, int y) {
        // If mouse within the game area, show the name of the current tile in the upper left
        if (x >= 0 && x < map.width && y >= 0 && y < map.height) {
            StdDraw.setPenColor(Color.WHITE);
            StdDraw.setFont(HUDFONT);
            String description = map.peek(x, y).description();
            StdDraw.textLeft(2 * WIDTH / 60.0, 48 * HEIGHT / 50.0, description);
        }
        StdDraw.setPenColor(Color.WHITE);
        StdDraw.setFont(HUDFONT);
        String centerText;

        // Determine which text to show depending on flag value
        if (colonPressed) {
            centerText = "Press q to quit";
        } else {
            centerText = "Seed: " + seed;
        }
        StdDraw.text(WIDTHCENTRE, 48 * HEIGHT / 50.0, centerText);
        StdDraw.textRight(58 * WIDTH / 60.0, 48 * HEIGHT / 50.0, "Level " + level);
        StdDraw.show();
    }

    /**
     * Serializes the current Game object and saves to txt file.
     */
    private void saveGame() {
        try {
            FileOutputStream fileOut = new FileOutputStream("./byog/savefile.txt");
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(this);
            out.close();
            fileOut.close();
        } catch (IOException i)  {
            i.printStackTrace();
        }
    }

    /**
     * Load a game from the stored save file, then sets map and level to the saved objects values.
     */
    private void loadGame() {
        Game g;
        try {
            FileInputStream fileIn = new FileInputStream("./byog/savefile.txt");
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

    /**
     * Parse the next command (char) from the user. Works for both keyboard and string modes.
     */
    public char getNextCommand() {
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
            // Need some arbitrary value to return
            return '~';
        }
    }

    /**
     * Given a seed String, creates new map object, makes this object generate a new TETile[][]
     * matrix, then renders the world, displaying this on the screen
     */
    private void generateWorld(String seed) {
        this.seed = Long.parseLong(seed);
        map = new Map(WIDTH, HEIGHT - HUDHEIGHT, this.seed, enableFOV);
        map.generateWorld();
        ter.renderFrame(map.getMap());
    }
}
