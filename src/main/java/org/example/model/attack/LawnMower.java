package org.example.model.attack;

import lombok.Getter;
import lombok.Setter;
import org.example.logic.Game;
import org.example.model.zombie.Zombie;

@Getter
@Setter
public class LawnMower extends Attack {
    private int x, y, row;

    private boolean used = false;
    private Game game;
    private boolean active;
    private final int speed = 10;


    public LawnMower(int x, int y, int row, Game game) {
        this.x = x;
        this.y = y;
        this.row = row;
        this.game = game;
        this.active = false;
    }

    public void checkCollision(Zombie zombie) {
        if (!used && zombie.getRow() == row && Math.abs(zombie.getX() - x) < 20) {
            used = true;
            game.removeZombie(zombie);
            // Podrías también iniciar una animación o sonido aquí si deseas
        }
    }

    public void move() {
        if (active) {
            x += speed; // velocidad ajustable
        }
    }
}

