package org.example.logic;

import lombok.Getter;
import org.example.model.attack.Attack;
import org.example.model.attack.GreenPea;
import org.example.model.plant.CherryBomb;
import org.example.model.plant.PeaShooter;
import org.example.model.plant.Plant;
import org.example.model.plant.WallNut;

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

    public void deletePlant(Plant plant) {
        // Eliminar la planta de la lista de plantas
        plants.remove(plant);

        // Liberar el espacio en el tablero
        synchronized (plantsInBoard) {
            int row = (plant.getY() - posStartY) / plant.getHeight();
            int col = (plant.getX() - posStartX) / plant.getWidth();
            if (row >= 0 && row < 5 && col >= 0 && col < 9) {
                plantsInBoard[row][col] = false;
            }
        }

        // Eliminar la planta de la interfaz de usuario
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
                // Revisar si la CherryBomb debe explotar
                if (!cb.isExploded() && (currentTime - cb.getPrevTime() >= cb.getExplosionTime())) {
                    cb.explode();  // Llamamos a la explosión de la CherryBomb
                    iGameEvents.explosionUI(cb);  // Actualizamos la UI para mostrar la explosión

                    // Programamos la eliminación de la CherryBomb después de la explosión
                    // Suponemos que la explosión dura 1 segundo (1000 ms)
                    Timer timer = new Timer(1000, e -> {
                        // Primero eliminamos la planta de la lista de plantas
                        plants.remove(cb);

                        // Luego, marcamos la celda correspondiente como vacía
                        synchronized (plantsInBoard) {
                            int row = (cb.getY() - posStartY) / CHERRY_BOMB_HEIGHT;
                            int col = (cb.getX() - posStartX) / CHERRY_BOMB_WIDTH;
                            if (row >= 0 && row < 5 && col >= 0 && col < 9) {
                                plantsInBoard[row][col] = false;
                            }
                        }

                        // Actualizamos la interfaz de usuario eliminando el componente
                        iGameEvents.deleteComponentUI(cb.getId());  // Eliminar de la UI
                    });

                    timer.setRepeats(false);
                    timer.start();
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
