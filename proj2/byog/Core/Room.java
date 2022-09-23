package byog.Core;

import byog.TileEngine.TETile;
import byog.TileEngine.Tileset;

public class Room {

    protected static final TETile wallType = Tileset.WALL;
    protected Position lowerLeft;
    protected Position upperRight;
    protected Position centre;
    protected TETile floorType;

    public Room(Position lowerLeft, Position upperRight, TETile floorType) {
        this.lowerLeft = lowerLeft;
        this.upperRight = upperRight;
        this.centre = new Position((lowerLeft.x + upperRight.x) / 2, (lowerLeft.y + upperRight.y) / 2);
        this.floorType = floorType;
    }

}
