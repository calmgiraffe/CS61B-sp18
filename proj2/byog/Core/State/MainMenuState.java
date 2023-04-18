package byog.Core.State;

import byog.Core.Graphics.FontSet;
import byog.Core.Game;
import byog.Core.Level.Text;
import byog.Core.Visitable;
import byog.Core.Visitor;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static byog.Core.Graphics.Colors.*;

public class MainMenuState implements State {
    private int angle = 0;

    private Game game;
    private final Text titleStr = new Text("ROGUELITE", Color.BLACK, FontSet.TITLE, 0.50, 0.65, Text.Alignment.CENTRE);
    private final Text newStr = new Text("New Game (N)", Color.WHITE, FontSet.OPTION, 0.50, 0.46, Text.Alignment.CENTRE);
    private final Text loadStr = new Text("Load Game (L)", Color.WHITE, FontSet.OPTION, 0.50, 0.40, Text.Alignment.CENTRE);
    private final Text quitStr = new Text("Quit Game (Q)", Color.WHITE, FontSet.OPTION, 0.50, 0.34, Text.Alignment.CENTRE);
    private final List<Visitable> visitables = new ArrayList<>();

    public MainMenuState(Game game) {
        this.game = game;
        this.visitables.addAll(Arrays.asList(titleStr, newStr, loadStr, quitStr));
    }

    @Override
    public void setContext(Game game) {
        this.game = game;
    }

    @Override
    public void nextFrame(char cmd, double x, double y) {
        angle = (angle + 5) % 360;
        titleStr.setColor(rainbowColor(angle));

        if (cmd == 'n') {
            game.setContext(new SetupState(game));
        } else if (cmd == 'l') {
            game.setContext(new LoadState(game));
        } else if (cmd == 'q') {
            game.quit();
        }
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    @Override
    public List<Visitable> getVisitables() {
        return visitables;
    }
}
