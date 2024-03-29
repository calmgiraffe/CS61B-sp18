package byog.Core.State;

import byog.Core.Game;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import static java.lang.System.exit;

/* Serializes the current Game object and saves to txt file. Exits with error code (0 or 1). */
public class SaveState implements State, Serializable {
    private Game game;
    private final PlayState save;

    public SaveState(Game game, PlayState save) {
        this.game = game;
        this.save = save;
    }

    @Override
    public void update() {
        try {
            // Serialize PlayState to a file
            FileOutputStream fileOut = new FileOutputStream("savefile.txt");
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(save);
            out.close();
            fileOut.close();
            game.quit();
        } catch (IOException i)  {
            i.printStackTrace();
            exit(1);
        }
    }

    @Override
    public void setContext(Game game) {
        this.game = game;
    }
}
