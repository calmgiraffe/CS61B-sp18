package byog.Core.State;

import byog.Core.Game;
import byog.Core.Visitable;

public interface State extends Visitable {
    void setContext(Game game);
    void nextFrame(char cmd, double x, double y);
}