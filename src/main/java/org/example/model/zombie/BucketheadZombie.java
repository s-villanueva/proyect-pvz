package org.example.model.zombie;

import org.example.logic.Game;

public class BucketheadZombie extends Zombie {
    private boolean bucketIntact = true;

    public BucketheadZombie(int x, int y, int row, Game game) {
        super(x, y, 80, 100, row, game);
        setHealth(300);
        setSpeed(1);
    }

    public boolean isBucketIntact() {
        return bucketIntact;
    }

    @Override
    public void advance() {
        long now = System.currentTimeMillis();

        boolean isFrozenNow = isFrozen() && now < getFrozenUntil();

        long interval = isFrozenNow ? getMoveInterval() * 2 : getMoveInterval();

        if (now - getLastAttackTime() >= interval) {
            setX(getX() - getSpeed());
            setLastAttackTime(now);
        }

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

    @Override
    public void takeDamage(int damage) {
        setHealth(getHealth() - damage);
        if (bucketIntact && getHealth() <= 100) {
            bucketIntact = false;
        }
    }
}
