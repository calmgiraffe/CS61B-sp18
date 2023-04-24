package byog.Core.Level;

import byog.Core.Level.Map.Map;
import byog.RandomTools.RandomInclusive;

import java.io.Serializable;

public class Level implements Serializable {
    /* Instance variables */
    private final int width;
    private final int height;
    private final RandomInclusive rand;
    private final Map map;
    private Entity player;

    public Level(int width, int height, RandomInclusive rand) {
        this.width = width;
        this.height = height;
        this.rand = rand;
        this.map = new Map(width, height, rand); // generate the underlying map (grid of tiles)
        this.player = new Player();
    }

    public Map getMap() {
        return map;
    }

    public void update(char cmd) {
        if ("wasd".indexOf(cmd) != -1) { // reset flag if player moved
            player.move(cmd);

        }
    }

    private void nextFrame() {

    }
}