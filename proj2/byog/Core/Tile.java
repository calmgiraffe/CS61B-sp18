package byog.Core;

import byog.Core.Graphics.Sprite;

public class Tile extends GameObject {
    public Tile(Position p, Sprite sprite) {
        super(p, sprite);
    }

    public Tile(double x, double y, Sprite sprite) {
        super(x, y, sprite);
    }
}
