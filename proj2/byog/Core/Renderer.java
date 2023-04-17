package byog.Core;

import byog.Core.Graphics.FontSet;
import byog.Core.Graphics.Text;
import byog.Core.Graphics.Tile;
import byog.Core.Level.Entity;
import byog.Core.State.State;
import edu.princeton.cs.introcs.StdDraw;

import java.awt.Color;
import java.awt.Font;
import java.io.Serializable;
import java.util.List;

/**
 * Utility class for rendering tiles. You do not need to modify this file. You're welcome
 * to, but be careful. We strongly recommend getting everything else working before
 * messing with this renderer, unless you're trying to do something fancy like
 * allowing scrolling of the screen or tracking the player or something similar.
 */
public class Renderer implements Serializable {
    private static final int TILE_SIZE = 18;
    private int width;
    private int height;
    private int xOffset;
    private int yOffset;

    /**
     * Same functionality as the other initialization method. The only difference is that the xOff
     * and yOff parameters will change where the renderFrame method starts drawing. For example,
     * if you select w = 60, h = 30, xOff = 3, yOff = 4 and then call renderFrame with a
     * Tile[50][25] array, the renderer will leave 3 tiles blank on the left, 7 tiles blank
     * on the right, 4 tiles blank on the bottom, and 1 tile blank on the top.
     * @param w width of the window in tiles
     * @param h height of the window in tiles.
     */
    public void initialize(int w, int h, int xOff, int yOff) {
        this.width = w;
        this.height = h;
        this.xOffset = xOff;
        this.yOffset = yOff;
        StdDraw.setCanvasSize(width * TILE_SIZE, height * TILE_SIZE);


        Font font = new Font("Monaco", Font.BOLD, TILE_SIZE - 2);
        StdDraw.setFont(font);

        StdDraw.setXscale(0, width);
        StdDraw.setYscale(0, height);

        StdDraw.clear(new Color(0, 0, 0)); // clear the screen to black
        StdDraw.enableDoubleBuffering();
        StdDraw.show(); // copy offscreen buffer to onscreen buffer
    }

    /**
     * Initializes StdDraw parameters and launches the StdDraw window. w and h are the
     * width and height of the world in number of tiles. If the Tile[][] array that you
     * pass to renderFrame is smaller than this, then extra blank space will be left
     * on the right and top edges of the frame. For example, if you select w = 60 and
     * h = 30, this method will create a 60 tile wide by 30 tile tall window. If
     * you then subsequently call renderFrame with a Tile[50][25] array, it will
     * leave 10 tiles blank on the right side and 5 tiles blank on the top side. If
     * you want to leave extra space on the left or bottom instead, use the other
     * initialization method.
     * @param w width of the window in tiles
     * @param h height of the window in tiles.
     */
    public void initialize(int w, int h) {
        this.initialize(w, h, 0, 0);
    }

    /**
     * Takes in a 2d array of Tile objects and renders the 2d array to the screen, starting from
     * xOffset and yOffset.
     * <p>
     * If the array is an NxM array, then the element displayed at positions would be as follows,
     * given in units of tiles.
     * <p>
     *              positions   xOffset |xOffset+1|xOffset+2| .... |xOffset+world.length
     * <p>
     * startY+world[0].length   [0][M-1] | [1][M-1] | [2][M-1] | .... | [N-1][M-1]
     *                    ...    ......  |  ......  |  ......  | .... | ......
     *               startY+2    [0][2]  |  [1][2]  |  [2][2]  | .... | [N-1][2]
     *               startY+1    [0][1]  |  [1][1]  |  [2][1]  | .... | [N-1][1]
     *                 startY    [0][0]  |  [1][0]  |  [2][0]  | .... | [N-1][0]
     * <p>
     * By varying xOffset, yOffset, and the size of the screen when initialized, you can leave
     * empty space in different places to leave room for other information, such as a GUI.
     * This method assumes that the xScale and yScale have been set such that the max x
     * value is the width of the screen in tiles, and the max y value is the height of
     * the screen in tiles.
     * @param world the 2D Tile[][] array to render
     */
    public void renderFrame(Tile[][] world) {
        if (world == null) {
            return;
        }
        int numXTiles = world.length;
        int numYTiles = world[0].length;
        for (int x = 0; x < numXTiles; x += 1) {
            for (int y = 0; y < numYTiles; y += 1) {
                if (world[x][y] == null) {
                    throw new IllegalArgumentException("Tile at position x=" + x + ", y=" + y + " is null.");
                }
                draw(world[x][y], x, y);
            }
        }
    }

    /**
     * Draws the tile to the screen at location x, y. If a valid filepath is provided,
     * we draw the image located at that filepath to the screen. Otherwise, we fall
     * back to the character and color representation for the tile.
     * <p>
     * Note that the image provided must be of the right size (16x16). It will not be
     * automatically resized or truncated.
     * @param x x coordinate
     * @param y y coordinate
     */
    public void draw(Tile tile, double x, double y) {
        if (tile.filepath() != null) {
            try {
                StdDraw.picture(x + 0.5, y + 0.5, tile.filepath());
                return;
            } catch (IllegalArgumentException e) {
                // Exception happens because the file can't be found. In this case, fail silently
                // and just use the character and background color for the tile.
            }
        }
        StdDraw.setFont(FontSet.TILE);
        StdDraw.setPenColor(tile.backgroundColor());
        StdDraw.filledSquare(x + 0.5, y + 0.5, 0.5);
        StdDraw.setPenColor(tile.textColor());
        StdDraw.text(x + 0.5, y + 0.5, Character.toString(tile.character()));
    }

    /* Render the given state. State consists of texts and tiles currently.
    *  "Rendering" is equivalent to showing one frame.
    */
    public void render(State state) {
        // Todo: render should iterate through renderable objects recursively

        List<Renderable> data = state.getData();
        StdDraw.clear(Color.BLACK);

        for (Renderable obj : data) {
            if (obj instanceof Text) {

            } else if (obj instanceof Tile) {

            } else if (obj instanceof Entity) {

            }
        }
        // i.e, state.getData();
        StdDraw.show();
    }
}
