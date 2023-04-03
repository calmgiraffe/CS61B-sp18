package byog.Core;

/** This is the main entry point for the program. This class simply parses
 *  the command line inputs, and lets the byog.Core.Game class take over
 *  in either keyboard or input string mode.
 */
public class Main {
    public static void main(String[] args) throws InterruptedException {
        if (args.length > 1) {
            System.out.println("Can only have one argument - the input string");
            System.exit(0);
        }
        String cmdString = (args.length == 1) ? args[0] : null;
        Game game = new Game(cmdString);
        game.start();
    }
}
