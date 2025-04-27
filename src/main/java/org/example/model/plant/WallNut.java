package org.example.model.plant;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class WallNut extends Plant {

    private int health;
    private int maxHealth;

    public WallNut(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.maxHealth = 300; // Ejemplo de salud inicial
        this.health = maxHealth;
    }

    public void takeDamage(int damage) {
        this.health -= damage;
    }

    public boolean isDead() {
        return this.health <= 0;
    }

}
