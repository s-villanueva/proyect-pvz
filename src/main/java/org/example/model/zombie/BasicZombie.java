package org.example.model.zombie;

import org.example.logic.Game;

public class BasicZombie extends Zombie {

    public BasicZombie(int x, int y, int row, Game game) {
        super(x, y, 80, 100, row, game); // Asegúrate de pasar el objeto Game
    }

    @Override
    public void advance() {
        if (!isAttacking()) {
            setX(getX()-getSpeed()); // o el valor de velocidad
        }
    }

    @Override
    public void die() {
        if (getState() == ZombieState.IN_PROGRESS) {
            setState(ZombieState.WAITING);
            Game game = getGame();
            if (game != null) {
                game.removeZombie(this);
                game.getIGameEvents().deleteComponentUI(getId());
            }
        }
    }

    @Override
    public void update() {
        if (!isAlive()) {
            die();
        }
    }
}
