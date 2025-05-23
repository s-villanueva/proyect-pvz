package org.example.model.plant;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class PeaShooter extends Plant {

    private int attackTime = 1000; // 1000ms = 1seg
    private long prevTime;

    public PeaShooter(int x, int y, int width, int height) {
        this.id = UUID.randomUUID().toString();
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.sunCost = 100;
        this.prevTime = System.currentTimeMillis();
    }

    @Override
    public String toString() {
        return "PeaShooter{" +
                "attackTime=" + attackTime +
                ", prevTime=" + prevTime +
                ", id='" + id + '\'' +
                ", x=" + x +
                ", y=" + y +
                ", width=" + width +
                ", height=" + height +
                ", reloadTime=" + reloadTime +
                ", defenseInitial=" + defenseInitial +
                ", defense=" + defense +
                '}';
    }
}
