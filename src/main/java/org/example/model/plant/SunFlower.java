package org.example.model.plant;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.UUID;

/**
 * PeaShooter
 *
 * @author Marcos Quispe
 * @since 1.0
 */
@Getter
@Setter
public class SunFlower extends Plant {

    private int sunTime = 22000; // 22000ms = 22seg
    private long prevTime;

    public SunFlower(int x, int y, int width, int height) {
        this.id = UUID.randomUUID().toString();
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.prevTime = System.currentTimeMillis();
    }

    @Override
    public String toString() {
        return "SunFlower{" +
                "id='" + id + '\'' +
                ", x=" + x +
                ", y=" + y +
                ", width=" + width +
                ", height=" + height +
                ", reloadTime=" + reloadTime +
                ", defenseInitial=" + defenseInitial +
                ", defense=" + defense +
                ", sunTime=" + sunTime +
                ", prevTime=" + prevTime +
                '}';
    }
}
