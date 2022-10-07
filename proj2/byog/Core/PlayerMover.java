package byog.Core;

import byog.TileEngine.TETile;
import byog.TileEngine.Tileset;

import java.io.Serializable;

public class PlayerMover implements Serializable {
    private static final String matchString = "wasd";
    private Map map;
    private Position pos;
    private Position newPos;
    private TETile last;

    public PlayerMover(Map map) {
        this.map = map;
    }

    public void setPosition(Position p) {
        pos = p;
        last = map.peek(p);
        map.placeTile(p, Tileset.PLAYER);
    }

    /**
     * Given a direction 'wasd', moves the player icon up/right/down/left.
     * Checks that the tile to be moved to is not a wall, in which case the character is moved.
     * Keeps a record of the tile that was previously placed so original tile is restored
     * once player moves out.
     * @param direction one of 'wasd', representing direction to move in
     */
    public void movePlayer(char direction) {
        if (inMatchString(direction)) {
            switch (direction) {
                case 'w' -> newPos = new Position(pos.x(), pos.y() + 1);
                case 'd' -> newPos = new Position(pos.x() + 1, pos.y());
                case 's' -> newPos = new Position(pos.x(), pos.y() - 1);
                case 'a' -> newPos = new Position(pos.x() - 1, pos.y());
            }
            if (map.peek(newPos).character() != '#') {
                map.placeTile(pos, last);
                last = map.peek(newPos);
                map.placeTile(newPos, Tileset.PLAYER);
                pos = newPos;
            }
        }
    }

    private boolean inMatchString(char direction) {
        for (int i = 0; i < matchString.length(); i++) {
            if (matchString.charAt(i) == direction) {
                return true;
            }
        }
        return false;
    }
}