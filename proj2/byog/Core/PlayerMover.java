package byog.Core;

import byog.TileEngine.TETile;
import byog.TileEngine.Tileset;
import java.io.Serializable;
import java.util.ArrayList;

public class PlayerMover implements Serializable {
    /* Static variables */
    private static final int FOV_RANGE = 5;

    /** Public instance variables */
    protected TETile prevTile;
    /* Private instance variables */
    private final ArrayList<Position> fov = new ArrayList<>();
    private int currX;
    private int currY;

    /** Constructor */
    public PlayerMover() {
        int numRooms = Game.map.rooms.size();

        /* Pick a room and place character in it */
        int i = Game.rand.nextIntInclusive(numRooms - 1);
        Position playerPos = Game.map.rooms.get(i).randomPosition(1);
        this.currX = playerPos.x;
        this.currY = playerPos.y;
        this.prevTile = Game.map.peek(currX, currY);
        Game.map.placeTile(currX, currY, Tileset.PLAYER);

        /* Pick a room and place door in it */
        i = Game.rand.nextIntInclusive(numRooms - 1);
        Position doorPos = Game.map.rooms.get(i).randomPosition(1);
        Game.map.placeTile(doorPos.x, doorPos.y, Tileset.UNLOCKED_DOOR);

        fov.clear();
        getFOVPoints(FOV_RANGE, currX, currY);
        Game.map.updateFOVmap(fov);
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
        if (Game.map.peek(newX, newY).character() != '#') { // if tile to move to is not wall
            if (Game.map.peek(newX, newY).character() == '▢') { // if next tile is door
                Game.map.generateWorld();
                Game.map.level += 1;
                int numRooms = Game.map.rooms.size();

                /* Todo: Duplicate of constructor code - could put in separate method? */
                /* Pick a room and place character in it */
                int i = Game.rand.nextIntInclusive(numRooms - 1);
                Position playerPos = Game.map.rooms.get(i).randomPosition(1);
                this.currX = playerPos.x;
                this.currY = playerPos.y;
                this.prevTile = Game.map.peek(currX, currY);
                Game.map.placeTile(currX, currY, Tileset.PLAYER);

                /* Pick a room and place door in it */
                i = Game.rand.nextIntInclusive(numRooms - 1);
                Position doorPos = Game.map.rooms.get(i).randomPosition(1);
                Game.map.placeTile(doorPos.x, doorPos.y, Tileset.UNLOCKED_DOOR);
            } else {
                /*
                At where the character currently is, place the tile that was previously there.
                Update prevTile to the tile where the player will move.
                At where the character is to move, place the player and update currX, currY.
                Clear FOV and remake its set of coordinates.
                */
                Game.map.placeTile(currX, currY, prevTile);
                prevTile = Game.map.peek(newX, newY);
                Game.map.placeTile(newX, newY, Tileset.PLAYER);
                currX = newX;
                currY = newY;
            }
            fov.clear();
            getFOVPoints(FOV_RANGE, currX, currY);
            Game.map.updateFOVmap(fov);
        }
    }

    /*
     * Updates the list of points that make up the current FOV of the player.
     * this.fov is a List of Positions corresponding to the coordinates of the desired FOV tiles
     * Todo: change to a more efficient method?
     */
    private void getFOVPoints(int count, int x, int y) {
        if (count < 0 || !Game.map.isValid(x, y)) {
            return;
        }
        fov.add(new Position(x, y));
        if (!(Game.map.peek(x, y).character() == '#')) {
            getFOVPoints(count - 1, x, y + 1);
            getFOVPoints(count - 1, x, y - 1);
            getFOVPoints(count - 1, x + 1, y);
            getFOVPoints(count - 1, x - 1, y);
        }
    }
}