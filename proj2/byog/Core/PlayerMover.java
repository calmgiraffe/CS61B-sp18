package byog.Core;

import byog.TileEngine.TETile;
import byog.TileEngine.Tileset;
import java.io.Serializable;
import java.util.ArrayList;

public class PlayerMover implements Serializable {
    private static final String MATCHSTRING = "wasd";
    private static final int FOVRANGE = 6;
    private final Map map;
    private Position pos;
    private Position newPos;
    private TETile last;
    private final ArrayList<Position> fov;

    public PlayerMover(Map map) {
        this.map = map;
        this.fov = new ArrayList<>();
    }

    public void setPosition(Position p) {
        pos = p;
        last = map.peek(p);
        map.placeTile(p, Tileset.PLAYER);
        updateFOV(FOVRANGE, pos);
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

                fov.clear();
                updateFOV(FOVRANGE, newPos);
            }
        }
    }

    /**
     * Updates the list of points that make up the current FOV of the player.
     */
    private void updateFOV(int count, Position p) {
        fov.add(p);
        if (!(map.peek(p).character() == '#')) {
            if (count > 0) {
                Position pUp = new Position(p.x(), p.y() + 1);
                Position pRight = new Position(p.x() + 1, p.y());
                Position pDown = new Position(p.x(), p.y() - 1);
                Position pLeft = new Position(p.x() - 1, p.y());

                updateFOV(count - 1, pUp);
                updateFOV(count - 1, pRight);
                updateFOV(count - 1, pDown);
                updateFOV(count - 1, pLeft);
            }
        }
    }

    /**
     * Returns the list of points that comprise the FOV.
     */
    public ArrayList<Position> getFOV() {
        return fov;
    }

    /**
     * Returns true or false, depending on whether the char is in MATCHSTRING
     * @param c char to be checked
     * @return resulting true or false
     */
    private boolean inMatchString(char c) {
        for (int i = 0; i < MATCHSTRING.length(); i++) {
            if (MATCHSTRING.charAt(i) == c) {
                return true;
            }
        }
        return false;
    }
}