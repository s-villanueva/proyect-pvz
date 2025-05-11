package org.example.model.zombie;

import org.example.logic.Game;

public class ConeheadZombie extends Zombie {
    private boolean coneIntact = true;

    public ConeheadZombie(int x, int y, int row, Game game) {
        super(x, y, 80, 100, row, game); // Usa mismo tamaño que BasicZombie
        setHealth(200); // Salud total
        setSpeed(1);    // Misma velocidad que básico
    }

    public boolean isConeIntact() {
        return coneIntact;
    }

    @Override
    public void advance() {
        System.out.println("ConeheadZombie advance");
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
        if (coneIntact && getHealth() <= 100) {
            coneIntact = false;
            // Opcional: cambiar sprite visual al perder el cono
            Game game = getGame();
//            if (game != null) {
//                game.getIGameEvents().updateZombieSprite(getId(),coneIntact);
//            }
        }
    }
}
