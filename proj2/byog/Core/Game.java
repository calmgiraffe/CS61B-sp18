package byog.Core;

import byog.Core.Component.RendererComponent;
import byog.Core.State.MainMenuState;

import byog.Core.State.State;
import java.io.*;

import static java.lang.System.exit;

public class Game implements Serializable {
    /* Static variables */
    private static final int MS_PER_UPDATE = 15;
    public static final int WIDTH = 50;
    public static final int HEIGHT = 40;
    public static final int HUD_HEIGHT = 4;
    public static Controller controller;

    /* Instance variables */
    public RendererComponent rendererComponent;
    private boolean quitGame = false;
    private State state;

    /* Constructor */
    public Game(String cmdString) {
        rendererComponent.initialize(WIDTH, HEIGHT);
        controller = new Controller(cmdString);
        this.state = new MainMenuState(this); // set initial state
    }

    public void start() throws InterruptedException {
        while (!quitGame) {
            // Get keyboard and mouse inputs
            state.update();
            rendererComponent.render(state);
            Thread.sleep(MS_PER_UPDATE);
        }
        exit(0);
    }

    public void quit() {
        this.quitGame = true;
    }

    public void setContext(State state) {
        this.state = state;
    }
}