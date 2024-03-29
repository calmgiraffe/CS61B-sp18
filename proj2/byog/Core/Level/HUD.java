package byog.Core.Level;

import byog.Core.Game;
import byog.Core.Graphics.FontSet;
import byog.Core.Graphics.Sprite;
import byog.Core.GameObject.Text;
import byog.Core.Level.Level;
import byog.Core.Level.Map.Map;
import byog.Core.State.PlayState;

import java.awt.*;

public class HUD {
    private final PlayState state;
    private boolean colonPressed;

    /* HUD text */
    private final Text tileStr = new Text("", Color.WHITE, FontSet.HUD, 0.03, 0.96, Text.Alignment.LEFT);
    private final Text centreStr = new Text("Press q to quit", Color.WHITE, FontSet.HUD, 0.5, 0.96, Text.Alignment.CENTRE);
    private final Text levelStr = new Text("Level 1", Color.WHITE, FontSet.HUD, 0.97, 0.96, Text.Alignment.RIGHT);

    public HUD(PlayState state) {
        this.state = state;
    }

    public void update() {
        // Get user inputs
        int mouseX = (int) Game.controller.getMouseX();
        int mouseY = (int) Game.controller.getMouseY();
        char cmd = Game.controller.getNextCommand();

        /* Change the object's state based off user input. Either a change in HUD or Level */
        if (cmd == ':') { // raise flag
            colonPressed = true;
        }
        else if (cmd == 'q' && colonPressed) { // save game if flag raised and condition met
            colonPressed = false;
            state.save();
        }
        else if ("wasd".indexOf(cmd) != -1) { // reset flag if player moved
            colonPressed = false;
        }
        /* Set tileStr text */
        String tileDesc;
        Level currLevel = state.getCurrLevel();
        Map currMap = currLevel.getMap();

        if (currMap.isValid(mouseX, mouseY)) {
            Sprite currSprite = currMap.peek(mouseX, mouseY).getSprite();
            tileDesc = currSprite.description();
        } else {
            tileDesc = "";
        }
        tileStr.setText(tileDesc);

        /* Set centreStr based off flag */
        centreStr.setText(colonPressed ? "Press q to quit" : "");
    }
}