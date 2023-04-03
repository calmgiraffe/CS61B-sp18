package byog.Core.State;

import byog.Core.Graphics.FontSet;
import byog.Core.Game;
import byog.Core.Level.Level;
import byog.RandomTools.RandomInclusive;
import byog.Core.Graphics.Text;
import byog.Core.Graphics.TETile;

import java.awt.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PlayState implements State, Serializable {
    public static final int IRREGULAR_ODDS = 50;
    public static final int GRASS_ODDS = 70;
    protected static int ENABLE_FOV = 1;

    private final Game game;
    private final int width;
    private final int height;
    private final RandomInclusive rand;
    private final Level[] levels = new Level[26]; // 25 levels, the index corresponds to the level
    private int currLevel = 1; //
    private boolean colonPressed = false; // internal flag

    // HUD text
    private final Text tileStr = new Text("", Color.WHITE, FontSet.HUD, 0.04, 0.96);
    private final Text centreStr = new Text("Press q to quit", Color.WHITE, FontSet.HUD, 0.5, 0.96);
    private final Text levelStr = new Text("Level " + currLevel, Color.WHITE, FontSet.HUD, 0.96, 0.96);
    private final List<Text> text = new ArrayList<>(
            Arrays.asList(tileStr, centreStr, levelStr)
    );

    protected PlayState(Game game, Long seed, int width, int height) {
        this.game = game;
        this.width = width;
        this.height = height;
        this.rand = new RandomInclusive(seed);

        for (int i = currLevel - 1; i <= currLevel + 1; i++) {
            levels[i] = new Level(width, height, rand);
        }
    }

    /* For current window, increment by one frame */
    public void updateWindow() {
        for (int i = currLevel - 1; i <= currLevel + 1; i++) {
            if (levels[i] != null) {
                levels[i] = new Level(width, height, rand);
            }
            // levels[i].nextFrame(cmd)
        }
    }

    @Override
    public void nextFrame(char cmd, double x, double y) {
        Level curr = levels[currLevel];

        if (cmd == ':') {
            colonPressed = true;
        } else if (cmd == 'q' && colonPressed) {
            game.setContext(new SaveState(game, this));
        } else if ("wasd".indexOf(cmd) != -1) { // wasd
            colonPressed = false;
            this.updateWindow();
        }
        // Tile description text
        int newX = Math.round((float) Math.floor(x));
        int newY = Math.round((float) Math.floor(y));
        String tileDesc;
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
        // underlying TETile level
        // entities
        // player
        return levels[currLevel].getTilemap();
    }
}
