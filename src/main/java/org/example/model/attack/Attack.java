package org.example.model.attack;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Attack {
    protected String id;
    protected int x;
    protected int y;
    protected int width;
    protected int height;
    protected int attack;

    public int getDamage(int dmg) {
        return dmg; // o el valor correspondiente
    }
}
