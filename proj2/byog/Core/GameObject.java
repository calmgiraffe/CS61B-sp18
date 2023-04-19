package byog.Core;

import byog.Core.Graphics.Sprite;

public abstract class GameObject {
    protected Position p;
    protected Sprite sprite;

    public GameObject(double x, double y, Sprite sprite) {
        this.p = new Position(x, y);
        this.sprite = sprite;
    }

    public Sprite getSprite() { return sprite; }
    public double getDx() { return p.dx(); }
    public double getDy() { return p.dy(); }
}
