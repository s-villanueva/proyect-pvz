package org.example.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class Entity {
    private int x;
    private int y;
    private int width;
    private int height;
    private String id;

    public Entity(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.id = generateId();
    }

    private String generateId() {
        return String.valueOf(System.nanoTime());
    }

    // Método abstracto que debe ser implementado por las clases hijas
    public abstract void update();
}
