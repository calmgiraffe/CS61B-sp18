package byog.Core.Level;

import byog.Core.Component.PlayerInputComponent;
import byog.Core.Component.PlayerPhysicsComponent;
import byog.Core.GameObject.Entity;
import byog.Core.Graphics.Sprite;
import byog.Core.Level.Map.Map;
import byog.RandomTools.RandomInclusive;

import java.io.Serializable;

public class Level implements Serializable {
    PlayerInputComponent playerInputComponent;
    PlayerPhysicsComponent playerPhysicsComponent;
    private final Map map;
    private Entity player;

    public Level(int width, int height, RandomInclusive rand) {
        /* Instance variables */
        this.map = new Map(width, height, rand); // generate the underlying map (grid of tiles)
        this.player = new Entity(map.getStart(), Sprite.PLAYER, this); // place player
    }

    public void update() {
        // map.update();
        playerInputComponent.update(player);
        playerPhysicsComponent.update(player, map);
    }

    public void initializePlayer() {
        this.player = new Entity(map.getStart(), Sprite.PLAYER, this);
        this.playerInputComponent = new PlayerInputComponent();
    }
    public void setPlayer(Entity player) {
        this.player = player;
    }
    public Entity getPlayer() {
        return player;
    }
    public Map getMap() {
        return map;
    }
}