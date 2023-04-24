package byog.Core.State;

import byog.Core.Game;
import byog.Core.GameObject.Entity;
import byog.Core.Graphics.Sprite;
import byog.Core.Level.HUD;
import byog.Core.Level.Level;
import byog.Core.Position;
import byog.RandomTools.RandomInclusive;

import java.io.Serializable;

public class PlayState implements State, Serializable {
    public static final int NUM_LEVELS = 25;

    private Game game;
    private final int gameHeight, gameWidth;
    private final Level[] levels;
    private int currLevel = 1;
    private final HUD hud;

    private final RandomInclusive rand;

    public PlayState(Game game, Long seed) {
        this.game = game;
        this.gameWidth = Game.WIDTH;
        this.gameHeight = Game.HEIGHT - Game.HUD_HEIGHT;
        this.rand = new RandomInclusive(seed);
        this.levels = new Level[NUM_LEVELS + 1];
        this.hud = new HUD(this);

        // Generate the first level
        levels[currLevel] = new Level(Game.WIDTH, Game.HEIGHT - Game.HUD_HEIGHT, rand);
        getCurrLevel().initializePlayer();
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
            levels[currLevel] = new Level(gameWidth, gameHeight, rand);
        }
        levels[currLevel].setPlayer(player);
    }
    public void prevLevel() {
        Entity player = getCurrLevel().getPlayer();

        currLevel -= 1;
        if (levels[currLevel] == null) {
            levels[currLevel] = new Level(gameWidth, gameHeight, rand);
        }
        levels[currLevel].setPlayer(player);
    }
}
