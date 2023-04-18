package byog.Core;

import byog.Core.Graphics.FontSet;
import byog.Core.Level.Text;

import java.awt.*;

public class HUD {
    /* HUD text */
    private final Text tileStr = new Text("", Color.WHITE, FontSet.HUD, 0.03, 0.96, Text.Alignment.LEFT);
    private final Text centreStr = new Text("Press q to quit", Color.WHITE, FontSet.HUD, 0.5, 0.96, Text.Alignment.CENTRE);
    private final Text levelStr = new Text("Level 1", Color.WHITE, FontSet.HUD, 0.97, 0.96, Text.Alignment.RIGHT);

    public HUD() {
        this.
    }
}
