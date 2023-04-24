package byog.Core.State;

import byog.Core.Game;
import byog.Core.Level.Level;
import byog.RandomTools.RandomInclusive;

import java.io.Serializable;

public class PlayState implements State, Serializable {
    public static final int NUM_LEVELS = 25;

    private int width;
    private int height;
    private Game game;
    private final Level[] levels;
    private int currLevel = 1;
    private final HUD hud;
    private final RandomInclusive rand;

    public PlayState(Game game, Long seed, int width, int height) {
        this.width = width;
        this.height = height;
        this.game = game;
        this.rand = new RandomInclusive(seed);
        this.levels = new Level[NUM_LEVELS + 1];
        levels[currLevel] = new Level(width, height, rand); // generate first level
        this.hud = new HUD(this); // back reference to this state
    }

    @Override
    public void setContext(Game game) {
        this.game = game;
    }

    @Override
    public void update() {
        // Get user inputs
        int floorX = (int) Game.controller.getMouseX();
        int floorY = (int) Game.controller.getMouseY();
        char cmd = Game.controller.getNextCommand();

        // Update current level
        getCurrLevel().update(cmd);

        // Update HUD
        hud.update();
    }
    public void save() {
        game.setContext(new SaveState(game, this));
    }

    public Level getCurrLevel() {
        return levels[currLevel];
    }
    public void nextLevel() {
        currLevel += 1;
        if (levels[currLevel] == null) {
            levels[currLevel] = new Level(width, height, rand);
        }
    }
    public void prevLevel() {
        currLevel -= 1;
        if (levels[currLevel] == null) {
            levels[currLevel] = new Level(width, height, rand);
        }
    }
}
