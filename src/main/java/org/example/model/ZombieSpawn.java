package org.example.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class ZombieSpawn {
    int timeMs; // tiempo en milisegundos desde el inicio
    int row;
    String type; // "basic", "cone", "bucket"

    public ZombieSpawn(int timeMs, int row, String type) {
        this.timeMs = timeMs;
        this.row = row;
        this.type = type;
    }
}

