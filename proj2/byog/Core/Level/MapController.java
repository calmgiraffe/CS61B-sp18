package byog.Core.Level;

import byog.Core.Game;
import byog.Core.Graphics.TETile;
import byog.Core.Graphics.Tileset;

import java.awt.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import static byog.Core.Level.Level.*;

public class MapController implements Serializable {
    /* Static variables */
    private static final int FOV_RANGE = 5;

    /** Public instance variables */
    protected TETile prevTile;
    /* Private instance variables */
    private final Set<Integer> fov = new HashSet<>();
    private final Set<Integer> visited = new HashSet<>();
    private int currX;
    private int currY;

    /** Also initializes Game.level with door and player */
    public MapController() {
        this.placeCharacterAndDoor();
        this.updateFOV();
    }

    /**
     * Given a direction 'wasd', moves the player icon up/right/down/left.
     * Checks that the tile to be moved to is not a wall, in which case the character is moved.
     * Keeps a record of the tile that was previously placed so original tile is restored
     * once player moves out.
     * @param direction one of 'wasd', representing direction to move in
     */
    public void parseCommand(char direction) {
        int newX = currX;
        int newY = currY;

        switch (direction) {
            case 'w' -> newY += 1;
            case 'd' -> newX += 1;
            case 's' -> newY -= 1;
            case 'a' -> newX -= 1;
        }
        // If the tile to move to is not a wall tile
        if (Game.level.peek(newX, newY, MAIN).character() != '#') {
            if (Game.level.peek(newX, newY, MAIN).character() == '▢') {
                /* Next tile is door -> go to next level */
                fov.clear();
                Game.level.generate();
                Game.level.level += 1;
                this.placeCharacterAndDoor();

            } else { // Next tile is not door
                /*
                At where the character currently is, place the tile that was previously there.
                Update prevTile to the tile where the player will move.
                At where the character is to move, place the player and update currX, currY.
                */
                Game.level.place(currX, currY, prevTile, MAIN);
                prevTile = Game.level.peek(newX, newY, MAIN);
                Game.level.place(newX, newY, Tileset.PLAYER, MAIN);
                currX = newX;
                currY = newY;
            }
            this.updateFOV();
        }
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
        this.updateFOVPoints(FOV_RANGE, currX, currY);

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
        this.prevTile = Game.level.peek(currX, currY, MAIN);
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