package org.example.model.zombie;

import org.example.logic.Game;

public class BasicZombie extends Zombie {

    private boolean lostArm = false;

    public BasicZombie(int x, int y, int row, Game game) {
        super(x, y, 80, 100, row, game); // Asegúrate de pasar el objeto Game
    }

    @Override
    public void takeDamage(int damage) {
        super.takeDamage(damage);
        if (!lostArm && getHealth() <= 50) {
            lostArm = true;
        }
    }


    @Override
    public void advance() {
        long now = System.currentTimeMillis();

        // Determina si está congelado
        boolean isFrozenNow = isFrozen() && now < getFrozenUntil();

        long interval = isFrozenNow ? getMoveInterval() * 2 : getMoveInterval();

        if (now - getLastAttackTime() >= interval) {
            setX(getX() - getSpeed()); // Desplazarse a la izquierda
            setLastAttackTime(now);
        }

        // Si el tiempo de congelamiento terminó, desactiva el estado frozen
        if (isFrozen() && now >= getFrozenUntil()) {
            setFrozen(false);
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
        if (isDead()) {
            die();
        }
    }
}
