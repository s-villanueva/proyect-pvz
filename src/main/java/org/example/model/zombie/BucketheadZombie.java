package org.example.model.zombie;

import org.example.logic.Game;

public class BucketheadZombie extends Zombie {
    private boolean bucketIntact = true;

    public BucketheadZombie(int x, int y, int row, Game game) {
        super(x, y, 80, 100, row, game); // Usa mismo tamaño que los otros zombis
        setHealth(300); // Más salud por tener el balde
        setSpeed(1);    // Misma velocidad que básico
    }

    public boolean isBucketIntact() {
        return bucketIntact;
    }

    @Override
    public void advance() {
        if (!isAttacking()) {
            setX(getX() - getSpeed());
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

    @Override
    public void takeDamage(int damage) {
        setHealth(getHealth() - damage);
        if (bucketIntact && getHealth() <= 100) {
            bucketIntact = false;
            // Opcional: cambiar sprite visual al perder el balde
            Game game = getGame();
//            if (game != null) {
//                game.getIGameEvents().updateZombieSprite(getId(), bucketIntact);
//            }
        }
    }
}
