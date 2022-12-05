package byog.Core;

import byog.TileEngine.TETile;
import byog.TileEngine.Tileset;
import java.io.Serializable;
import java.util.ArrayList;

public class PlayerMover implements Serializable {
    private static final int FOVRANGE = 5;
    private final Map map;
    private final ArrayList<Position> fov;
    private Position pos;
    private Position newPos;
    private TETile prevTile;

    public PlayerMover(Map map) {
        this.map = map;
        this.fov = new ArrayList<>();
    }

    /**
     * Set the player icon to given Position p and update fov array
     * @param p player position
     */
    public void setPosition(Position p) {
        pos = p;
        prevTile = map.peek(p.x, p.y);
        map.placeTile(p.x, p.y, Tileset.PLAYER);
        updateFOV(FOVRANGE, p.x, p.y);
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
            case 'w' -> newPos = new Position(pos.x, pos.y + 1);
            case 'd' -> newPos = new Position(pos.x + 1, pos.y);
            case 's' -> newPos = new Position(pos.x, pos.y - 1);
            case 'a' -> newPos = new Position(pos.x - 1, pos.y);
        }
        if (map.peek(newPos.x, newPos.y).character() != '#') {
            map.placeTile(pos.x, pos.y, prevTile);
            prevTile = map.peek(newPos.x, newPos.y);

            map.placeTile(newPos.x, newPos.y, Tileset.PLAYER);
            pos = newPos;

            fov.clear();
            updateFOV(FOVRANGE, newPos.x, newPos.y);
        }
    }

    /**
     * Updates the list of points that make up the current FOV of the player.
     * Todo: change to Dijkstra's allowing diagonal
     */
    private void updateFOV(int count, int x, int y) {
        if (count < 0) {
            return;
        } else if (!map.isValid(x, y)) {
            return;
        }
        fov.add(new Position(x, y));
        if (!(map.peek(x, y).character() == '#')) {
            updateFOV(count - 1, x, y + 1);
            updateFOV(count - 1, x, y - 1);
            updateFOV(count - 1, x + 1, y);
            updateFOV(count - 1, x - 1, y);
        }
    }

    /**
     * Returns the list of points that comprise the FOV.
     */
    public ArrayList<Position> getFOV() {
        return fov;
    }
}