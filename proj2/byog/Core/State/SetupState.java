package byog.Core.State;

import byog.Core.Game;
import byog.Core.Graphics.FontSet;
import byog.Core.Level.Text;
import byog.Core.Visitable;
import byog.Core.Visitor;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static byog.Core.Graphics.Colors.rainbowColor;

public class SetupState implements State {
    private static final int MAX_LONG_LEN = 17;
    private static final int BACKSPACE = 8;

    private Game game;
    private int angle = 0;
    private final Text titleStr = new Text("ROGUELITE", Color.WHITE, FontSet.TITLE, 0.5, 0.65, Text.Alignment.CENTRE);
    private final Text submitStr = new Text("Start (S)", Color.WHITE, FontSet.OPTION, 0.5, 0.46, Text.Alignment.CENTRE);
    private final Text backStr = new Text("Back (B)", Color.WHITE, FontSet.OPTION, 0.5, 0.40, Text.Alignment.CENTRE);
    private final StringBuilder seed = new StringBuilder();
    private final Text seedStr = new Text("Seed: " + seed, Color.WHITE, FontSet.OPTION, 0.5, 0.34, Text.Alignment.CENTRE);
    private final List<Visitable> visitables = new ArrayList<>();

    public SetupState(Game game) {
        this.game = game;
        this.visitables.addAll(Arrays.asList(titleStr, submitStr, backStr, seedStr));
    }

    @Override
    public void setContext(Game game) {
        this.game = game;
    }

    @Override
    public void nextFrame(char cmd, double x, double y) {
        angle = (angle + 5) % 360;
        titleStr.setColor(rainbowColor(angle));

        if ((int) cmd == BACKSPACE && seed.length() > 0) { // delete from seed
            seed.deleteCharAt(seed.length() - 1);
            seedStr.setText("Seed: " + seed);
        }
        else if (seed.length() <= MAX_LONG_LEN && Character.isDigit(cmd)) { // add to seed
            seed.append(cmd);
            seedStr.setText("Seed: " + seed);
        }
        else if (cmd == 's' && seed.length() > 0) { // s = start
            Long seedValue = Long.parseLong(seed.toString());
            game.setContext(new PlayState(game, seedValue, Game.WIDTH, Game.HEIGHT - Game.HUD_HEIGHT));
        }
        else if (cmd == 'b') { // b = go back
            game.setContext(new MainMenuState(game));
        }
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
        for (Visitable obj : visitables) {
            obj.accept(visitor);
        }
    }
}