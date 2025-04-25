package org.example.model.attack;

import lombok.Getter;
import lombok.Setter;

/**
 * Attack
 *
 * @author Marcos Quispe
 * @since 1.0
 */
@Getter
@Setter
public class Attack {
    protected String id;
    protected int x;
    protected int y;
    protected int width;
    protected int height;
    protected int attack;
}
