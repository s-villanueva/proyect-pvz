package org.example.logic;

import lombok.Getter;
import org.example.model.attack.Attack;
import org.example.model.attack.GreenPea;
import org.example.model.attack.SnowPea;
import org.example.model.attack.Sun;
import org.example.model.plant.CherryBomb;
import org.example.model.plant.PeaShooter;
import org.example.model.plant.Plant;
import org.example.model.plant.SnowPeaShooter;
import org.example.model.zombie.Zombie;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class Game {
    // Constantes de dimensiones de las plantas y ataques
    public static final int PEA_SHOOTER_WIDTH = 50;
    public static final int PEA_SHOOTER_HEIGHT = 70;
    public static final int PEA_WIDTH = 20;
    public static final int PEA_HEIGHT = 20;
    public static final int CHERRY_BOMB_WIDTH = 60;
    public static final int CHERRY_BOMB_HEIGHT = 60;

    @Getter
    private final IGameEvents iGameEvents;

    // Variables para la posición de inicio
    private int posStartX = 0;
    private int posStartY = 0;

    // Acumulación de soles
    private int accumulatedSuns = 50;

    // Listas de plantas, ataques, soles y zombis
    @Getter
    private final List<Plant> availablePlants;
    @Getter
    private final List<Plant> plants;
    @Getter
    private final List<Attack> attacks;
    @Getter
    private final List<Sun> suns = Collections.synchronizedList(new ArrayList<>());
    @Getter
    private final List<Zombie> zombies = Collections.synchronizedList(new ArrayList<>());

    // Matrices para las plantas y zombis en el tablero
    private final boolean[][] plantsInBoard = new boolean[5][9];
    private final boolean[][] zombiesInBoard = new boolean[5][1];  // Solo una columna por ahora para los zombis

    @Getter
    private volatile Plant selectedPlant = null;

    public Game(IGameEvents iGameEvents) {
        this.availablePlants = new ArrayList<>();
        this.plants = new CopyOnWriteArrayList<>();
        this.attacks = Collections.synchronizedList(new ArrayList<>());
        this.iGameEvents = iGameEvents;
        this.posStartX = 100;
        this.posStartY = 100;
    }

    // --- Métodos para manejar las plantas ---

    // Agregar planta al tablero
    public void addPlant(int row, int col, Plant plant) {
        if (row < 0 || row >= 5 || col < 0 || col >= 9) return;
        synchronized (plantsInBoard) {
            if (plantsInBoard[row][col]) return;  // Si ya hay una planta en la casilla, no agregar otra
            plantsInBoard[row][col] = true;
        }
        plants.add(plant);
        iGameEvents.addPlantUI(plant);  // Actualizar la interfaz de usuario
    }

    // Eliminar planta
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

    // --- Métodos para manejar los zombis ---

    // Eliminar zombi
    public void removeZombie(Zombie zombie) {
        zombies.remove(zombie);

        synchronized (zombiesInBoard) {
            int row = (zombie.getY() - posStartY) / zombie.getHeight();
            if (row >= 0 && row < 5) {
                zombiesInBoard[row][0] = false;  // Solo hay una columna para los zombis
            }
        }

        iGameEvents.deleteComponentUI(zombie.getId());
    }

    // Agregar zombi
    public void addZombie(Zombie z) {
        zombies.add(z);
        for (int row = 0; row < 5; row++) {
            if (!zombiesInBoard[row][0]) {
                zombiesInBoard[row][0] = true;
                break;
            }
        }
        iGameEvents.addZombieUI(z);
    }

    // Revisión de zombis, avance de los mismos
    public void reviewZombies() {
        synchronized (zombies) {
            Iterator<Zombie> iterator = zombies.iterator(); // Sincronización al crear el iterador

            while (iterator.hasNext()) {
                Zombie zombie = iterator.next();
                zombie.advance();

                // Actualización de la posición del zombi en la UI
                iGameEvents.updatePositionUI(zombie.getId());

                // Si el zombi ha llegado al final (X <= 0), se elimina
                if (zombie.getX() <= 0) {
                    iterator.remove();  // Eliminar el zombi de la lista de zombies
                    iGameEvents.deleteComponentUI(zombie.getId());
                }
            }
        }
    }

    // --- Métodos para manejar los ataques ---

    // Revisión de plantas, generación de ataques
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

    // Revisión de los ataques, movimiento de las bolas
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

    // Generación de GreenPea
    private GreenPea newGreenPea(PeaShooter plant) {
        int x = plant.getX() + plant.getWidth();
        int y = plant.getY() + (PEA_SHOOTER_HEIGHT / 4) - (PEA_WIDTH / 2) + 4;
        GreenPea gp = new GreenPea(x, y, PEA_WIDTH, PEA_HEIGHT);
        gp.setMaxXToDied(800);
        return gp;
    }

    // Generación de SnowPea
    private SnowPea newSnowPea(SnowPeaShooter plant) {
        int x = plant.getX() + plant.getWidth();
        int y = plant.getY() + (PEA_SHOOTER_HEIGHT / 4) - (PEA_WIDTH / 2) + 4;
        SnowPea sp = new SnowPea(x, y, PEA_WIDTH, PEA_HEIGHT);
        sp.setMaxXToDied(800);
        return sp;
    }

    // --- Métodos para manejar los soles ---

    // Generación de Sol
    public void generateSun() {
        int randomCol = (int) (Math.random() * 9);
        int randomRow = (int) (Math.random() * 5);

        int x = 100 + randomCol * 100;
        int y = 100 + randomRow * 120;

        Sun sun = new Sun(x, y, 60, 60);
        suns.add(sun);
        iGameEvents.addSunUI(sun);

        // Eliminar el sol después de 4 segundos
        new Thread(() -> {
            try {
                Thread.sleep(4000);
                if (suns.contains(sun)) {
                    suns.remove(sun);
                    iGameEvents.deleteComponentUI(sun.getId());
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    // Método para recolectar el sol
    public void collectSun(String id) {
        synchronized (suns) {
            suns.removeIf(s -> s.getId().equals(id));
            accumulatedSuns += 25;
            iGameEvents.updateSunCounter(accumulatedSuns);
        }
    }

    // Selección de planta
    public void selectPlant(Plant plant) {
        this.selectedPlant = plant;
    }
}
