package byog.Core;

import byog.TileEngine.TERenderer;
import byog.TileEngine.TETile;

import byog.TileEngine.Tileset;
import edu.princeton.cs.introcs.StdDraw;
import java.awt.Color;
import java.awt.Font;

public class Game {
    private static final int WIDTH = 60;
    private static final int HEIGHT = 50;
    private static final Font title = new Font("Consolas", Font.BOLD, 40);
    private static final Font option = new Font("Consolas", Font.PLAIN, 28);
    private static final Font tileFont = new Font("Monaco", Font.BOLD, 14);
    private static final Font HUDFont = new Font("Bahnschrift", Font.PLAIN, 20);
    private final TERenderer ter = new TERenderer();
    private Map map;
    private int level = 1;
    private boolean quitGame = false;

    /**
     * Method used for playing a fresh game. The game should start from the main menu.
     * Loop until proper user input is obtained, then go to appropriate method.
     */
    public void playWithKeyboard() {
        ter.initialize(WIDTH, HEIGHT);

        StdDraw.setPenColor(Color.WHITE);
        StdDraw.clear(Color.BLACK);
        loadTitle();
        loadPrompts("New Game (N)", "Load Game (L)", "Quit (Q)");
        StdDraw.show();

        while (true) {
            char input = getUserChar();
            if (input == 'n') {
                inputSeedScreen();
                break;
            } else if (input == 'l') {
                loadGame();
                break;
            } else if (input == 'q') {
                quitGame = true;
                break;
            }
        }

        while (!quitGame) {
            StdDraw.clear(Color.BLACK);
            if (StdDraw.hasNextKeyTyped()) {
                char next = StdDraw.nextKeyTyped();
                map.movePlayer(next);
                if (next == ':' && getUserChar() == 'q') {
                    break;
                }
            }
            StdDraw.setFont(tileFont);
            ter.renderFrame(map.TETileMatrix());

            // From the retrieved x and y, determine which tile is ot that TETile[][] location
            long mouseX = Math.round(StdDraw.mouseX());
            long mouseY = Math.round(StdDraw.mouseY());

            StdDraw.setPenColor(Color.WHITE);
            StdDraw.setFont(HUDFont);

            String description = map.peek((int) mouseX, (int) mouseY).description();
            System.out.println(description);

            StdDraw.textLeft(2 * WIDTH / 60.0, 48 * HEIGHT / 50.0, description);
            StdDraw.show();
        }
        System.exit(0);
    }

    /**
     * Loops infinitely, reading user input and appending each character to variable "input".
     * Once the string's length reaches length n, the loop ends, and the method returns the string.
     * @return resulting string typed by user
     */
    private char getUserChar() {
        while (true) {
            if (!StdDraw.hasNextKeyTyped()) {
                continue;
            }
            return StdDraw.nextKeyTyped();
        }
    }

    private void loadTitle() {
        StdDraw.setFont(title);
        StdDraw.text(30 * WIDTH / 60.0, 26 * HEIGHT / 40.0, "CS61B: The Game");
    }

    private void loadPrompts(String s1, String s2, String s3) {
        StdDraw.setFont(option);
        StdDraw.text(30 * WIDTH / 60.0, 18 * HEIGHT / 40.0, s1);
        StdDraw.text(30 * WIDTH / 60.0, 16 * HEIGHT / 40.0, s2);
        StdDraw.text(30 * WIDTH / 60.0, 14 * HEIGHT / 40.0, s3);
    }

    /**
     * Displays the seed input screen; backspace to return to main menu,
     * s to submit the seed and generate a new map. Can only put in numbers for a seed.
     */
    private void inputSeedScreen() {
        StringBuilder seed = new StringBuilder();

        while (true) {
            StdDraw.clear(Color.BLACK);
            loadTitle();
            loadPrompts("(press s to submit)", "", "Seed: " + seed);
            StdDraw.show();

            char c = getUserChar();
            if ((int) c == 8 && seed.length() > 0) { // ASCII backspace
                seed.deleteCharAt(seed.length() - 1);
            } else if (c == 's' && seed.length() > 0) {
                break;
            } else if (seed.length() < 16 && Character.isDigit(c)) {
                seed.append(c);
            }
        }
        generateWorld(seed.toString());
    }

    /**
     * Load a game from the stored save.
     */
    private void loadGame() {
        // Todo
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
        ter.initialize(WIDTH, HEIGHT);
        input = input.toLowerCase();

        char mode = input.charAt(0);
        if (mode == 'n') {
            if (input.charAt(input.length() - 1) == 's') {
                String seed = input.substring(1, input.length() - 1);
                generateWorld(seed);
            }
        } else if (mode == 'l') {
            // Todo
            return null;

        } else if (mode == 'q') {
            System.exit(0);
        }
        return map.TETileMatrix();
    }

    /**
     * Given a seed String, creates new map object, makes this object generate a new TETile[][]
     * matrix, then renders the world, displaying this on the screen
     */
    private void generateWorld(String seed) {
        map = new Map(WIDTH, HEIGHT - 4, Long.parseLong(seed));
        map.generateWorld();
        StdDraw.setFont(tileFont);
        ter.renderFrame(map.TETileMatrix());
    }
}
