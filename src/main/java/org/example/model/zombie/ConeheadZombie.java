package org.example.model.zombie;

import lombok.Getter;
import lombok.Setter;
import org.example.logic.Game;

@Getter
@Setter
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
    public void takeDamage(int dmg) {
        super.takeDamage(dmg);

        if (isConeIntact() && getHealth() < 75) {
            setConeIntact(false); // pierde el cono
        }

        if (!isArmLost() && getHealth() < 40) {
            setArmLost(true); // pierde el brazo
        }
    }

}
