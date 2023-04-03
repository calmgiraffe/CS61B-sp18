package byog.Core.Level;

import java.awt.*;

public abstract class Entity {
    protected int x;
    protected int y;
    protected char character;
    protected Color color;

    public Entity(int x, int y, char character, Color color) {
        this.x = x;
        this.y = y;
        this.character = character;
        this.color = color;
    }

    public abstract void move(int dx, int dy);
}
