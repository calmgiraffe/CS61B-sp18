package byog.Core;

import byog.TileEngine.TETile;
import byog.TileEngine.Tileset;

public class Room {

    protected static final TETile wallType = Tileset.WALL;
    protected Position lowerLeft;
    protected Position upperRight;
    protected TETile floorType;

    public Room(Position lowerLeft, Position upperRight, TETile floorType) {
        this.lowerLeft = lowerLeft;
        this.upperRight = upperRight;
        this.floorType = floorType;
    }

}
