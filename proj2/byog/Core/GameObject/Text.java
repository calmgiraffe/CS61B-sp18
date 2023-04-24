package byog.Core.GameObject;

import java.awt.*;
import java.io.Serializable;

public class Text extends GameObject implements Serializable {
    public enum Alignment {
        LEFT,
        CENTRE,
        RIGHT
    }
    private String text;
    private Color color;
    private Font font;
    private Alignment alignment;

    public Text(String text, Color color, Font font, double x, double y, Alignment alignment) {
        super(x, y);
        this.text = text;
        this.color = color;
        this.font = font;
        this.alignment = alignment;
    }

    public void setText(String text) {
        this.text = text;
    }
    public void setColor(Color color) {
        this.color = color;
    }
    public void setFont(Font font) {
        this.font = font;
    }
    public String getText() {
        return this.text;
    }
    public Color getColor() {
        return this.color;
    }
    public Font getFont() {
        return this.font;
    }
    public Alignment getAlignment() {
        return this.alignment;
    }
}
