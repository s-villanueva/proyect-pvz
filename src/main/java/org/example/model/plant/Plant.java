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
    protected int reloadTime = 1000;
    protected int defenseInitial = 100;
    protected int defense = 100;
    protected int sunCost = 50;
    protected int health = 100;

    public void takeDamage(int damage) {
        this.health -= damage;

    }
}
