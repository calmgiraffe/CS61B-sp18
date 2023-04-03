package byog.Core.Level;

import byog.Core.Game;
import byog.Core.Graphics.TETile;
import byog.Core.Graphics.Tileset;
import byog.Core.State.PlayState;

import java.awt.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

public class Player extends Entity implements Serializable {
    private static final int FOV = 5;

    protected TETile tile = Tileset.PLAYER;
    private final Set<Integer> fov = new HashSet<>();
    private final Set<Integer> visited = new HashSet<>();

    public Player(int x, int y, Color color, Level level) {
        super(x, y, color, level);
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
            Position pos = Game.level.toPosition(p);
            TETile tile = Game.level.peek(pos.x, pos.y, MAIN);
            Game.level.place(pos.x, pos.y, new TETile(tile, new Color(45, 45, 45)), FOV);
        }
        fov.clear();
        this.updateFOVPoints(FOV, currX, currY);

        // Update FOV level with points fom this.fov
        for (int p : fov) {
            Position pos = Game.level.toPosition(p);
            TETile tile = Game.level.peek(pos.x, pos.y, MAIN);
            Game.level.place(pos.x, pos.y, tile, FOV);
        }
    }

    private void placeCharacterAndDoor() {
        int numRooms = Game.level.rooms.size();

        /* Pick a room and place character in it */
        int i = Game.rand.nextInt(numRooms - 1);
        Position playerPos = Game.level.rooms.get(i).randomPosition(1);
        this.currX = playerPos.x;
        this.currY = playerPos.y;
        this.currTile = Game.level.peek(currX, currY, MAIN);
        Game.level.place(currX, currY, Tileset.PLAYER, MAIN);

        /* Pick a room and place door in it */
        i = Game.rand.nextInt(numRooms - 1);
        Position doorPos = Game.level.rooms.get(i).randomPosition(1);
        Game.level.place(doorPos.x, doorPos.y, Tileset.UNLOCKED_DOOR, MAIN);
    }

    /* Updates the list of points that make up the current FOV and visited tiles.
     * this.fov is a Set of 1D positions corresponding to the coordinates of the desired FOV tiles */
    private void updateFOVPoints(int count, int x, int y) {
        if (count < 0 || !Game.level.isValid(x, y)) {
            return;
        }
        visited.add(Game.level.to1D(x, y));
        fov.add(Game.level.to1D(x, y));
        if (!(Game.level.peek(x, y, MAIN).character() == '#')) {
            updateFOVPoints(count - 1, x, y + 1);
            updateFOVPoints(count - 1, x, y - 1);
            updateFOVPoints(count - 1, x + 1, y);
            updateFOVPoints(count - 1, x - 1, y);
        }
    }
}