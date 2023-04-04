package byog.Core.Level;

import byog.Core.Graphics.Tile;

public abstract class Entity {
    protected int x;
    protected int y;
    protected int health = 100;
    protected Level level;
    protected Tile tile;
    protected Tile currTile; // Tile that is "below" the entity

    public Entity(int x, int y, Level level, Tile tile) {
        this.x = x;
        this.y = y;
        this.level = level;
        this.tile = tile;
        this.currTile = level.peek(x, y);
        level.place(x, y, tile);
    }

    public void move(int dx, int dy) {
        int newX = x + dx, newY = y + dy;
        if (level.peek(newX, newY).character() != '#') {
            level.place(x, y, currTile);
            currTile = level.peek(newX, newY);
            level.place(newX, newY, tile);
            x = newX;
            y = newY;
        }
    }
}
