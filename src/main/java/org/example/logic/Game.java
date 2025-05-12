package org.example.logic;

import lombok.Getter;
import lombok.Setter;
import org.example.model.Audio.Audio;
import org.example.model.Audio.AudioName;
import org.example.model.attack.*;
import org.example.model.plant.*;
import org.example.model.zombie.ConeheadZombie;
import org.example.model.zombie.Zombie;

import javax.swing.*;
import java.awt.*;
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

    private long lastPlantEatingSoundTime = 0;
    private static final long PLANT_EATING_COOLDOWN_MS = 500; // 2 segundos de cooldown

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
    private volatile boolean isRunning = true;
    public boolean badEndGame = false;

    private final IGameEvents iGameEvents;
    private LawnMower[] lawnMowers = new LawnMower[5];

    public Game(IGameEvents iGameEvents) {
        this.availablePlants = new ArrayList<>();
        this.plants = new CopyOnWriteArrayList<>();
        this.attacks = Collections.synchronizedList(new ArrayList<>());
        this.iGameEvents = iGameEvents;
        this.posStartX = 100;
        this.posStartY = 100;
        for (int i = 0; i < 5; i++) {
            lawnMowers[i] = new LawnMower(70, 110 + i * 120 + 20, i, this);
        }
    }

    // --- Métodos para manejar las plantas ---

    public void addPlant(int row, int col, Plant plant) {
        if (plant == null || row < 0 || row >= 5 || col < 0 || col >= 9) return;

        int plantCost = plant.getSunCost();

        synchronized (plantsInBoard) {
            if (plantsInBoard[row][col]) {
                selectedPlant = null;
                return;
            }
        }

        if (accumulatedSuns >= plantCost) {
            accumulatedSuns -= plantCost;
            plants.add(plant);
            iGameEvents.playAudio(AudioName.PLANTING);
            synchronized (plantsInBoard) {
                plantsInBoard[row][col] = true;
            }
            iGameEvents.addPlantUI(plant);
            iGameEvents.updateSunCounter(accumulatedSuns);
        } else {
            selectedPlant = null;
        }
    }

    public void removePlant(Plant plant) {
        synchronized (plantsInBoard) {
            int row = (plant.getY() - posStartY) / plant.getHeight();
            int col = (plant.getX() - posStartX) / plant.getWidth();
            if (row >= 0 && row < 5 && col >= 0 && col < 9) {
                plantsInBoard[row][col] = false;
            }
        }
        plants.remove(plant);
        iGameEvents.deleteComponentUI(plant.getId());
    }

    public void selectPlant(Plant plant) {
        this.selectedPlant = plant;
    }

    public void freeCell(int row, int col) {
        plantsInBoard[row][col] = false;
    }

    public int getColFromX(int x) {
        int cellWidth = 100;
        int startX = 250;
        return (x - startX) / cellWidth;
    }

    public void reviewPlants() {
        long currentTime = System.currentTimeMillis();
        List<Plant> plantsToRemove = new ArrayList<>();

        synchronized (plants) {
            for (Plant plant : new ArrayList<>(plants)) {
                if (plant instanceof PeaShooter ps) {
                    // Verificar si hay zombis en la fila antes de disparar
                    boolean zombieInRow = false;
                    int attackRow = (ps.getY() - posStartY) / 120;  // Calcular fila del ataque
                    for (Zombie zombie : zombies) {
                        int zombieRow = (zombie.getY() - posStartY) / 120;
                        if (zombieRow == attackRow) {
                            zombieInRow = true;
                            break;  // Si hay un zombi en la fila, no es necesario seguir buscando
                        }
                    }

                    // Si no hay zombis en la fila, no lanzar el proyectil
                    if (zombieInRow && currentTime - ps.getPrevTime() > ps.getAttackTime()) {
                        ps.setPrevTime(currentTime);
                        GreenPea p = newGreenPea(ps);
                        synchronized (attacks) {
                            attacks.add(p);
                        }
                        iGameEvents.throwAttackUI(p);
                    }

                } else if (plant instanceof SnowPeaShooter sps) {
                    // Verificar si hay zombis en la fila antes de disparar
                    boolean zombieInRow = false;
                    int attackRow = (sps.getY() - posStartY) / 120;  // Calcular fila del ataque
                    for (Zombie zombie : zombies) {
                        int zombieRow = (zombie.getY() - posStartY) / 120;
                        if (zombieRow == attackRow) {
                            zombieInRow = true;
                            break;  // Si hay un zombi en la fila, no es necesario seguir buscando
                        }
                    }

                    // Si no hay zombis en la fila, no lanzar el proyectil
                    if (zombieInRow && currentTime - sps.getPrevTime() > sps.getAttackTime()) {
                        sps.setPrevTime(currentTime);
                        SnowPea sp = newSnowPea(sps);
                        synchronized (attacks) {
                            attacks.add(sp);
                        }
                        iGameEvents.throwAttackUI(sp);
                    }

                } else if (plant instanceof SunFlower sf) {
                    if (currentTime - sf.getPrevTime() >= sf.getSunProductionInterval()) {
                        sf.setPrevTime(currentTime);
                        Sun sun = new Sun(sf.getX(), sf.getY(), 60, 60);
                        synchronized (suns) {
                            suns.add(sun);
                        }
                        iGameEvents.addSunUI(sun);

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
                } else if (plant instanceof CherryBomb cb) {
                    if (!cb.isExploded() && System.currentTimeMillis() - cb.getPlantTime() > 1800) {
                        cb.explode();
                        freeCell(cb.getRow(), cb.getCol());
                    }
                }
                if (plant.getHealth() <= 0) {
                    iGameEvents.playAudio(AudioName.PLANT_ATE);
                    plantsInBoard[plant.getRow()][plant.getCol()] = false;
                    iGameEvents.deleteComponentUI(plant.getId());
                    plantsToRemove.add(plant);

                    int row = plant.getRow();
                    int col = plant.getCol();
                    freeCell(row, col);
                }
            }
            plants.removeAll(plantsToRemove);
        }
    }


    public void badEndGame() {
        // Detener todos los hilos lógicamente (puedes usar una bandera booleana)
        isRunning = false;

        // Mostrar la pantalla de "Has perdido"
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Fin del juego");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(400, 200);
            frame.setLocationRelativeTo(null);

            JLabel label = new JLabel("¡Has perdido!", SwingConstants.CENTER);
            label.setFont(new Font("Arial", Font.BOLD, 28));
            frame.add(label);

            frame.setVisible(true);
            iGameEvents.playAudio(AudioName.ZOMBIES_WIN);
        });
    }

    public void winGame() {
        isRunning = false;

        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("¡Victoria!");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(400, 200);
            frame.setLocationRelativeTo(null);

            JLabel label = new JLabel("¡Has ganado!", SwingConstants.CENTER);
            label.setFont(new Font("Arial", Font.BOLD, 28));
            frame.add(label);

            frame.setVisible(true);
        });
    }

    // --- Métodos para manejar los zombis ---

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

    public void removeZombie(Zombie zombie) {
        zombies.remove(zombie);

        synchronized (zombiesInBoard) {
            int row = (zombie.getY() - posStartY) / zombie.getHeight();
            if (row >= 0 && row < 5) {
                zombiesInBoard[row][0] = false;
            }
        }

        iGameEvents.deleteComponentUI(zombie.getId());
    }

    public void reviewZombies() {
        long currentTime = System.currentTimeMillis();
        List<Zombie> zombiesToRemove = new ArrayList<>();

        synchronized (zombies) {
            for (Zombie z : new ArrayList<>(zombies)) {
                boolean collided = false;
                for (Plant plant : new ArrayList<>(plants)) {
                    if (intersects(plant, z)) {
                        z.attackPlant(plant);
                        long now = System.currentTimeMillis();
                        if (now - lastPlantEatingSoundTime > PLANT_EATING_COOLDOWN_MS) {
                            iGameEvents.playAudio(AudioName.PLANT_EATING);
                            lastPlantEatingSoundTime = now;
                        }
                        collided = true;
                        break;
                    }
                }
                if (!collided && currentTime - z.getLastMoveTime() >= z.getMoveInterval()) {
                    z.advance();
                    iGameEvents.updatePositionUI(z.getId());
                    z.setLastMoveTime(currentTime);
                }

                if (z.isDead()) {
                    iGameEvents.removeZombieUI(z.getId());
                    zombiesToRemove.add(z);
                    continue;
                }

                if (z.getX() <= 0) {
                    iGameEvents.deleteComponentUI(z.getId());
                    zombiesToRemove.add(z);
                    setBadEndGame(true);
                }

                if (lawnMowers[z.getRow()] != null) {
                    LawnMower mower = lawnMowers[z.getRow()];
                    if (!mower.isActive() && z.getX() <= mower.getX() + 30) {
                        mower.setActive(true);
                    }
                }
            }

            for (int row = 0; row < lawnMowers.length; row++) {
                LawnMower mower = lawnMowers[row];
                if (mower == null)
                    continue;
                if (mower.isActive()) {
                    mower.move();
                    iGameEvents.updateLawnMowerUI(row);
                    if (!mower.isPlayed()) {
                        iGameEvents.playAudio(AudioName.LAWN_MOWER);
                        mower.setPlayed(true);
                    }
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

    public void reviewAttacks() {
        long currentTime = System.currentTimeMillis();
        List<Attack> toRemove = new ArrayList<>();

        synchronized (attacks) {
            synchronized (zombies) {
                for (Attack attack : new ArrayList<>(attacks)) {
                    boolean collided = false;

                    if (attack instanceof GreenPea gp) {
                        if (currentTime - gp.getPrevTime() > gp.getAdvanceTime()) {
                            gp.avanzar();

                            for (Zombie zombie : zombies) {
                                int attackRow = (attack.getY() - posStartY) / 120;
                                int zombieRow = (zombie.getY() - posStartY) / 120;

                                if (attackRow == zombieRow && intersects(attack, zombie)) {
                                    iGameEvents.playAudio(AudioName.PEA_IMPACT);
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
                                    iGameEvents.playAudio(AudioName.SNOW_PEA_IMPACT);
                                    zombie.takeDamage(attack.getAttack());
                                    zombie.setFrozen(true);
                                    zombie.setFrozenUntil(System.currentTimeMillis() + 4000); // Congela 4 segundos
                                    zombie.setMoveInterval(1800);
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
    }

    // --- Métodos para manejar los soles ---

    public void generateSun() {
        int randomCol = (int) (Math.random() * 9);
        int randomRow = (int) (Math.random() * 5);

        int x = 100 + randomCol * 100;
        int y = 100 + randomRow * 120;

        Sun sun = new Sun(x, y, 60, 60);
        suns.add(sun);
        iGameEvents.addSunUI(sun);

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

    public void collectSun(String id) {
        synchronized (suns) {
            suns.removeIf(s -> s.getId().equals(id));
            accumulatedSuns += 25;
            iGameEvents.updateSunCounter(accumulatedSuns);
            iGameEvents.playAudio(AudioName.SUN_PICKING);
        }
    }

    // --- Métodos utilitarios ---

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
        iGameEvents.playAudio(AudioName.THROW);
        gp.setMaxXToDied(900);
        return gp;
    }

    private SnowPea newSnowPea(SnowPeaShooter plant) {
        int x = plant.getX() + plant.getWidth();
        int y = plant.getY() + (PEA_SHOOTER_HEIGHT / 4) - (PEA_WIDTH / 2) + 4;
        SnowPea sp = new SnowPea(x, y, PEA_WIDTH, PEA_HEIGHT);
        sp.setMaxXToDied(900);
        iGameEvents.playAudio(AudioName.THROW);
        return sp;
    }
}
