package byog.Core.Level;

import byog.Core.GameObject;
import byog.Core.Graphics.Sprite;
import byog.Core.Position;

public class Entity extends GameObject {
    protected double xVelocity;
    protected double yVelocity;
    protected int health = 100;
    protected Level level;

    public Entity(Position p, Sprite sprite, Level level) {
        super(p.ix(), p.iy(), sprite);
        this.level = level;
    }

    public Entity(double x, double y, Sprite sprite, Level level) {
        super(x, y, sprite);
        this.level = level;
    }

    void move(char direction) {
        int dx = 0, dy = 0;

        switch (direction) {
            case 'w' -> dy = 1;
            case 'd' -> dx = 1;
            case 's' -> dy = -1;
            case 'a' -> dx = -1;
        }
        move(dx, dy);
    }

    public void updateVelocity(char direction) {
        switch (direction) {
            case 'w' -> yVelocity = 0.05;
            case 'd' -> xVelocity = 0.05;
            case 's' -> yVelocity = -0.05;
            case 'a' -> xVelocity = -0.05;
        }
    }
    /*
    public void move(int dx, int dy) {
        // Check map for collisions
        // Check map to see if at door
        // If at door, next level
        int newX = x + dx, newY = y + dy;
        if (level.peek(newX, newY).character() != '#') {
            level.place(x, y, currTile);
            currTile = level.peek(newX, newY);
            level.place(newX, newY, tile);
            x = newX;
            y = newY;
        }
    }
    */
}
