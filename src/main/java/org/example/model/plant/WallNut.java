package org.example.model.plant;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Setter
@Getter
public class WallNut extends Plant {

    private String id;
    private int health;
    private int maxHealth;

    public WallNut(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.id = UUID.randomUUID().toString();
        this.width = width;
        this.height = height;
        this.maxHealth = 300;
        this.sunCost = 50;
        this.health = maxHealth;
    }

    public void takeDamage(int damage) {
        this.health -= damage;
    }

    public boolean isDead() {
        return this.health <= 0;
    }

}
