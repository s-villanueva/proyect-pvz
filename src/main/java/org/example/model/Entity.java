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

    // Constructor que recibe los parámetros comunes
    public Entity(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.id = generateId();
    }

    // Método para generar un identificador único
    private String generateId() {
        return String.valueOf(System.nanoTime());  // Usamos el tiempo en nanosegundos para un ID único
    }

    // Método abstracto que debe ser implementado por las clases hijas
    // Es útil para realizar acciones comunes, pero cada tipo de entidad puede tener su propia implementación
    public abstract void update();
}
