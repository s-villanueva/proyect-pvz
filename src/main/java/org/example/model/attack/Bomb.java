package org.example.model.attack;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.UUID;

/**
 * Bomb
 *
 * @author Marcos Quispe
 * @since 1.0
 */
@Getter
@Setter
@ToString
public class Bomb extends Attack {

    public Bomb(int x, int y, int width, int height) {
        this.id = UUID.randomUUID().toString();
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

}
