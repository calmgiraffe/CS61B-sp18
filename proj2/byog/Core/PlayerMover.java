package byog.Core;

import byog.TileEngine.TETile;
import byog.TileEngine.Tileset;

public class PlayerMover {
    private Map map;
    private Position playerPos;
    private Position newPos;
    private TETile last;

    public PlayerMover(Map map) {
        this.map = map;
    }

    public void setPosition(Position p) {
        playerPos = p;
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
        switch (direction) {
            case 'w' -> newPos = new Position(playerPos.x(), playerPos.y() + 1);
            case 'd' -> newPos = new Position(playerPos.x() + 1, playerPos.y());
            case 's' -> newPos = new Position(playerPos.x(), playerPos.y() - 1);
            case 'a' -> newPos = new Position(playerPos.x() - 1, playerPos.y());
        }
        if (map.peek(newPos).character() != '#') {
            map.placeTile(playerPos, last);
            last = map.peek(newPos);
            map.placeTile(newPos, Tileset.PLAYER);
            playerPos = newPos;
        }
    }
}