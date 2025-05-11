package org.example.ui;

import org.example.logic.Game;
import org.example.logic.IGameEvents;
import org.example.model.attack.*;
import org.example.model.plant.*;
import org.example.model.zombie.BasicZombie;
import org.example.model.zombie.BucketheadZombie;
import org.example.model.zombie.ConeheadZombie;
import org.example.model.zombie.Zombie;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Random;

public class Frame extends JFrame implements IGameEvents {
    private final Game game;
    private JLabel sunCounterLabel;
    private final MenuPanel menuPanel;

    public Frame() {
        setTitle("Plantas vs Zombies");
        setSize(1010, 725);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(null);

        game = new Game(this);

        // Crear y configurar el panel de menú
        menuPanel = new MenuPanel("SelectionMenu.png");
        menuPanel.setBounds(90, 0, 700, 80);
        configureMenuButtons();
        add(menuPanel);

        // Configurar el fondo
        Background background = new Background();
        background.setBounds(0, 0, getWidth(), getHeight());
        background.setGame(game);
        add(background);

        // Agregar listener de mouse para colocar plantas
        configureMouseListener(background);

        // Iniciar hilos para generar zombis, revisar estado de plantas y ataques, y generar soles
        startGameThreads();

        setVisible(true);
        addLawnMowers();
    }

    private void configureMenuButtons() {
        // Crear botones para las plantas
        JButton sunFlowerButton = createPlantButton("SunflowerSeed.png", e -> game.selectPlant(new SunFlower(0, 0, 40, 60)));
        JButton peaShooterButton = createPlantButton("PeaShooterSeed.png", e -> game.selectPlant(new PeaShooter(0, 0, Game.PEA_SHOOTER_WIDTH, Game.PEA_SHOOTER_HEIGHT)));
        JButton wallNutButton = createPlantButton("WallNutSeed.png", e -> game.selectPlant(new WallNut(0, 0, 50, 60)));
//        JButton cherryBombButton = createPlantButton("CherryBombButton.png", e -> game.selectPlant(new CherryBomb(0, 0, 50, 60, this)));
        JButton snowPeaShooterButton = createPlantButton("SnowPeaShooterSeed.png", e -> game.selectPlant(new SnowPeaShooter(0, 0, 50, 70)));

        // Añadir botones al panel
        menuPanel.add(sunFlowerButton);
        menuPanel.add(peaShooterButton);
        menuPanel.add(wallNutButton);
//        menuPanel.add(cherryBombButton);
        menuPanel.add(snowPeaShooterButton);
    }

    private JButton createPlantButton(String imagePath, ActionListener listener) {
        JButton button = new JButton(new ImageIcon(imagePath));
        button.setContentAreaFilled(false);
        button.setPreferredSize(new Dimension(60, 60));
        button.addActionListener(listener);
        return button;
    }

    private void configureMouseListener(Background background) {
        // Agregar listener para añadir plantas en el campo de juego
        background.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                Plant selected = game.getSelectedPlant();
                if (selected == null) return;

                int mouseX = e.getX();
                int mouseY = e.getY();
                int col = (mouseX - 100) / 100;
                int row = (mouseY - 100) / 120;

                if (row >= 0 && row < 5 && col >= 0 && col < 9) {
                    int x = 100 + col * 100 + (100 - selected.getWidth()) / 2;
                    int y = 100 + row * 120 + (120 - selected.getHeight()) / 2;

                    selected.setX(x);
                    selected.setY(y);
                    selected.setRow(row);
                    selected.setCol(col);

                    game.addPlant(row, col, selected);
                    game.selectPlant(null);
                }
            }
        });
    }

    private void startGameThreads() {
        // Generar zombis periódicamente
        new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(5000); // cada 5 segundos
                    int row = new Random().nextInt(5);
                    int y = 100 + row * 120 + 10;
                    game.addZombie(new BasicZombie(900, y, row, game));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        // Generar zombis periódicamente
        new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(25000); // cada 5 segundos
                    int row = new Random().nextInt(5);
                    int y = 100 + row * 120 + 10;
                    game.addZombie(new ConeheadZombie(900, y, row, game));
                    game.addZombie(new BucketheadZombie(900, y, row, game));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();


        // Revisar plantas, ataques y zombis
        new Thread(() -> {
            while (true) {
                game.reviewPlants();
                game.reviewAttacks();
                game.reviewZombies();
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        // Generar soles cada 12 segundos
        new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(12000);
                    game.generateSun();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }

    public void addLawnMowers() {
        for (int i = 0; i < 5; i++) {
            LawnMower mower = game.getLawnMowers()[i];
            LawnMowerDrawing drawing = new LawnMowerDrawing(mower, i);
            getContentPane().add(drawing, 0);
        }
    }


    // Métodos de la interfaz IGameEvents

    @Override
    public void addPlantUI(Plant p) {
        JComponent drawing = null;
        if (p instanceof PeaShooter ps) {
            drawing = new PeaShooterDrawing(ps);
        } else if (p instanceof SunFlower sf) {
            drawing = new SunFlowerDrawing(sf);
        } else if (p instanceof WallNut wn) {
            drawing = new WallNutDrawing(wn);
//        } else if (p instanceof CherryBomb cb) {
//            drawing = new CherryBombDrawing(cb);
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
        } else if (c instanceof BucketheadZombieDrawing bhz){
            bhz.updatePosition();
        } else if (c instanceof LawnMowerDrawing lmd) {
            lmd.updatePosition();
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

    public void removePlantUI(Plant p) {
        Component c = getComponentById(p.getId());
        if (c != null) {
            getContentPane().remove(c);
        }
    }

    @Override
    public void updateLawnMowerUI(int row) {
        Component c = getLawnMowerByRow(row);
        if (c instanceof LawnMowerDrawing lmd) {
            lmd.updatePosition();
        }
    }


    @Override
    public void explosionUI(CherryBomb cherryBomb) {
        CherryBombDrawing cbDrawing = new CherryBombDrawing(cherryBomb);
        getContentPane().add(cbDrawing, 0);
        cbDrawing.repaint();

        // Animación de la explosión
        Timer timer = new Timer(100, new ActionListener() {
            int explosionFrame = 0;

            @Override
            public void actionPerformed(ActionEvent e) {
                if (explosionFrame == 0) {
                    cbDrawing.setExplosion(true);
                    cbDrawing.repaint();
                    explosionFrame++;
                } else if (explosionFrame == 1) {
                    getContentPane().remove(cbDrawing);
                    getContentPane().repaint();
                    ((Timer) e.getSource()).stop();
                }
            }
        });
        timer.start();
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
            }

        }
        return null;
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


    private Component getLawnMowerByRow(int row) {
        for (Component c : getContentPane().getComponents()) {
            if (c instanceof LawnMowerDrawing lmd && lmd.getRow() == row) {
                return lmd;
            }
        }
        return null;
    }


    @Override
    public void updateZombieSprite(String id, boolean coneIntact) {

    }

    public static void main(String[] args) {
        new Frame();
    }
}
