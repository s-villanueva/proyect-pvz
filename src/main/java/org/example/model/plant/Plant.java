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
    private int row;
    private int col;
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
    }

    /**
     * Verifica si la planta sigue viva.
     */
    public void die(){
        if (game != null) {
            game.removePlant(this);
            game.getIGameEvents().deleteComponentUI(getId());
        }
    }

    /**
     * Elimina la planta del juego.
     */

    public boolean isDead() {
        if (this.health <= 0) {
            return true;
        }
        return false;
    }

    /**
     * Devuelve el rectángulo para verificar colisiones.
     */
    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }
}
