package byog.Core;

import byog.Core.Graphics.FontSet;
import byog.Core.Level.Entity;
import byog.Core.Level.Level;
import byog.Core.Level.Map.Map;
import byog.Core.Level.Text;
import byog.Core.Level.Tile;
import byog.Core.State.State;
import edu.princeton.cs.introcs.StdDraw;

public class RendererVisitor implements Visitor {
    Renderer renderer = new Renderer();

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
    public void drawTile(Tile tile, double x, double y) {
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

    @Override
    public void visit(Tile tile) {
    }

    @Override
    public void visit(Text text) {
        StdDraw.setFont(text.getFont());
        StdDraw.setPenColor(text.getColor());
        StdDraw.text(text.getX(), text.getY(), text.getText());
    }

    @Override
    public void visit(Level level) {
    }

    @Override
    public void visit(Map map) {
    }

    @Override
    public void visit(Entity entity) {
    }

    @Override
    public void visit(State state) {
    }
}
