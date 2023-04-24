package byog.Core.State;

import byog.Core.Game;
import byog.Core.Level.Entity;
import byog.Core.Level.Level;
import byog.Core.Position;
import byog.RandomTools.RandomInclusive;

import java.io.Serializable;

public class PlayState implements State, Serializable {
    public static final int NUM_LEVELS = 25;

    private Game game;
    private int width, height;
    private final Level[] levels;
    private int currLevel = 1;
    private final RandomInclusive rand;
    private final HUD hud;

    public PlayState(Game game, Long seed, int width, int height) {
        this.game = game;
        this.width = width;
        this.height = height;
        this.rand = new RandomInclusive(seed);
        this.levels = new Level[NUM_LEVELS + 1];
        this.hud = new HUD(this);

        // Generate the first level
        levels[currLevel] = new Level(width, height, rand);
    }

    @Override
    public void setContext(Game game) {
        this.game = game;
    }

    @Override
    public void update() {
        getCurrLevel().update();
        hud.update();
    }

    public void save() {
        game.setContext(new SaveState(game, this));
    }

    public Level getCurrLevel() {
        return levels[currLevel];
    }
    public void nextLevel() {
        Entity player = getCurrLevel().getPlayer();

        currLevel += 1;
        if (levels[currLevel] == null) {
            levels[currLevel] = new Level(width, height, rand);
        }
        levels[currLevel].setPlayer(player);
    }
    public void prevLevel() {
        Entity player = getCurrLevel().getPlayer();

        currLevel -= 1;
        if (levels[currLevel] == null) {
            levels[currLevel] = new Level(width, height, rand);
        }
        levels[currLevel].setPlayer(player);
    }
}
