package org.example.model.attack;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.UUID;

/**
 * GreenPea
 *
 * @author Marcos Quispe
 * @since 1.0
 */
@Getter
@Setter
@ToString
public class GreenPea extends Attack {
    private int advanceTime = 5; // 5ms
    private int maxXToDied;
    private long prevTime;

    public GreenPea(int x, int y, int width, int height) {
        this.id = UUID.randomUUID().toString();
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.prevTime = System.currentTimeMillis();
    }

    public void avanzar() {
        x = x + 1;
    }
}
