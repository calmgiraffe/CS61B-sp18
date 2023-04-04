package byog.Core.Graphics;

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
    // Entities
    public static final Tile PLAYER = new Tile('@', Color.white, Color.black, "player");
    public static final Tile ENTITY = new Tile('&', Color.red, Color.black, "entity");

    public static final Tile WALL = new Tile('#', new Color(216, 128, 128), Color.darkGray, "wall");
    public static final Tile FLOOR = new Tile('·', new Color(128, 192, 128), Color.black, "floor");
    public static final Tile NOTHING = new Tile(' ', Color.black, Color.black, "");
    public static final Tile GRASS = new Tile('"', new Color(66, 197, 40), Color.black, "grass");
    public static final Tile WATER = new Tile('≈', Color.blue, Color.black, "water");
    public static final Tile FLOWER = new Tile('❀', Color.magenta, Color.pink, "flower");
    public static final Tile LOCKED_DOOR = new Tile('█', Color.orange, Color.black, "locked door");
    public static final Tile UNLOCKED_DOOR = new Tile('▢', Color.orange, Color.black, "unlocked door");
    public static final Tile SAND = new Tile('▒', Color.yellow, Color.black, "sand");
    public static final Tile MOUNTAIN = new Tile('▲', Color.gray, Color.black, "mountain");
    public static final Tile TREE = new Tile('♠', Color.green, Color.black, "tree");

    // Better looking flowers
    public static final Tile FLOWERMAGENTA = new Tile('❀', Color.magenta, Color.black, "flower");
    public static final Tile FLOWERORANGE = new Tile('❀', Color.ORANGE, Color.black, "orange flower");
    public static final Tile FLOWERRED = new Tile('❀', Color.red, Color.black, "red flower");
    public static final Tile FLOWERPINK = new Tile('❀', Color.pink, Color.black, "pink flower");
    public static final Tile FLOWERCYAN = new Tile('❀', Color.cyan, Color.black, "flower");

    public static Tile randomFlower(Random r) {
        int choice = r.nextInt(3);
        if (choice == 0) {
            return FLOWERORANGE;
        } else if (choice == 1) {
            return FLOWERRED;
        } else {
            return FLOWERPINK;
        }
    }

    public static Tile colorVariantWall(Random r) {
        return Tile.colorVariant(WALL, 30, 30, 30, r);
    }

    public static Tile colorVariantGrass(Random r) {
        return Tile.colorVariant(GRASS, 50, 50, 50, r);
    }
}
