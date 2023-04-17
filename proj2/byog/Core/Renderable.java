package byog.Core;

import java.util.List;

public interface Renderable {
    List<Renderable> getRenderableData();
    void update();
}
