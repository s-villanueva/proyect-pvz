package org.example.model.zombie;

import lombok.Getter;
import lombok.Setter;
import org.example.model.Entity;
import org.example.logic.Game;

public abstract class Zombie extends Entity {
    private int health;
    private int speed;
    private int row;
    @Getter
    @Setter
    private ZombieState state;
    @Getter
    private Game game;

    public Zombie(int x, int y, int width, int height, int row, Game game) {
        super(x, y, width, height);
        this.health = 100;
        this.speed = 1;
        this.row = row;
        this.state = ZombieState.IN_PROGRESS;
        this.game = game;
    }

    public abstract void advance();

    public void takeDamage(int damage) {
        this.health -= damage;
        if (this.health <= 0) {
            die();
        }
    }

    public boolean isAlive() {
        return this.health > 0;
    }

    public void die() {
        if (state == ZombieState.IN_PROGRESS) {
            setState(ZombieState.WAITING);
            if (game != null) {
                game.removeZombie(this);
                game.getIGameEvents().deleteComponentUI(getId());
            }
        }
    }

}
