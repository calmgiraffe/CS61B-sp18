package byog.TileEngine;

import java.awt.Color;
import java.util.Random;

/**
 * Contains constant tile objects, to avoid having to remake the same tiles in different parts of
 * the code.
 *
 * You are free to (and encouraged to) create and add your own tiles to this file. This file will
 * be turned in with the rest of your code.
 *
 * Ex:
 *      world[x][y] = Tileset.FLOOR;
 *
 * The style checker may crash when you try to style check this file due to use of unicode
 * characters. This is OK.
 */

public class Tileset {
    public static final TETile PLAYER = new TETile('@', Color.white, Color.black, "player");
    public static final TETile WALL = new TETile('#', new Color(216, 128, 128), Color.darkGray, "wall");
    public static final TETile FLOOR = new TETile('·', new Color(128, 192, 128), Color.black, "floor");
    public static final TETile NOTHING = new TETile(' ', Color.black, Color.black, "");
    public static final TETile GRASS = new TETile('"', new Color(66, 197, 40), Color.black, "grass");
    public static final TETile WATER = new TETile('≈', Color.blue, Color.black, "water");
    public static final TETile FLOWER = new TETile('❀', Color.magenta, Color.pink, "flower");
    public static final TETile LOCKED_DOOR = new TETile('█', Color.orange, Color.black, "locked door");
    public static final TETile UNLOCKED_DOOR = new TETile('▢', Color.orange, Color.black, "unlocked door");
    public static final TETile SAND = new TETile('▒', Color.yellow, Color.black, "sand");
    public static final TETile MOUNTAIN = new TETile('▲', Color.gray, Color.black, "mountain");
    public static final TETile TREE = new TETile('♠', Color.green, Color.black, "tree");

    // Better looking flowers
    public static final TETile FLOWERMAGENTA = new TETile('❀', Color.magenta, Color.black, "flower");
    public static final TETile FLOWERORANGE = new TETile('❀', Color.ORANGE, Color.black, "orange flower");
    public static final TETile FLOWERRED = new TETile('❀', Color.red, Color.black, "red flower");
    public static final TETile FLOWERPINK = new TETile('❀', Color.pink, Color.black, "pink flower");
    public static final TETile FLOWERCYAN = new TETile('❀', Color.cyan, Color.black, "flower");

    public static TETile randomFlower(Random r) {
        int choice = r.nextInt(3);
        if (choice == 0) {
            return FLOWERORANGE;
        } else if (choice == 1) {
            return FLOWERRED;
        } else {
            return FLOWERPINK;
        }
    }

    public static TETile colorVariantWall(Random r) {
        return TETile.colorVariant(WALL, 30, 30, 30, r);
    }

    public static TETile colorVariantGrass(Random r) {
        return TETile.colorVariant(GRASS, 50, 50, 50, r);
    }
}
