package byog.Core.State;

import byog.Core.Graphics.FontSet;
import byog.Core.Game;
import byog.Core.Level.Level;
import byog.Core.Level.Player;
import byog.RandomTools.RandomInclusive;
import byog.Core.Graphics.Text;
import byog.Core.Graphics.TETile;

import java.awt.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PlayState implements State, Serializable {
    private class LevelManager {
        private Level[] levels = new Level[NUM_LEVELS + 1];
        private int curr;

        public void updateWindow() {
            for (int i = curr - 1; i <= curr + 1; i++) {
                if (levels[i] == null) {
                    levels[i] = new Level(width, height, rand);
                    levels[i].generate();
                }
                levels[i].update();
            }
        }
        public void incrementLevel() {
            curr += 1;
        }
        public void decrementLevel() {
            curr -= 1;
        }
        public int getCurr() {
            return curr;
        }
        public Level getCurrLevel() {
            return levels[curr];
        }
        public Player getPlayer() {
            return levels[curr].getPlayer();
        }
    }

    public static final int NUM_LEVELS = 25;
    public static final int GRASS_ODDS = 70;
    public static int ENABLE_FOV = 1; // Todo: need to implement this

    private Game game;
    private final int width;
    private final int height;
    private final RandomInclusive rand;
    private Player player;
    private final LevelManager manager = new LevelManager();
    private boolean colonPressed = false; // internal flag

    // HUD text
    private final Text tileStr = new Text("", Color.WHITE, FontSet.HUD, 0.03, 0.96, Text.Alignment.LEFT);
    private final Text centreStr = new Text("Press q to quit", Color.WHITE, FontSet.HUD, 0.5, 0.96, Text.Alignment.CENTRE);
    private final Text levelStr = new Text("Level " + manager.curr, Color.WHITE, FontSet.HUD, 0.97, 0.96, Text.Alignment.RIGHT);
    private final List<Text> text = new ArrayList<>(
            Arrays.asList(tileStr, centreStr, levelStr)
    );

    public PlayState(Game game, Long seed, int width, int height) {
        this.game = game;
        this.width = width;
        this.height = height;
        this.rand = new RandomInclusive(seed);
        this.player = manager.getPlayer();
    }

    @Override
    public void nextFrame(char cmd, double x, double y) {
        manager.updateWindow();

        if (cmd == ':') {
            colonPressed = true;
        } else if (cmd == 'q' && colonPressed) {
            colonPressed = false;
            game.setContext(new SaveState(game, this));
        } else if ("wasd".indexOf(cmd) != -1) { // wasd
            colonPressed = false;
            player.move(cmd);
            // player.move(cmd)
            // levels.moveEntities()
        }
        // Tile description text
        int newX = Math.round((float) Math.floor(x)), newY = Math.round((float) Math.floor(y));
        String tileDesc;
        Level curr = manager.getCurrLevel();
        if (curr.isValid(newX, newY)) {
            TETile currTile = curr.peek(newX, newY);
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
    public TETile[][] getTilemap() {
        // underlying TETile level, entities, player?
        return manager.getCurrLevel().getTilemap();
    }

    @Override
    public void setContext(Game game) {
        this.game = game;
    }
}
