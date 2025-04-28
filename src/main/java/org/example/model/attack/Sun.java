package org.example.model.attack;

import lombok.Getter;
import lombok.Setter;
import java.util.UUID;

@Getter
@Setter
public class Sun {
    private String id;
    private int x;
    private int y;
    private int width;
    private int height;

    public Sun(int x, int y, int width, int height) {
        this.id = UUID.randomUUID().toString();
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }
}
