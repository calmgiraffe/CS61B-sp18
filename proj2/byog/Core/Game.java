package byog.Core;

import byog.TileEngine.TERenderer;
import byog.TileEngine.TETile;

public class Game {
    private static final int WIDTH = 60;
    private static final int HEIGHT = 40;
    protected static RandomExtra random;
    private final TERenderer ter = new TERenderer();

    /** Method used for playing a fresh game. The game should start from the main menu. */
    public void playWithKeyboard() {
        // to do
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
        // Fill out this method to run the game using the input passed in,
        // and return a 2D tile representation of the world that would have been
        // drawn if the same inputs had been given to playWithKeyboard().
        input = input.toLowerCase();
        char mode = input.charAt(0);

        // example: N3412S should generate a world with seed 3412
        if (mode == 'n') {
            String seed = input.substring(1);
            Game.random = new RandomExtra(Long.parseLong(seed));

            Map world = new Map(WIDTH, HEIGHT);
            world.generateRooms();

            ter.initialize(WIDTH, HEIGHT);
            ter.renderFrame(Map.getMap());

        } else if (mode == 'l') {
            // load
        } else if (mode == 'q') {
            // quit
        }
        return Map.getMap();
    }
}

