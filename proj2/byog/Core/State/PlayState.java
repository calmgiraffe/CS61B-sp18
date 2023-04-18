package byog.Core.State;

import byog.Core.Graphics.FontSet;
import byog.Core.Game;
import byog.Core.Graphics.Sprite;
import byog.Core.Level.Level;
import byog.Core.Map.Map;
import byog.RandomTools.RandomInclusive;
import byog.Core.Level.Text;

import java.awt.*;
import java.io.Serializable;

public class PlayState implements State, Serializable {
    // Static variables
    public static final int NUM_LEVELS = 25;

    // Instance variables
    private Game game;
    private final Level[] levels;
    private int currLevel = 1;
    private final RandomInclusive rand;

    // Flags
    private boolean reachedEndOfLevel = false;
    private boolean colonPressed = false;

    /* HUD text */
    private final Text tileStr = new Text("", Color.WHITE, FontSet.HUD, 0.03, 0.96, Text.Alignment.LEFT);
    private final Text centreStr = new Text("Press q to quit", Color.WHITE, FontSet.HUD, 0.5, 0.96, Text.Alignment.CENTRE);
    private final Text levelStr = new Text("Level 1", Color.WHITE, FontSet.HUD, 0.97, 0.96, Text.Alignment.RIGHT);

    public PlayState(Game game, Long seed, int width, int height) {
        this.game = game;
        this.rand = new RandomInclusive(seed);
        this.levels = new Level[NUM_LEVELS + 1];
        levels[currLevel] = new Level(width, height, rand); // generate first level
    }

    private void nextLevel() {
        currLevel += 1;
    }
    private void prevLevel() {
        currLevel -= 1;
    }

    @Override
    public void setContext(Game game) {
        this.game = game;
    }

    @Override
    public void nextFrame(char cmd, double mouseX, double mouseY) {
        /* Set the next frame of window */
        levels[currLevel].nextFrame();

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
            levels[currLevel].updateEntities(cmd); // entities move only when player moves
        }
        // Todo: handling case when player reaches end of level

        /* Set tileStr based off current mouse position */
        int newX = Math.round((float) Math.floor(mouseX));
        int newY = Math.round((float) Math.floor(mouseY));
        String tileDesc;
        Map currMap = levels[currLevel].getMap();
        if (currMap.isValid(newX, newY)) {
            Sprite currSprite = currMap.peek(newX, newY);
            tileDesc = currSprite.description();
        } else {
            tileDesc = "";
        }
        tileStr.setText(tileDesc);

        /* Set centreStr based off flag */
        centreStr.setText(colonPressed ? "Press q to quit" : "");
    }
}
