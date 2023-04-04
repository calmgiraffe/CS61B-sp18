package byog.Core.Level;

import byog.Core.Graphics.Tile;

import java.awt.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

public class Player extends Entity implements Serializable {
    private static final int FOV = 5;

    private final Set<Integer> fov = new HashSet<>();
    private final Set<Integer> visited = new HashSet<>();

    public Player(int x, int y, Level level) {
        super(x, y, level, Tile.PLAYER);
    }

    /**
     * Given a direction 'wasd', moves the player icon up/right/down/left.
     * Checks that the tile to be moved to is not a wall, in which case the character is moved.
     * Keeps a record of the tile that was previously placed so original tile is restored
     * once player moves out.
     * @param direction one of 'wasd', representing direction to move in
     */
    public void move(char direction) {
        int dx = 0, dy = 0;

        switch (direction) {
            case 'w' -> dy = 1;
            case 'd' -> dx = 1;
            case 's' -> dy = -1;
            case 'a' -> dx = -1;
        }
       move(dx, dy);
    }


    /* Updates fovmap with the coordinates that correspond to the field of view.
    *  Implicitly assumes that player moved to a non-wall tile but FOV is not yet updated. */
    private void updateFOV() {
        // Replace current fov tiles with tiles from MAIN. If new level, fov is empty.
        for (int p : fov) {
            Position pos = level.toPosition(p);
            Tile tile = level.peek(pos.x, pos.y);
            level.place(pos.x, pos.y, new Tile(tile, new Color(45, 45, 45)));
        }
        fov.clear();
        this.updateFOVPoints(FOV, x, y);

        // Update FOV level with points fom this.fov
        for (int p : fov) {
            Position pos = level.toPosition(p);
            Tile tile = level.peek(pos.x, pos.y);
            level.place(pos.x, pos.y, tile);
        }
    }

    /* Updates the list of points that make up the current FOV and visited tiles.
     * this.fov is a Set of 1D positions corresponding to the coordinates of the desired FOV tiles */
    private void updateFOVPoints(int count, int x, int y) {
        if (count < 0 || !level.isValid(x, y)) {
            return;
        }
        visited.add(level.to1D(x, y));
        fov.add(level.to1D(x, y));
        if (!(level.peek(x, y).character() == '#')) {
            updateFOVPoints(count - 1, x, y + 1);
            updateFOVPoints(count - 1, x, y - 1);
            updateFOVPoints(count - 1, x + 1, y);
            updateFOVPoints(count - 1, x - 1, y);
        }
    }
}