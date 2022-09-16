package byog.Core;

import byog.TileEngine.TETile;
import byog.TileEngine.Tileset;

public class Room {
    private static TETile wallType = Tileset.WALL;
    private Position lowerLeft;
    private Position upperRight;
    private TETile floorType;

    public Room(Position lowerLeft, Position upperRight, TETile floorType) {
        this.lowerLeft = lowerLeft;
        this.upperRight = upperRight;
        this.floorType = floorType;
    }

    public Position lowerLeft() {
        return lowerLeft;
    }

    public Position upperRight() {
        return upperRight;
    }

    public TETile floorType() {
        return floorType;
    }
}
