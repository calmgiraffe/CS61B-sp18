package byog.Core.State;

import byog.Core.Game;
import byog.Core.Graphics.Sprite;
import byog.Core.Level.Level;
import byog.Core.Map.Map;
import byog.RandomTools.RandomInclusive;

import java.io.Serializable;

public class PlayState implements State, Serializable {
    public static final int NUM_LEVELS = 25;

    private Game game;
    private final Level[] levels;
    private int currLevel = 1;
    private final RandomInclusive rand;
    private boolean colonPressed = false;

    public PlayState(Game game, Long seed, int width, int height) {
        this.game = game;
        this.rand = new RandomInclusive(seed);
        this.levels = new Level[NUM_LEVELS + 1];
        levels[currLevel] = new Level(width, height, rand); // generate first level
    }

    @Override
    public void setContext(Game game) {
        this.game = game;
    }

    @Override
    public void update() {
        // level[currlevel].update()
        // hud.update()

        // Hud needs to know:
        // mouse x and y position
        // the tiles underneath x and y
        // the current level

        char cmd = Game.controller.getNextCommand();


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
            Sprite currSprite = currMap.peek(newX, newY).getSprite();
            tileDesc = currSprite.description();
        } else {
            tileDesc = "";
        }
        tileStr.setText(tileDesc);

        /* Set centreStr based off flag */
        centreStr.setText(colonPressed ? "Press q to quit" : "");
    }

    private void nextLevel() {
        currLevel += 1;
    }
    private void prevLevel() {
        currLevel -= 1;
    }
}
