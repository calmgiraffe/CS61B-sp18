package byog.Core.State;

import byog.Core.Graphics.FontSet;
import byog.Core.Game;
import byog.Core.Graphics.Text;
import byog.Core.Graphics.TETile;

import java.awt.*;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;

public class MainMenuState implements State {
    private Game game;
    private final Text titleStr = new Text("ROGUELITE", Color.WHITE, FontSet.TITLE, 0.50, 0.65, Text.Alignment.CENTRE);
    private final Text newStr = new Text("New Game (N)", Color.WHITE, FontSet.OPTION, 0.50, 0.46, Text.Alignment.CENTRE);
    private final Text loadStr = new Text("Load Game (L)", Color.WHITE, FontSet.OPTION, 0.50, 0.40, Text.Alignment.CENTRE);
    private final Text quitStr = new Text("Quit Game (Q)", Color.WHITE, FontSet.OPTION, 0.50, 0.34, Text.Alignment.CENTRE);
    private final List<Text> text = new ArrayList<>(
            Arrays.asList(titleStr, newStr, loadStr, quitStr)
    );

    public MainMenuState(Game game) {
        this.game = game;
    }

    @Override
    public void nextFrame(char cmd, double x, double y) { // Todo: animate the title screen
        if (cmd == 'n') {
            game.setContext(new SetupState(game));
        } else if (cmd == 'l') {
            game.setContext(new LoadState(game));
        } else if (cmd == 'q') {
            game.quit();
        }
    }

    @Override
    public List<Text> getText() {
        return text;
    }

    @Override
    public TETile[][] getTilemap() {
        return null;
    }

    @Override
    public void setContext(Game game) {
        this.game = game;
    }
}
