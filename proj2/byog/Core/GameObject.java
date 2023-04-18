package byog.Core;

import byog.Core.Graphics.Sprite;

public abstract class GameObject {
    private Position p;
    private Sprite sprite;

    public GameObject(Position p, Sprite sprite) {
        this.p = p;
        this.sprite = sprite;
    }

    public GameObject(double x, double y, Sprite sprite) {
        this.p = new Position(x, y);
        this.sprite = sprite;
    }

    public Sprite getSprite() { return sprite; }
    public double x() { return p.x(); }
    public double y() { return p.y(); }
}
