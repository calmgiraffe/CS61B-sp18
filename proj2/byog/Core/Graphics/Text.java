package byog.Core.Graphics;

import java.awt.*;

public class Text {
    private String text;
    private Color color;
    private Font font;
    private double x;
    private double y;

    public Text(String text, Color color, Font font, double x, double y) {
        this.text = text;
        this.color = color;
        this.font = font;
        this.x = x;
        this.y = y;
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
    public void setX(double x) {
        this.x = x;
    }
    public void setY(double y) {
        this.y = y;
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
    public double getX() {
        return this.x;
    }
    public double getY() {
        return this.y;
    }
}
