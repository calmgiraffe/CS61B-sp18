package byog.Core.State;

import byog.Core.Game;

public interface State {
    void setContext(Game game);
    void update();
}