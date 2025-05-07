package org.example.model.plant;

import lombok.Getter;
import lombok.Setter;
import org.example.logic.Game;

import java.awt.Rectangle;

@Getter
@Setter
public class Plant {
    protected Game game;
    protected String id;
    protected int x;
    protected int y;
    protected int width;
    protected int height;

    protected int reloadTime = 1000;      // Tiempo de recarga por defecto
    protected int defenseInitial = 100;   // Defensa inicial
    protected int defense = 100;          // Defensa actual
    protected int sunCost = 50;           // Coste de soles
    protected int health = 100;           // Vida inicial

    /**
     * Método para recibir daño.
     */
    public void takeDamage(int damage) {
        this.health -= damage;
        if (this.health <= 0) {
            die();  // Elimina la planta si muere
        }
    }

    /**
     * Verifica si la planta sigue viva.
     */
    public boolean isAlive() {
        return this.health > 0;
    }

    /**
     * Elimina la planta del juego.
     */
    public void die() {
        if (game != null) {
            game.deletePlant(this);
        }
    }

    /**
     * Devuelve el rectángulo para verificar colisiones.
     */
    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }
}
