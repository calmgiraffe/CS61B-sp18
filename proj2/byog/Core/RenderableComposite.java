package byog.Core;

import java.util.ArrayList;
import java.util.List;

public class RenderableComposite implements Renderable {
    /* Instance variables */
    protected List<Renderable> children;

    public RenderableComposite() {
        this.children = new ArrayList<>();
    }

    public RenderableComposite(List<Renderable> children) {
        this.children = children;
    }

    @Override
    public List<Renderable> getRenderableData() {
        return children;
    }

    @Override
    public void update() {
        // Todo: should traverse tree and update elements recursively
    }

    public void add() {

    }

    public void remove() {

    }
}
