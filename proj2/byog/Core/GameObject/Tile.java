package byog.Core.GameObject;

import byog.Core.Graphics.Sprite;

public class Tile extends GameObject {
    Sprite sprite;

    public Tile(double x, double y, Sprite sprite) {
        super(x, y);
        this.sprite = sprite;
    }
    public int getX() { return (int) p.dx(); }
    public int getY() { return (int) p.dy(); }
    public Sprite getSprite() { return sprite; }
}
