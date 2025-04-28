package org.example.logic;

import lombok.Getter;
import org.example.model.attack.Attack;
import org.example.model.attack.GreenPea;
import org.example.model.attack.SnowPea;
import org.example.model.attack.Sun;
import org.example.model.plant.*;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class Game {
    public static final int PEA_SHOOTER_WIDTH = 50;
    public static final int PEA_SHOOTER_HEIGHT = 70;
    public static final int PEA_WIDTH = 20;
    public static final int PEA_HEIGHT = 20;
    public static final int CHERRY_BOMB_WIDTH = 60; // Define el tamaño de la CherryBomb
    public static final int CHERRY_BOMB_HEIGHT = 60;

    private final IGameEvents iGameEvents;
    private int posStartX = 0;
    private int posStartY = 0;
    private int accumulatedSuns = 0;
    private final boolean[][] plantsInBoard;

    @Getter
    private final List<Plant> availablePlants;
    @Getter
    private final List<Plant> plants;
    @Getter
    private final List<Attack> attacks;
    @Getter
    private final List<Sun> suns = Collections.synchronizedList(new ArrayList<>());

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

    public void deletePlant(Plant plant) {
        plants.remove(plant);
        
        synchronized (plantsInBoard) {
            int row = (plant.getY() - posStartY) / plant.getHeight();
            int col = (plant.getX() - posStartX) / plant.getWidth();
            if (row >= 0 && row < 5 && col >= 0 && col < 9) {
                plantsInBoard[row][col] = false;
            }
        }
        iGameEvents.deleteComponentUI(plant.getId());
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
            } else if (plant instanceof CherryBomb cb) {
                if (!cb.isExploded() && (currentTime - cb.getPrevTime() >= cb.getExplosionTime())) {
                    cb.explode(); 
                    iGameEvents.explosionUI(cb);  
                    Timer timer = new Timer(1000, e -> {
                        plants.remove(cb);

                        synchronized (plantsInBoard) {
                            int row = (cb.getY() - posStartY) / CHERRY_BOMB_HEIGHT;
                            int col = (cb.getX() - posStartX) / CHERRY_BOMB_WIDTH;
                            if (row >= 0 && row < 5 && col >= 0 && col < 9) {
                                plantsInBoard[row][col] = false;
                            }
                        }
                        iGameEvents.deleteComponentUI(cb.getId());
                    });
                    timer.setRepeats(false);
                    timer.start();
                }

            } else if (plant instanceof SnowPeaShooter sps) {
                if (currentTime - sps.getPrevTime() > sps.getAttackTime()) {
                    sps.setPrevTime(currentTime);
                    SnowPea sp = newSnowPea(sps);
                    synchronized (attacks) {
                        attacks.add(sp);
                    }
                    iGameEvents.throwAttackUI(sp);
                }
            }

        }
    }

    public void damagePlant(Plant plant, int damage) {
        if (plant instanceof WallNut wn) {
            wn.takeDamage(damage);
            if (wn.isDead()) {
                plants.remove(wn);
                synchronized (plantsInBoard) {
                    int row = (wn.getY() - posStartY) / PEA_SHOOTER_HEIGHT;
                    int col = (wn.getX() - posStartX) / PEA_SHOOTER_WIDTH;
                    if (row >= 0 && row < 5 && col >= 0 && col < 9) {
                        plantsInBoard[row][col] = false;
                    }
                }
                iGameEvents.deleteComponentUI(wn.getId());
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
                } else if (attack instanceof SnowPea sp) {
                    if (currentTime - sp.getPrevTime() > sp.getAdvanceTime()) {
                        sp.avanzar();
                        if (sp.getX() > sp.getMaxXToDied()) {
                            toRemove.add(attack);
                            iGameEvents.deleteComponentUI(attack.getId());
                        } else {
                            iGameEvents.updatePositionUI(sp.getId());
                        }
                        sp.setPrevTime(currentTime);
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

    private SnowPea newSnowPea(SnowPeaShooter plant) {
        int x = plant.getX() + plant.getWidth();
        int y = plant.getY() + (PEA_SHOOTER_HEIGHT / 4) - (PEA_WIDTH / 2) + 4;
        SnowPea sp = new SnowPea(x, y, PEA_WIDTH, PEA_HEIGHT);
        sp.setMaxXToDied(800);
        return sp;
    }

    public void generateSun() {
        int randomCol = (int) (Math.random() * 9);
        int randomRow = (int) (Math.random() * 5);

        int x = 100 + randomCol * 100;
        int y = 100 + randomRow * 120;

        Sun sun = new Sun(x, y, 60, 60);
        suns.add(sun);
        iGameEvents.addSunUI(sun); // para que aparezca el dibujo en el Frame

        // Aquí programamos que se borre solo después de 4 segundos
        new Thread(() -> {
            try {
                Thread.sleep(4000); // espera 4 segundos
                if (suns.contains(sun)) {
                    suns.remove(sun); // lo eliminamos del modelo
                    iGameEvents.deleteComponentUI(sun.getId()); // eliminamos su imagen
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }


    public void collectSun(String id) {
        synchronized (suns) {
            suns.removeIf(s -> s.getId().equals(id));
            accumulatedSuns += 25;
            iGameEvents.updateSunCounter(accumulatedSuns);
        }
    }


    @Getter
    private volatile Plant selectedPlant = null;

    public void selectPlant(Plant plant) {
        this.selectedPlant = plant;
    }

}
