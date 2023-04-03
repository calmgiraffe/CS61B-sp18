package byog.Core.Level;

import byog.Core.Game;
import byog.Core.Graphics.TETile;
import byog.Core.Graphics.Tileset;

import java.awt.*;

public abstract class Entity {
    protected int x;
    protected int y;
    protected TETile tile;
    protected Color color;
    protected int health = 100;
    protected Level level;
    protected TETile currTile; // Tile that is "below" the entity

    public Entity(int x, int y, Color color, Level level) {
        this.x = x;
        this.y = y;
        this.color = color;
        this.level = level;
        this.currTile = level.peek(x, y);
    }

    public void move(int dx, int dy) {
        int newX = x + dx, newY = y + dy;
        if (level.peek(newX, newY).character() != '#') {
            level.place(x, x, currTile);
            currTile = level.peek(newX, newY);
            level.place(newX, newY, tile);
            x = newX;
            y = newY;
        }
    }
}
