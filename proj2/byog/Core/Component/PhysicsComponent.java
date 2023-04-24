package byog.Core.Component;

import byog.Core.GameObject.GameObject;
import byog.Core.Level.Map.Map;

public interface PhysicsComponent {
    void update(GameObject obj, Map map);
}
