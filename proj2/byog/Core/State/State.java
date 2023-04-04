package byog.Core.State;

import java.util.List;

import byog.Core.Game;
import byog.Core.Graphics.Text;
import byog.Core.Graphics.Tile;

public interface State {
    void nextFrame(char cmd, double x, double y);
    List<Text> getText();
    Tile[][] getTilemap();
    void setContext(Game game);
}
