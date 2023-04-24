package byog.Core;

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
    public static Renderer renderer;

    /* Instance variables */
    private boolean quitGame = false;
    private State state;

    /* Constructor */
    public Game() {
        controller = new Controller();
        renderer = new Renderer(WIDTH, HEIGHT);
        this.state = new MainMenuState(this); // set initial state
    }

    public void start() throws InterruptedException {
        while (!quitGame) {
            renderer.clear();
            state.update();
            renderer.show();
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