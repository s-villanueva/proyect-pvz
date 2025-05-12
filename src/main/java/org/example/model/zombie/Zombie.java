package org.example.model.zombie;

import lombok.Getter;
import lombok.Setter;
import org.example.logic.IGameEvents;
import org.example.model.Entity;
import org.example.logic.Game;
import org.example.model.plant.Plant;

import java.awt.*;
import java.util.List;
@Getter
@Setter
public abstract class Zombie extends Entity {
    private int health;
    private int speed;
    private int row;
    private ZombieState state;
    private long lastMoveTime = 0;
    private long moveInterval = 100;
    private boolean frozen = false;
    private long frozenUntil = 0;
    private Game game;
    private long lastAttackTime = 0;
    private boolean attacking = false;
    private final int attackDamage = 15;
    private final long attackCooldown = 1000;
    private boolean armLost = false;


    public Zombie(int x, int y, int width, int height, int row, Game game) {
        super(x, y, width, height);
        this.health = 100;
        this.speed = 1;
        this.row = row;
        this.state = ZombieState.IN_PROGRESS;
        this.game = game;
    }

    public abstract void advance();

    public void attackPlant(Plant plant) {
        long now = System.currentTimeMillis();
        if (now - lastAttackTime >= attackCooldown) {
            plant.takeDamage(attackDamage);
            lastAttackTime = now;
        }
    }

    public void takeDamage(int damage) {
        this.health -= damage;
        if (this.health <= 0) {
            die();
        }
    }

    public boolean isDead() {
        return health <= 0;
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
