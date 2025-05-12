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
    private boolean played = false;

    public LawnMower(int x, int y, int row, Game game) {
        this.x = x;
        this.y = y;
        this.row = row;
        this.game = game;
        this.active = false;
    }

    public void move() {
        if (active) {
            x += speed; // velocidad ajustable
        }
    }
}

