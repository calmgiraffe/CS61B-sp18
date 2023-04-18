package byog.Core.State;

import byog.Core.Game;

public interface State {
    void setContext(Game game);
    void nextFrame(char cmd, double x, double y);
}