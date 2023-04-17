package byog.Core.State;

import byog.Core.Graphics.FontSet;
import byog.Core.Game;
import byog.Core.Level.Level;
import byog.Core.Renderable;
import byog.RandomTools.RandomInclusive;
import byog.Core.Graphics.Text;
import byog.Core.Graphics.Tile;

import java.awt.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PlayState implements State, Serializable {
    /* Helper class for levels */
    private class LevelManager implements Serializable {
        // Todo: need to update a window of levels rather than just one
        // Can maybe use Observer method for this
        public static final int NUM_LEVELS = 25;
        private final Level[] levels = new Level[NUM_LEVELS + 1];
        private int curr = 1;

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
        public Level getCurrLevel() {
            return levels[curr];
        }
    }

    // Instance variables
    private Game game;
    private final int width;
    private final int height;
    private final RandomInclusive rand;
    private final LevelManager levelManager = new LevelManager();
    private final ArrayList<Renderable> data;

    // Flags
    private boolean reachedEndOfLevel = false;
    private boolean colonPressed = false;

    // HUD text
    // Todo: move into separate HUD class later
    private final Text tileStr = new Text("", Color.WHITE, FontSet.HUD, 0.03, 0.96, Text.Alignment.LEFT);
    private final Text centreStr = new Text("Press q to quit", Color.WHITE, FontSet.HUD, 0.5, 0.96, Text.Alignment.CENTRE);
    private final Text levelStr = new Text("Level 1", Color.WHITE, FontSet.HUD, 0.97, 0.96, Text.Alignment.RIGHT);

    public PlayState(Game game, Long seed, int width, int height) {
        this.game = game;
        this.width = width;
        this.height = height;
        this.rand = new RandomInclusive(seed);
        this.data = new ArrayList<>(Arrays.asList(tileStr, centreStr, levelStr, levelManager.getCurrLevel()));
    }

    @Override
    public void nextFrame(char cmd, double mouseX, double mouseY) {
        /* Set the next frame of window */
        levelManager.nextFrame();

        /* Change the object's state based off user input. Either a change in HUD or Level */
        if (cmd == ':') {
            colonPressed = true;
        }
        else if (cmd == 'q' && colonPressed) {
            colonPressed = false;
            game.setContext(new SaveState(game, this));
        }
        else if ("wasd".indexOf(cmd) != -1) { // moving player
            colonPressed = false;
            Level curr = levelManager.getCurrLevel();
            curr.updateEntities(cmd); // entities move only when player moves
        }
        // Todo: raise flag if reached end of level
        // if

        /* Set tileStr based off current mouse position */
        int newX = Math.round((float) Math.floor(mouseX));
        int newY = Math.round((float) Math.floor(mouseY));
        String tileDesc;
        Level curr = levelManager.getCurrLevel();
        if (curr.isValid(newX, newY)) {
            Tile currTile = curr.peek(newX, newY);
            tileDesc = currTile.description();
        } else {
            tileDesc = "";
        }
        tileStr.setText(tileDesc);

        /* Set centreStr based off flag */
        centreStr.setText(colonPressed ? "Press q to quit" : "");
    }

    @Override
    public void setContext(Game game) {
        this.game = game;
    }

    @Override
    public List<Renderable> getData() {
        return new ArrayList<>(Arrays.asList(tileStr, centreStr, levelStr, levelManager.getCurrLevel()));
    }
}
