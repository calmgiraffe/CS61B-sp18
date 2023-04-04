package byog.Core.State;

import byog.Core.Graphics.FontSet;
import byog.Core.Game;
import byog.Core.Level.Level;
import byog.RandomTools.RandomInclusive;
import byog.Core.Graphics.Text;
import byog.Core.Graphics.Tile;

import java.awt.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PlayState implements State, Serializable {
    private class LevelManager implements Serializable {
        private final Level[] levels = new Level[NUM_LEVELS + 1];

        private LevelManager() {
            levels[curr] = new Level(width, height, rand);
        }
        public void nextFrame() {
            levels[curr].nextFrame();
        }
        public void incrementLevel() {
            curr += 1;
        }
        public void decrementLevel() {
            curr -= 1;
        }
        public Level getCurrentLevel() {
            return levels[curr];
        }
    }
    public static final int NUM_LEVELS = 25;
    public static final int GRASS_ODDS = 70;
    public static int ENABLE_FOV = 1; // Todo: need to implement this

    private Game game;
    private int curr = 1;
    private final int width;
    private final int height;
    private final RandomInclusive rand;
    private final LevelManager manager;

    // Flags
    private boolean reachedEndOfLevel = false;
    private boolean colonPressed = false;

    // HUD text
    private final Text tileStr = new Text("", Color.WHITE, FontSet.HUD, 0.03, 0.96, Text.Alignment.LEFT);
    private final Text centreStr = new Text("Press q to quit", Color.WHITE, FontSet.HUD, 0.5, 0.96, Text.Alignment.CENTRE);
    private final Text levelStr = new Text("Level " + curr, Color.WHITE, FontSet.HUD, 0.97, 0.96, Text.Alignment.RIGHT);
    private final List<Text> text = new ArrayList<>(
            Arrays.asList(tileStr, centreStr, levelStr)
    );

    public PlayState(Game game, Long seed, int width, int height) {
        this.game = game;
        this.width = width;
        this.height = height;
        this.rand = new RandomInclusive(seed);
        this.manager = new LevelManager();
    }

    @Override
    public void nextFrame(char cmd, double x, double y) {
        manager.nextFrame();

        if (cmd == ':') {
            colonPressed = true;
        } else if (cmd == 'q' && colonPressed) {
            colonPressed = false;
            game.setContext(new SaveState(game, this));
        } else if ("wasd".indexOf(cmd) != -1) { // wasd
            colonPressed = false;
            Level curr = manager.getCurrentLevel();
            curr.updateEntities(cmd); // entities move only when player moves
        }
        // Todo: raise flag if reached end of level

        // Tile description text
        int newX = Math.round((float) Math.floor(x)), newY = Math.round((float) Math.floor(y));
        String tileDesc;
        Level curr = manager.getCurrentLevel();
        if (curr.isValid(newX, newY)) {
            Tile currTile = curr.peek(newX, newY);
            tileDesc = currTile.description();
        } else {
            tileDesc = "";
        }
        tileStr.setText(tileDesc);

        // Center text
        centreStr.setText(colonPressed ? "Press q to quit" : "");
    }

    @Override
    public List<Text> getText() {
        return text;
    }

    @Override
    public Tile[][] getTilemap() {
        return manager.getCurrentLevel().getTilemap();
    }

    @Override
    public void setContext(Game game) {
        this.game = game;
    }
}
