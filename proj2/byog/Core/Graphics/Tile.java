package byog.Core.Graphics;

import java.awt.Color;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Random;

import edu.princeton.cs.introcs.StdDraw;
import byog.RandomTools.RandomUtils;

/**
 * The Tile object is used to represent a single tile in your game. A 2D array of tiles make up a
 * board, and can be drawn to the screen using the Renderer class.
 * <p>
 * All Tile objects must have a character, textcolor, and background color to be used to represent
 * the tile when drawn to the screen. You can also optionally provide a path to an image file of an
 * appropriate size (16x16) to be drawn in place of the unicode representation. If the image path
 * provided cannot be found, draw will fallback to using the provided character and color
 * representation, so you are free to use image tiles on your own computer.
 * <p>
 * The provided Tile is immutable, i.e. none of its instance variables can change. You are welcome
 * to make your Tile class mutable, if you prefer.
 */

public class Tile implements Serializable {
    // Entities
    public static final Tile PLAYER = new Tile('@', Color.white, Color.black, "player");
    public static final Tile ENTITY = new Tile('&', Color.red, Color.black, "entity");

    // Standard
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

    private final char character; // Do not rename character or the autograder will break.
    private Color textColor;
    private final Color backgroundColor;
    private final String description;
    private final String filepath;


    /**
     * Full constructor for Tile objects.
     * @param character The character displayed on the screen.
     * @param textColor The color of the character itself.
     * @param backgroundColor The color drawn behind the character.
     * @param description The description of the tile, shown in the GUI on hovering over the tile.
     * @param filepath Full path to image to be used for this tile. Must be correct size (16x16)
     */
    public Tile(char character, Color textColor, Color backgroundColor, String description,
                String filepath) {
        this.character = character;
        this.textColor = textColor;
        this.backgroundColor = backgroundColor;
        this.description = description;
        this.filepath = filepath;
    }

    /**
     * Constructor without filepath. In this case, filepath will be null, so when drawing, we
     * will not even try to draw an image, and will instead use the provided character and colors.
     * @param character The character displayed on the screen.
     * @param textColor The color of the character itself.
     * @param backgroundColor The color drawn behind the character.
     * @param description The description of the tile, shown in the GUI on hovering over the tile.
     */
    public Tile(char character, Color textColor, Color backgroundColor, String description) {
        this.character = character;
        this.textColor = textColor;
        this.backgroundColor = backgroundColor;
        this.description = description;
        this.filepath = null;
    }

    /**
     * Creates a copy of Tile t, except with given textColor.
     * @param t tile to copy
     * @param textColor foreground color for tile copy
     */
    public Tile(Tile t, Color textColor) {
        this(t.character, textColor, t.backgroundColor, t.description, t.filepath);
    }

    /**
     * Creates a copy of the given tile with a slightly different text color. The new
     * color will have a red value that is within dr of the current red value,
     * and likewise with dg and db.
     * @param t the tile to copy
     * @param dr the maximum difference in red value
     * @param dg the maximum difference in green value
     * @param db the maximum difference in blue value
     * @param r the random number generator to use
     */
    public static Tile colorVariant(Tile t, int dr, int dg, int db, Random r) {
        Color oldColor = t.textColor;
        int newRed = newColorValue(oldColor.getRed(), dr, r);
        int newGreen = newColorValue(oldColor.getGreen(), dg, r);
        int newBlue = newColorValue(oldColor.getBlue(), db, r);

        Color c = new Color(newRed, newGreen, newBlue);

        return new Tile(t, c);
    }

    private static int newColorValue(int v, int dv, Random r) {
        int rawNewValue = v + RandomUtils.uniform(r, -dv, dv + 1);

        // make sure value doesn't fall outside of the range 0 to 255.
        int newValue = Math.min(255, Math.max(0, rawNewValue));
        return newValue;
    }

    /**
     * Converts the given 2D array to a String. Handy for debugging.
     * Note that since y = 0 is actually the bottom of your world when
     * drawn using the tile rendering engine, this print method has to
     * print in what might seem like backwards order (so that the 0th
     * row gets printed last).
     * @param world the 2D world to print
     * @return string representation of the world
     */
    public static String toString(Tile[][] world) {
        int width = world.length;
        int height = world[0].length;
        StringBuilder sb = new StringBuilder();

        for (int y = height - 1; y >= 0; y -= 1) {
            for (int x = 0; x < width; x += 1) {
                if (world[x][y] == null) {
                    throw new IllegalArgumentException("Tile at position x=" + x + ", y=" + y
                            + " is null.");
                }
                sb.append(world[x][y].character());
            }
            sb.append('\n');
        }
        return sb.toString();
    }

    /**
     * Makes a copy of the given 2D tile array.
     * @param tiles the 2D array to copy
     **/
    public static Tile[][] copyOf(Tile[][] tiles) {
        if (tiles == null) {
            return null;
        }

        Tile[][] copy = new Tile[tiles.length][];

        int i = 0;
        for (Tile[] column : tiles) {
            copy[i] = Arrays.copyOf(column, column.length);
            i += 1;
        }

        return copy;
    }

    /**
     * Provides an equals method that is consistent
     *  with the way that the autograder works.
     */
    @Override
    public boolean equals(Object x) {
        if (this == x) {
            return true;
        }
        if (x == null) {
            return false;
        }
        if (this.getClass() != x.getClass()) {
            return false;
        }
        Tile that = (Tile) x;
        return this.character == that.character;
    }

    @Override
    public int hashCode() {
        return this.character;
    }

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

    /* Getter methods */

    public char character() {
        return character;
    }

    public Color textColor() {
        return textColor;
    }

    public Color backgroundColor() {
        return backgroundColor;
    }

    public String description() {
        return description;
    }

    public String filepath() {
        return filepath;
    }
}
