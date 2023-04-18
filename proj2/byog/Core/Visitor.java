package byog.Core;

import byog.Core.Level.Entity;
import byog.Core.Level.Level;
import byog.Core.Level.Map.Map;
import byog.Core.Level.Text;
import byog.Core.Level.Tile;
import byog.Core.State.State;

public interface Visitor {
    void visit(Tile tile);
    void visit(Text text);
    void visit(Level level);
    void visit(Map map);
    void visit(Entity entity);
    void visit(State state);
}
