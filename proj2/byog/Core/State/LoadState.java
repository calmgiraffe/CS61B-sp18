package byog.Core.State;

import byog.Core.Game;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

import static java.lang.System.exit;

/* Load a game from stored save file, restores previous state, and starts play() loop.
 * Exits with error code 1 upon unsuccessful load. */
public class LoadState implements State {
    private Game game;

    public LoadState(Game game) {
        this.game = game;
    }

    @Override
    public void setContext(Game game) {
        this.game = game;
    }

    @Override
    public void update(char cmd, double x, double y) {
        try {
            // Load the serialized save file, convert to valid PlayState object
            FileInputStream fileIn = new FileInputStream("savefile.txt");
            ObjectInputStream in = new ObjectInputStream(fileIn);
            PlayState savedGame = (PlayState) in.readObject();
            in.close();
            fileIn.close();
            game.setContext(savedGame); // change Game's context
            savedGame.setContext(game); // change PlayState's context to Game
        } catch (IOException i) {
            i.printStackTrace();
            exit(1);
        } catch (ClassNotFoundException c) {
            System.out.println("Game class not found");
            c.printStackTrace();
            exit(1);
        }
    }
}
