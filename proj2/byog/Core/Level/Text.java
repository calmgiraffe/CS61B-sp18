package byog.Core.Level;

import byog.Core.Visitable;
import byog.Core.Renderer;
import byog.Core.Visitor;

import java.awt.*;
import java.io.Serializable;
import java.util.List;

public class Text implements Serializable, Visitable {

    public enum Alignment {
        LEFT,
        CENTRE,
        RIGHT
    }
    private String text;
    private Color color;
    private Font font;
    private double x;
    private double y;
    private Alignment alignment;

    public Text(String text, Color color, Font font, double x, double y, Alignment alignment) {
        this.text = text;
        this.color = color;
        this.font = font;
        this.x = x;
        this.y = y;
        this.alignment = alignment;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    @Override
    public List<Visitable> getVisitables() {
        return null;
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
    public Alignment getAlignment() {
        return this.alignment;
    }
}
