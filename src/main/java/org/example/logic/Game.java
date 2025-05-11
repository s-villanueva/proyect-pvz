package org.example.logic;

import lombok.Getter;
import lombok.Setter;
import org.example.model.attack.*;
import org.example.model.plant.*;
import org.example.model.zombie.ConeheadZombie;
import org.example.model.zombie.Zombie;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Getter
@Setter
public class Game {
    // Constantes de dimensiones de las plantas y ataques
    public static final int PEA_SHOOTER_WIDTH = 50;
    public static final int PEA_SHOOTER_HEIGHT = 70;
    public static final int PEA_WIDTH = 20;
    public static final int PEA_HEIGHT = 20;
    public static final int CHERRY_BOMB_WIDTH = 60;
    public static final int CHERRY_BOMB_HEIGHT = 60;
    private final IGameEvents iGameEvents;
    private LawnMower[] lawnMowers = new LawnMower[5];

    // Variables para la posición de inicio
    private int posStartX = 0;
    private int posStartY = 0;

    // Acumulación de soles
    private int accumulatedSuns = 5000;

    // Listas de plantas, ataques, soles y zombis

    private final List<Plant> availablePlants;

    private final List<Plant> plants;

    private final List<Attack> attacks;

    private final List<Sun> suns = Collections.synchronizedList(new ArrayList<>());

    private final List<Zombie> zombies = Collections.synchronizedList(new ArrayList<>());

    // Matrices para las plantas y zombis en el tablero
    private boolean[][] plantsInBoard = new boolean[5][9];
    private boolean[][] zombiesInBoard = new boolean[5][1];  // Solo una columna por ahora para los zombis

    private volatile Plant selectedPlant = null;

    public Game(IGameEvents iGameEvents) {
        this.availablePlants = new ArrayList<>();
        this.plants = new CopyOnWriteArrayList<>();
        this.attacks = Collections.synchronizedList(new ArrayList<>());
        this.iGameEvents = iGameEvents;
        this.posStartX = 100;
        this.posStartY = 100;
        for (int i = 0; i < 5; i++) {
            lawnMowers[i] = new LawnMower(70, 110 + i * 120 + 20, i,this);
        }
    }

    // --- Métodos para manejar las plantas ---

    // Agregar planta al tablero
    public void addPlant(int row, int col, Plant plant) {
        if (plant == null || row < 0 || row >= 5 || col < 0 || col >= 9) return;

        // Verificar si hay suficientes soles para la planta seleccionada
        int plantCost = plant.getSunCost();  // Asegúrate de que Plant tenga un método getCost()

        synchronized (plantsInBoard) {
            if (plantsInBoard[row][col]) {
                selectedPlant = null;  // Deseleccionar si la celda está ocupada
                return;
            }
        }

        if (accumulatedSuns >= plantCost) {
            accumulatedSuns -= plantCost;
            plants.add(plant);
            synchronized (plantsInBoard) {
                plantsInBoard[row][col] = true;
            }
            iGameEvents.addPlantUI(plant);
            iGameEvents.updateSunCounter(accumulatedSuns);  // Actualizar contador en la UI
        } else {
            // No hay soles suficientes: deseleccionar la planta
            selectedPlant = null;
        }
    }


    public void removePlant(Plant plant) {
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
        List<Zombie> zombiesToRemove = new ArrayList<>();

        synchronized (zombies) {
            for (Zombie z : new ArrayList<>(zombies)) {

                boolean collided = false;
                for (Plant plant : new ArrayList<>(plants)) {
                    if (intersects(plant, z)) {
                        z.attackPlant(plant);
                        collided = true;
                        break;
                    }
                }

                if (!collided) {
                    z.advance();
                    iGameEvents.updatePositionUI(z.getId());

                    if (z instanceof ConeheadZombie coneZombie && !coneZombie.isConeIntact()) {
//                        iGameEvents.changeZombieToNormal(z.getId());
                    }
                }

                if (z.isDead()) {
                    iGameEvents.removeZombieUI(z.getId());
                    zombiesToRemove.add(z);
                    continue;
                }

                if (z.getX() <= 0) {
                    iGameEvents.deleteComponentUI(z.getId());
                    zombiesToRemove.add(z);
                    continue;
                }

                if (lawnMowers[z.getRow()] != null) {
                    LawnMower mower = lawnMowers[z.getRow()];
                    if (!mower.isActive() && z.getX() <= mower.getX() + 30) {
                        mower.setActive(true);
                    }
                }
            }

            // Mover podadoras activas y matar zombis en su fila
            for (int row = 0; row < lawnMowers.length; row++) {
                LawnMower mower = lawnMowers[row];
                if (mower == null)
                    continue;
                if (mower.isActive()) {
                    mower.move();
                    iGameEvents.updateLawnMowerUI(row);

                    for (Zombie z : new ArrayList<>(zombies)) {
                        if (z.getRow() == row && z.getX() <= mower.getX() + 60) {
                            z.takeDamage(z.getHealth());
                            iGameEvents.removeZombieUI(z.getId());
                            zombiesToRemove.add(z);
                        }
                    }

                    if (mower.getX() > 1000) {
                        mower.setActive(false);
                        lawnMowers[row] = null;
                    }
                }
            }
            zombies.removeAll(zombiesToRemove);
        }
    }




    // --- Métodos para manejar los ataques ---

    // Revisión de plantas, generación de ataques
    public void reviewPlants() {
        long currentTime = System.currentTimeMillis();

        for (Plant plant : plants) {
            if (plant.isDead()){
                iGameEvents.deleteComponentUI(plant.getId());
            }
            if (plant instanceof PeaShooter ps) {
                if (currentTime - ps.getPrevTime() > ps.getAttackTime()) {
                    ps.setPrevTime(currentTime);
                    GreenPea p = newGreenPea(ps);
                    synchronized (attacks) {
                        attacks.add(p);
                    }
                    iGameEvents.throwAttackUI(p);
                }

//            } else if (plant instanceof CherryBomb cb) {
//                if (!cb.isExploded() && (currentTime - cb.getPrevTime() >= cb.getExplosionTime())) {
//                    cb.explode();
//                    iGameEvents.explosionUI(cb);
//                    Timer timer = new Timer(1000, e -> {
//                        plants.remove(cb);
//
//                        synchronized (plantsInBoard) {
//                            int row = (cb.getY() - posStartY) / CHERRY_BOMB_HEIGHT;
//                            int col = (cb.getX() - posStartX) / CHERRY_BOMB_WIDTH;
//                            if (row >= 0 && row < 5 && col >= 0 && col < 9) {
//                                plantsInBoard[row][col] = false;
//                            }
//                        }
//                        iGameEvents.deleteComponentUI(cb.getId());
//                    });
//                    timer.setRepeats(false);
//                    timer.start();
//                }
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
            else if (plant instanceof SunFlower sf) {
                if (currentTime - sf.getPrevTime() >= sf.getSunProductionInterval()) {
                    sf.setPrevTime(currentTime);
                    // Generar un sol en la posición del girasol
                    Sun sun = new Sun(sf.getX(), sf.getY(), 60, 60);
                    suns.add(sun);
                    iGameEvents.addSunUI(sun);

                    // Eliminar el sol después de 4 segundos
                    new Thread(() -> {
                        try {
                            Thread.sleep(8000);
                            synchronized (suns) {
                                if (suns.contains(sun)) {
                                    suns.remove(sun);
                                    iGameEvents.deleteComponentUI(sun.getId());
                                }
                            }
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }).start();
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
                boolean collided = false;

                if (attack instanceof GreenPea gp) {
                    if (currentTime - gp.getPrevTime() > gp.getAdvanceTime()) {
                        gp.avanzar();

                        for (Zombie zombie : zombies) {
                            int attackRow = (attack.getY() - posStartY) / 120;
                            int zombieRow = (zombie.getY() - posStartY) / 120;

                            if (attackRow == zombieRow && intersects(attack, zombie)) {
                                zombie.takeDamage(attack.getAttack());
                                toRemove.add(attack);
                                iGameEvents.deleteComponentUI(attack.getId());
                                collided = true;
                                break;
                            }
                        }

                        if (!collided) {
                            if (gp.getX() > gp.getMaxXToDied()) {
                                toRemove.add(gp);
                                iGameEvents.deleteComponentUI(gp.getId());
                            } else {
                                iGameEvents.updatePositionUI(gp.getId());
                            }
                        }

                        gp.setPrevTime(currentTime);
                    }

                } else if (attack instanceof SnowPea sp) {
                    if (currentTime - sp.getPrevTime() > sp.getAdvanceTime()) {
                        sp.avanzar();

                        for (Zombie zombie : zombies) {
                            int attackRow = (attack.getY() - posStartY) / 120;
                            int zombieRow = (zombie.getY() - posStartY) / 120;

                            if (attackRow == zombieRow && intersects(attack, zombie)) {
                                zombie.takeDamage(attack.getAttack());
                                toRemove.add(attack);
                                iGameEvents.deleteComponentUI(attack.getId());
                                collided = true;
                                break;
                            }
                        }

                        if (!collided) {
                            if (sp.getX() > sp.getMaxXToDied()) {
                                toRemove.add(sp);
                                iGameEvents.deleteComponentUI(sp.getId());
                            } else {
                                iGameEvents.updatePositionUI(sp.getId());
                            }
                        }
                        sp.setPrevTime(currentTime);
                    }
                }
            }
            attacks.removeAll(toRemove);
        }
    }

    private boolean intersects(Attack attack, Zombie zombie) {
        return attack.getX() < zombie.getX() + zombie.getWidth() &&
                attack.getX() + attack.getWidth() > zombie.getX() &&
                attack.getY() < zombie.getY() + zombie.getHeight() &&
                attack.getY() + attack.getHeight() > zombie.getY();
    }

    private boolean intersects(Plant plant, Zombie zombie) {
        return plant.getX() < zombie.getX() + zombie.getWidth() &&
                plant.getX() + plant.getWidth() > zombie.getX() &&
                plant.getY() < zombie.getY() + zombie.getHeight() &&
                plant.getY() + plant.getHeight() > zombie.getY();
    }

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
