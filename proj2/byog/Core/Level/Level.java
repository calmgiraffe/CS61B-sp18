package byog.Core.Level;

import byog.Core.Level.Map.Map;
import byog.Core.Visitable;
import byog.Core.Renderer;
import byog.Core.Visitor;
import byog.RandomTools.RandomInclusive;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Level implements Serializable, Visitable {
    /* Instance variables */
    private final int width;
    private final int height;
    private final RandomInclusive rand;
    private final Map map;
    private final List<Visitable> visitables;

    public Level(int width, int height, RandomInclusive rand) {
        this.width = width;
        this.height = height;
        this.rand = rand;
        this.map = new Map(width, height, rand);
        this.visitables = new ArrayList<>(Arrays.asList(map));
        visitables.add(map);
    }

    public void nextFrame() {
    }

    public Map getMap() {
        return map;
    }

    public void updateEntities(char cmd) {
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    @Override
    public List<Visitable> getVisitables() {
        return visitables;
    }
}