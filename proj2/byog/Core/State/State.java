package byog.Core.State;

import byog.Core.Game;
import byog.Core.Renderable;

import java.util.List;

public interface State {
    void nextFrame(char cmd, double x, double y);
    void setContext(Game game);
    List<Renderable> getData();
}
