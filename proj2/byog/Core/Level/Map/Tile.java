package byog.Core.Level.Map;

import byog.Core.GameObject;
import byog.Core.Graphics.Sprite;

public class Tile extends GameObject {
    protected Tile(int x, int y, Sprite sprite) {
        super(x, y, sprite);
    }
    protected int getX() { return (int) p.dx(); }
    protected int getY() { return (int) p.dy(); }
}
