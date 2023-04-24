package byog.Core.GameObject;

import byog.Core.Position;

public abstract class GameObject {
    protected Position p;

    public GameObject(Position p) {
        this.p = p;
    }
    public GameObject(double x, double y) {
        this.p = new Position(x, y);
    }
    public double getDx() { return p.dx(); }
    public double getDy() { return p.dy(); }
    public void setX(double x) { p.setX(x); }
    public void setY(double y) { p.setY(y); }
}
