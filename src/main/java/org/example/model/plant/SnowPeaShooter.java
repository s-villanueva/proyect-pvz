package org.example.model.plant;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class SnowPeaShooter extends Plant {

    private int attackTime = 1200;
    private long prevTime;
    private double slowEffectDuration = 3.0;
    private double slowPercentage = 0.5;

    public SnowPeaShooter(int x, int y, int width, int height) {
        this.id = UUID.randomUUID().toString();
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.sunCost = 175;
        this.prevTime = System.currentTimeMillis();
    }

    @Override
    public String toString() {
        return "SnowPeaShooter{" +
                "attackTime=" + attackTime +
                ", prevTime=" + prevTime +
                ", slowEffectDuration=" + slowEffectDuration +
                ", slowPercentage=" + slowPercentage +
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
