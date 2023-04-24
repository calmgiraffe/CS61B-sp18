package byog.Core.Component;

import byog.Core.GameObject.GameObject;
import byog.Core.Renderer;

public interface GraphicsComponent {
    void update(GameObject obj, Renderer renderer);
}
