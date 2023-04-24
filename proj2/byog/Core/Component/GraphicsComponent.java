package byog.Core.Component;

import byog.Core.GameObject;
import byog.Core.Graphics.Renderer;

public interface GraphicsComponent {
    void update(GameObject obj, Renderer renderer);
}
