package byog.Core.State;

import byog.Core.Game;
import byog.Core.Graphics.Text;
import byog.Core.Graphics.TETile;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class SetupState implements State {

    private static final int MAX_LONG_LEN = 17;
    private static final int BACKSPACE = 8;
    private static final Font TITLE = new Font("Consolas", Font.BOLD, 40);
    private static final Font OPTION = new Font("Consolas", Font.PLAIN, 28);

    private final Game game;
    private final Text titleStr = new Text("ROGUELITE", Color.WHITE, TITLE, 0.5, 0.65);
    private final Text submitStr = new Text("Start (S)", Color.WHITE, OPTION, 0.5, 0.46);
    private final Text backStr = new Text("Back (B)", Color.WHITE, OPTION, 0.5, 0.40);
    private final StringBuilder seed = new StringBuilder();
    private final Text seedStr = new Text("Seed: " + seed, Color.WHITE, OPTION, 0.5, 0.34);
    private final List<Text> text = new ArrayList<>();

    SetupState(Game game) {
        this.game = game;
        text.add(titleStr);
        text.add(submitStr);
        text.add(backStr);
        text.add(seedStr);
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
    public void nextFrame(char cmd, double x, double y) {
        if ((int) cmd == BACKSPACE && seed.length() > 0) {
            seed.deleteCharAt(seed.length() - 1);
            seedStr.setText("Seed: " + seed);

        } else if (seed.length() <= MAX_LONG_LEN && Character.isDigit(cmd)) {
            seed.append(cmd);
            seedStr.setText("Seed: " + seed);

        } else if (cmd == 's' && seed.length() > 0) { // s = start
            Long seedValue = Long.parseLong(seed.toString());
            game.setContext(new PlayState(game, seedValue, Game.WIDTH,
                    Game.HEIGHT - Game.HUD_HEIGHT));

        } else if (cmd == 'b') { // b = go back
            game.setContext(new MainMenuState(game));
        }
    }
}