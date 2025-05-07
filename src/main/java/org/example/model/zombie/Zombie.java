package org.example.model.zombie;

import lombok.Getter;
import lombok.Setter;
import org.example.model.Entity;
import org.example.logic.Game;
import org.example.model.plant.Plant;

import java.awt.*;
import java.util.List;

public abstract class Zombie extends Entity {
    @Getter
    @Setter
    private int health;
    private int speed;
    private int row;
    @Getter
    @Setter
    private ZombieState state;
    @Getter
    private Game game;
    @Getter
    @Setter
    private long lastAttackTime = 0;
    @Getter
    @Setter
    private final long attackCooldown = 1000; // 1 segundo entre ataques


    public Zombie(int x, int y, int width, int height, int row, Game game) {
        super(x, y, width, height);
        this.health = 100;
        this.speed = 1;
        this.row = row;
        this.state = ZombieState.IN_PROGRESS;
        this.game = game;
    }

    public abstract void advance();

    public void tryToAttackPlant(List<Plant> plants) {
        for (Plant plant : plants) {
            if (plant.getY() == this.getY()) { // misma fila
                if (isColliding(plant)) {
                    long currentTime = System.currentTimeMillis();
                    if (currentTime - lastAttackTime >= attackCooldown) {
                        System.out.println("atta");
                        plant.takeDamage(20); // o el valor que desees
                        lastAttackTime = currentTime;
                    }
                    return; // Ya está atacando, no avanza
                }
            }
        }

        // Si no colisiona con ninguna planta, avanza normalmente
        advance();
    }

    private boolean isColliding(Plant plant) {
        int zombieLeft = this.getX();
        int zombieRight = this.getX() + this.getWidth();
        int zombieTop = this.getY();
        int zombieBottom = this.getY() + this.getHeight();

        int plantLeft = plant.getX();
        int plantRight = plant.getX() + plant.getWidth();
        int plantTop = plant.getY();
        int plantBottom = plant.getY() + plant.getHeight();

        // Verifica si hay colisión (intersección de rectángulos)
        return zombieRight > plantLeft && zombieLeft < plantRight &&
                zombieBottom > plantTop && zombieTop < plantBottom;
    }



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

    public Rectangle getBounds() {
        return new Rectangle(getX(), getY(), getWidth(), getHeight());
    }


}
