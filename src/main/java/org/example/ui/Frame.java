package org.example.ui;

import org.example.logic.Game;
import org.example.logic.IGameEvents;
import org.example.model.Audio.AudioName;
import org.example.model.ShovelButton;
import org.example.model.ZombieSpawn;
import org.example.model.attack.*;
import org.example.model.plant.*;
import org.example.model.zombie.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Random;

public class Frame extends JFrame implements IGameEvents {
    private final Game game;
    private final MenuPanel menuPanel;
    private final ShovelButton shovelButton = new ShovelButton();
    private static final int CELL_WIDTH = 100;
    private static final int CELL_HEIGHT = 120;
    private static final int OFFSET_X = 100;
    private static final int OFFSET_Y = 100;
    private JPanel shovelPanel;
    private AudioManager audioManager;

    // Zombie wave setup
    private final ZombieSpawn[] zombieWave = new ZombieSpawn[]{
            new ZombieSpawn(1000, 0, "basic"),
            new ZombieSpawn(5000, 2, "basic"),
            new ZombieSpawn(10000, 1, "cone"),
            new ZombieSpawn(15000, 4, "basic"),
            new ZombieSpawn(20000, 0, "bucket"),
            new ZombieSpawn(25000, 3, "cone"),
            new ZombieSpawn(30000, 2, "bucket"),
            new ZombieSpawn(35000, 1, "basic"),
    };

    public Frame() {
        setTitle("Plantas vs Zombies");
        setSize(1010, 725);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(null);

        // Initialize game and audio manager
        game = new Game(this);
        audioManager = new AudioManager();

        // Set up menu panel
        menuPanel = new MenuPanel("SelectionMenu.png");
        menuPanel.setBounds(90, 0, 700, 80);
        configureMenuButtons();
        add(menuPanel);

        // Set up shovel panel
        shovelPanel = new ImagePanel();
        shovelPanel.setBounds(790, 0, 80, 80);
        shovelPanel.setLayout(new FlowLayout());
        setupShovelButton();
        shovelPanel.setVisible(true);
        add(shovelPanel);

        // Set background
        Background background = new Background();
        background.setBounds(0, 0, getWidth(), getHeight());
        background.setGame(game);
        add(background);

        // Configure mouse listener
        configureMouseListener(background);

        // Start game threads
        startGameThreads();

        // Set window visibility
        setVisible(true);

        // Add lawn mowers to the game
        addLawnMowers();
    }

    private void configureMenuButtons() {
        JButton sunFlowerButton = createPlantButton("SunflowerSeed.png", e -> game.selectPlant(new SunFlower(0, 0, 40, 60)));
        JButton peaShooterButton = createPlantButton("PeaShooterSeed.png", e -> game.selectPlant(new PeaShooter(0, 0, Game.PEA_SHOOTER_WIDTH, Game.PEA_SHOOTER_HEIGHT)));
        JButton wallNutButton = createPlantButton("WallNutSeed.png", e -> game.selectPlant(new WallNut(0, 0, 50, 60)));
        JButton cherryBombButton = createPlantButton("CherryBombButton.png", e -> game.selectPlant(new CherryBomb(0, 0, 50, 60, this.game)));
        JButton snowPeaShooterButton = createPlantButton("SnowPeaShooterSeed.png", e -> game.selectPlant(new SnowPeaShooter(0, 0, 50, 70)));

        menuPanel.add(sunFlowerButton);
        menuPanel.add(peaShooterButton);
        menuPanel.add(wallNutButton);
        menuPanel.add(cherryBombButton);
        menuPanel.add(snowPeaShooterButton);
    }

    private JButton createPlantButton(String imagePath, ActionListener listener) {
        JButton button = new JButton(new ImageIcon(imagePath));
        button.setContentAreaFilled(false);
        button.setPreferredSize(new Dimension(60, 60));
        button.addActionListener(listener);
        return button;
    }

    private void setupShovelButton() {
        shovelButton.setOpaque(false);
        shovelButton.setContentAreaFilled(true);
        shovelButton.setBorderPainted(true);
        shovelButton.setFocusPainted(false);
        shovelButton.setRolloverEnabled(true);
        shovelButton.setBackground(new Color(Color.TRANSLUCENT));
        shovelPanel.add(shovelButton);
    }

    private void configureMouseListener(Background background) {
        background.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int mouseX = e.getX();
                int mouseY = e.getY();
                int col = (mouseX - OFFSET_X) / CELL_WIDTH;
                int row = (mouseY - OFFSET_Y) / CELL_HEIGHT;

                if (row < 0 || row >= 5 || col < 0 || col >= 9) return;

                if (shovelButton.isActive()) {
                    handleShovelAction(row, col);
                } else {
                    handlePlantAction(row, col);
                }
            }
        });
    }

    private void handleShovelAction(int row, int col) {
        Plant plantToRemove = game.getPlants().stream()
                .filter(p -> p.getRow() == row && p.getCol() == col)
                .findFirst()
                .orElse(null);

        if (plantToRemove != null) {
            game.removePlant(plantToRemove);
            game.freeCell(row, col);
        }
        shovelButton.deactivate();
    }

    private void handlePlantAction(int row, int col) {
        Plant selected = game.getSelectedPlant();
        if (selected != null) {
            int x = OFFSET_X + col * CELL_WIDTH + (CELL_WIDTH - selected.getWidth()) / 2;
            int y = OFFSET_Y + row * CELL_HEIGHT + (CELL_HEIGHT - selected.getHeight()) / 2;

            selected.setX(x);
            selected.setY(y);
            selected.setRow(row);
            selected.setCol(col);

            game.addPlant(row, col, selected);
            game.selectPlant(null);
        }
    }

    private void startGameThreads() {
        new Thread(() -> {
            long startTime = System.currentTimeMillis();
            int current = 0;

            // Espera inicial de 20 segundos
            try {
                Thread.sleep(20000);  // Esperar 20 segundos
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            game.getIGameEvents().playAudio(AudioName.ZOMBIES_INCOMING);

            while (current < zombieWave.length && game.isRunning()) {
                long now = System.currentTimeMillis() - startTime;

                if (zombieWave[current].getTimeMs() <= now) {
                    spawnZombie(zombieWave[current]);
                    current++;

                    // Esperar 10 segundos entre la aparición de cada zombi
                    try {
                        Thread.sleep(10000);  // Esperar 10 segundos entre cada zombi
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        Thread.sleep(100);  // Revisar cada 100 ms si es hora de generar el siguiente zombi
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }

            waitForZombiesToDie();

        }).start();


        new Thread(() -> {
            while (game.isRunning()) {
                game.reviewPlants();
                game.reviewAttacks();
                game.reviewZombies();

                if (game.isBadEndGame()) {
                    game.badEndGame();
                    break;
                }

                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        new Thread(() -> {
            while (game.isRunning()) {
                try {
                    Thread.sleep(12000);
                    game.generateSun();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void spawnZombie(ZombieSpawn spawn) {
        Zombie z;
        int y = 100 + spawn.getRow() * 120 + 10;
        switch (spawn.getType()) {
            case "cone" -> z = new ConeheadZombie(900, y, spawn.getRow(), game);
            case "bucket" -> z = new BucketheadZombie(900, y, spawn.getRow(), game);
            default -> z = new BasicZombie(900, y, spawn.getRow(), game);
        }
        game.addZombie(z);
    }

    private void waitForZombiesToDie() {
        while (game.isRunning() && game.getZombies().size() > 0) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        if (game.isRunning()) {
            game.winGame();
        }
    }

    public void addLawnMowers() {
        for (int i = 0; i < 5; i++) {
            LawnMower mower = game.getLawnMowers()[i];
            LawnMowerDrawing drawing = new LawnMowerDrawing(mower, i);
            getContentPane().add(drawing, 0);
        }
    }

    private Component getLawnMowerByRow(int row) {
        for (Component c : getContentPane().getComponents()) {
            if (c instanceof LawnMowerDrawing lmd && lmd.getRow() == row) {
                return lmd;
            }
        }
        return null;
    }

    public Component getComponentById(String id) {
        for (Component c : getContentPane().getComponents()) {
            if (c instanceof GreenPeaDrawing pd && pd.getId().equals(id)) {
                return pd;
            } else if (c instanceof SnowPeaDrawing spd && spd.getId().equals(id)) {
                return spd;
            } else if (c instanceof SunDrawing sd && sd.getId().equals(id)) {
                return sd;
            } else if (c instanceof ZombieDrawing zd && zd.getId().equals(id)) {
                return zd;
            } else if (c instanceof ConeheadZombieDrawing chz && chz.getId().equals(id)) {
                return chz;
            } else if (c instanceof BucketheadZombieDrawing bhz && bhz.getId().equals(id)) {
                return bhz;
            } else if (c instanceof LawnMowerDrawing lmd && lmd.getId().equals(id)) {
                return lmd;
            } else if (c instanceof PeaShooterDrawing psd && psd.getId().equals(id)) {
                return psd;
            } else if (c instanceof SunFlowerDrawing sfd && sfd.getId().equals(id)) {
                return sfd;
            } else if (c instanceof WallNutDrawing wnd && wnd.getId().equals(id)) {
                return wnd;
            } else if (c instanceof SnowPeaShooterDrawing spsd && spsd.getId().equals(id)) {
                return spsd;
            } else if (c instanceof CherryBombDrawing cbd && cbd.getId().equals(id)) {
                return cbd;
            }
        }
        return null;
    }

    // --- Métodos de IGameEvents ---
    @Override
    public void addPlantUI(Plant p) {
        JComponent drawing = null;
        if (p instanceof PeaShooter ps) {
            drawing = new PeaShooterDrawing(ps);
        } else if (p instanceof SunFlower sf) {
            drawing = new SunFlowerDrawing(sf);
        } else if (p instanceof WallNut wn) {
            drawing = new WallNutDrawing(wn);
        } else if (p instanceof CherryBomb cb) {
            drawing = new CherryBombDrawing(cb);
        } else if (p instanceof SnowPeaShooter sps) {
            drawing = new SnowPeaShooterDrawing(sps);
        }
        if (drawing != null) {
            getContentPane().add(drawing, 0);
            drawing.repaint();
        }
    }

    @Override
    public void throwAttackUI(Attack attack) {
        if (attack instanceof GreenPea greenPea) {
            GreenPeaDrawing pd = new GreenPeaDrawing(greenPea);
            getContentPane().add(pd, 0);
            pd.repaint();
        } else if (attack instanceof SnowPea snowPea) {
            SnowPeaDrawing spd = new SnowPeaDrawing(snowPea);
            getContentPane().add(spd, 0);
            spd.repaint();
        }
    }

    @Override
    public void updatePositionUI(String id) {
        Component c = getComponentById(id);
        if (c instanceof GreenPeaDrawing pd) {
            pd.updatePosition();
        } else if (c instanceof SnowPeaDrawing sps) {
            sps.updatePosition();
        } else if (c instanceof ZombieDrawing zd) {
            zd.updatePosition();
        } else if (c instanceof ConeheadZombieDrawing cnz) {
            cnz.updatePosition();
        } else if (c instanceof BucketheadZombieDrawing bhz) {
            bhz.updatePosition();
        } else if (c instanceof LawnMowerDrawing lmd) {
            lmd.updatePosition();
        } else if (c instanceof CherryBombDrawing cbd) {
            cbd.updatePosition();
        }
    }

    @Override
    public void deleteComponentUI(String id) {
        Component c = getComponentById(id);
        if (c != null) {
            getContentPane().remove(c);
            getContentPane().revalidate();
            getContentPane().repaint();
        }
    }

    @Override
    public void removeZombieUI(String id) {
        Component c = getComponentById(id);
        if (c != null) {
            getContentPane().remove(c);
            getContentPane().repaint();
        }
    }

    @Override
    public void addZombieUI(Zombie z) {
        JComponent drawing;
        if (z instanceof ConeheadZombie cz) {
            drawing = new ConeheadZombieDrawing(cz);
        } else if (z instanceof BucketheadZombie bz) {
            drawing = new BucketheadZombieDrawing(bz);
        } else {
            drawing = new ZombieDrawing(z);
        }
        getContentPane().add(drawing, 0);
        drawing.repaint();
    }

    @Override
    public void updateLawnMowerUI(int row) {
        Component c = getLawnMowerByRow(row);
        if (c instanceof LawnMowerDrawing lmd) {
            lmd.updatePosition();
        }
    }

    @Override
    public void addSunUI(Sun s) {
        SunDrawing sd = new SunDrawing(s);
        getContentPane().add(sd, 0);
        sd.repaint();
        sd.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                game.collectSun(s.getId());
                getContentPane().remove(sd);
                repaint();
            }
        });
    }

    @Override
    public void updateSunCounter(int suns) {
        menuPanel.setSuns(suns);
    }

    @Override
    public void playAudio(AudioName audioName) {
        audioManager.addAudio(audioName);
    }

    public static void main(String[] args) {
        new Frame();
    }
}
