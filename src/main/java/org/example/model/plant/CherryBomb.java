package org.example.model.plant;

import lombok.Getter;
import lombok.Setter;
import org.example.model.Audio.AudioName;
import org.example.logic.Game;
import org.example.model.zombie.Zombie;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class CherryBomb extends Plant {

    private String id;
    private long explosionTime;
    private long plantTime;
    private final long explosionDelay = 300;
    private boolean exploded = false;
    private final Game game;

    public CherryBomb(int x, int y, int width, int height, Game game) {
        this.x = x;
        this.id = UUID.randomUUID().toString();
        this.explosionTime = System.currentTimeMillis();
        this.y = y;
        this.width = width;
        this.height = height;
        this.game = game;
        this.plantTime = System.currentTimeMillis();
        this.setHealth(9999);
    }

    public void explode() {
        exploded = true;
        explosionTime = System.currentTimeMillis();

        int col = game.getColFromX(getX());
        int row = getRow();

        List<Zombie> zombiesSnapshot;
        synchronized (game.getZombies()) {
            zombiesSnapshot = new ArrayList<>(game.getZombies());
        }

        for (int r = row - 1; r <= row + 1; r++) {
            for (int c = col - 1; c <= col + 1; c++) {
                if (r >= 0 && r < 5 && c >= 0 && c < 9) {
                    for (Zombie z : zombiesSnapshot) {
                        if (z.getRow() == r && game.getColFromX(z.getX()) == c) {
                            game.getIGameEvents().playAudio(AudioName.CHERRYBOMBING);
                            z.takeDamage(z.getHealth()); // daño letal
                        }
                    }
                }
            }
        }

        new Thread(() -> {
            try {
                Thread.sleep(1000);
                game.removePlant(this);
                game.getIGameEvents().deleteComponentUI(getId());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }
}
