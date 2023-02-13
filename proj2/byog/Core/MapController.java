package byog.Core;

import byog.TileEngine.TETile;
import byog.TileEngine.Tileset;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import static byog.Core.Map.*;

public class MapController implements Serializable {
    /* Static variables */
    private static final int FOV_RANGE = 4;

    /** Public instance variables */
    protected TETile prevTile;
    /* Private instance variables */
    private final Set<Integer> fov = new HashSet<>();
    private final Set<Integer> visited = new HashSet<>();
    private int currX;
    private int currY;

    /** Also initializes Game.map with door and player */
    public MapController() {
        int numRooms = Game.map.rooms.size();

        /* Pick a room and place character in it */
        int i = Game.rand.nextInt(numRooms - 1);
        Position playerPos = Game.map.rooms.get(i).randomPosition(1);
        this.currX = playerPos.x;
        this.currY = playerPos.y;
        this.prevTile = Game.map.peek(currX, currY, MAP);
        Game.map.place(currX, currY, Tileset.PLAYER, MAP);

        /* Pick a room and place door in it */
        i = Game.rand.nextInt(numRooms - 1);
        Position doorPos = Game.map.rooms.get(i).randomPosition(1);
        Game.map.place(doorPos.x, doorPos.y, Tileset.UNLOCKED_DOOR, MAP);

        updateFOV();
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
        if (Game.map.peek(newX, newY, MAP).character() != '#') {
            if (Game.map.peek(newX, newY, MAP).character() == '▢') {
                /* Next tile is door -> go to next level */
                Game.map.generate();
                Game.map.level += 1;
                int numRooms = Game.map.rooms.size();

                /* Todo: Duplicate of constructor code - could put in separate method? */
                /* Pick a room and place character in it */
                int i = Game.rand.nextInt(numRooms - 1);
                Position playerPos = Game.map.rooms.get(i).randomPosition(1);
                this.currX = playerPos.x;
                this.currY = playerPos.y;
                this.prevTile = Game.map.peek(currX, currY, MAP);
                Game.map.place(currX, currY, Tileset.PLAYER, MAP);

                /* Pick a room and place door in it */
                i = Game.rand.nextInt(numRooms - 1);
                Position doorPos = Game.map.rooms.get(i).randomPosition(1);
                Game.map.place(doorPos.x, doorPos.y, Tileset.UNLOCKED_DOOR, MAP);

            } else { // Next tile is not door
                /*
                At where the character currently is, place the tile that was previously there.
                Update prevTile to the tile where the player will move.
                At where the character is to move, place the player and update currX, currY.
                */
                Game.map.place(currX, currY, prevTile, MAP);
                prevTile = Game.map.peek(newX, newY, MAP);
                Game.map.place(newX, newY, Tileset.PLAYER, MAP);
                currX = newX;
                currY = newY;
            }
            this.updateFOV();
        }
    }

    /* Updates fovmap with the coordinates that correspond to the field of view.
    *  Implicitly assumes that player moved to a non-wall tile but FOV is not yet updated. */
    private void updateFOV() {
        fov.clear();
        this.updateFOVPoints(FOV_RANGE, currX, currY);

        // Fill FOV map with blank tiles
        for (int i = 0; i < Game.map.width; i++) {
            for (int j = 0; j < Game.map.height; j++) {
                Game.map.place(i, j, Tileset.NOTHING, FOV);
            }
        }
        // Update FOV map with points fom this.fov
        for (int p : fov) {
            Position pos = Game.map.toPosition(p);
            TETile tile = Game.map.peek(pos.x, pos.y, MAP);
            Game.map.place(pos.x, pos.y, tile, FOV);
        }

        // at each time increment, want to recalculate the fovmap
    }

    /* Updates the list of points that make up the current FOV and visited tiles.
     * this.fov is a Set of 1D positions corresponding to the coordinates of the desired FOV tiles */
    private void updateFOVPoints(int count, int x, int y) {
        if (count < 0 || !Game.map.isValid(x, y)) {
            return;
        }
        visited.add(Game.map.to1D(x, y));
        fov.add(Game.map.to1D(x, y));
        if (!(Game.map.peek(x, y, MAP).character() == '#')) {
            updateFOVPoints(count - 1, x, y + 1);
            updateFOVPoints(count - 1, x, y - 1);
            updateFOVPoints(count - 1, x + 1, y);
            updateFOVPoints(count - 1, x - 1, y);
        }
    }
}