package org.example.logic;

import lombok.Getter;
import org.example.model.attack.Attack;
import org.example.model.attack.GreenPea;
import org.example.model.plant.PeaShooter;
import org.example.model.plant.Plant;
import org.example.model.plant.SunFlower;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.CopyOnWriteArrayList;

public class Game {
    public static final int PEA_SHOOTER_WIDTH = 50;
    public static final int PEA_SHOOTER_HEIGHT = 70;
    public static final int PEA_WIDTH = 20;
    public static final int PEA_HEIGHT = 20;

    private int posStartX = 0;
    private int posStartY = 0;
    private int accumulatedSuns = 0;
    private boolean[][] plantsInBoard;

    @Getter
    private final List<Plant> availablePlants;
    @Getter
    private final List<Plant> plants;
    @Getter
    private final List<Attack> attacks;

    private final IGameEvents iGameEvents;

    public Game(IGameEvents iGameEvents) {
        this.availablePlants = new ArrayList<>(); // editable desde un solo hilo, usualmente
        this.plants = new CopyOnWriteArrayList<>();
        this.attacks = Collections.synchronizedList(new ArrayList<>());
        this.iGameEvents = iGameEvents;
        this.posStartX = 100;
        this.posStartY = 100;
        this.plantsInBoard = new boolean[5][9];
    }

    public void addPlant(int row, int col, Plant plant) {
        if (row < 0 || row >= 5 || col < 0 || col >= 9) return;
        synchronized (plantsInBoard) {
            if (plantsInBoard[row][col]) return;
            plantsInBoard[row][col] = true;
        }
        plants.add(plant);
        iGameEvents.addPlantUI(plant);
    }

    public void reviewPlants() {
        long currentTime = System.currentTimeMillis();

        for (Plant plant : plants) {
            if (plant instanceof PeaShooter ps) {
                if (currentTime - ps.getPrevTime() > ps.getAttackTime()) {
                    ps.setPrevTime(currentTime);
                    GreenPea p = newGreenPea(ps);
                    synchronized (attacks) {
                        attacks.add(p);
                    }
                    iGameEvents.throwAttackUI(p);
                }
            } else if (plant instanceof SunFlower sf) {
                // comportamiento del SunFlower si se desea implementar
            }
        }
    }

    public void reviewAttacks() {
        long currentTime = System.currentTimeMillis();
        List<Attack> toRemove = new ArrayList<>();

        synchronized (attacks) {
            for (Attack attack : attacks) {
                if (attack instanceof GreenPea gp) {
                    if (currentTime - gp.getPrevTime() > gp.getAdvanceTime()) {
                        gp.avanzar();
                        if (gp.getX() > gp.getMaxXToDied()) {
                            toRemove.add(attack);
                            iGameEvents.deleteComponentUI(attack.getId());
                        } else {
                            iGameEvents.updatePositionUI(gp.getId());
                        }
                        gp.setPrevTime(currentTime);
                    }
                }
            }
            attacks.removeAll(toRemove);
        }
    }

    private GreenPea newGreenPea(PeaShooter plant) {
        int x = plant.getX() + plant.getWidth();
        int y = plant.getY() + (PEA_SHOOTER_HEIGHT / 4) - (PEA_WIDTH / 2) + 4;
        GreenPea gp = new GreenPea(x, y, PEA_WIDTH, PEA_HEIGHT);
        gp.setMaxXToDied(800);
        return gp;
    }

    @Getter
    private volatile Plant selectedPlant = null;

    public void selectPlant(Plant plant) {
        this.selectedPlant = plant;
    }

}
