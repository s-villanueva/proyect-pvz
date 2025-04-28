package org.example.ui;

import org.example.logic.Game;
import org.example.logic.IGameEvents;
import org.example.model.attack.Attack;
import org.example.model.attack.GreenPea;
import org.example.model.attack.SnowPea;
import org.example.model.plant.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Frame extends JFrame implements IGameEvents {
    private Game game;
    private JLabel floatingPreview = new JLabel();

    public Frame() {
        setTitle("Plantas vs Zombies");
        setSize(1010, 725);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(null);

        game = new Game(this);

        // Menú de selección de plantas
        MenuPanel menuPanel = new MenuPanel("SelectionMenu.png"); // tu imagen en resources
        menuPanel.setBounds(90, 0, 700, 80); // ubica el panel arriba del todo

        JButton sunFlowerButton = new JButton(new ImageIcon("SunflowerSeed.png"));
        sunFlowerButton.setContentAreaFilled(false);
        sunFlowerButton.setPreferredSize(new Dimension(60, 60));
        sunFlowerButton.addActionListener(e -> {
            int dummyX = 0, dummyY = 0;
            game.selectPlant(new SunFlower(dummyX, dummyY, 40, 60));
        });
        menuPanel.add(sunFlowerButton);

        JButton peaShooterButton = new JButton(new ImageIcon("PeaShooterSeed.png"));
        peaShooterButton.setContentAreaFilled(false);
        peaShooterButton.setPreferredSize(new Dimension(60, 60));
        peaShooterButton.addActionListener(e -> {
            int dummyX = 0, dummyY = 0;
            game.selectPlant(new PeaShooter(dummyX, dummyY, Game.PEA_SHOOTER_WIDTH, Game.PEA_SHOOTER_HEIGHT));
        });

        menuPanel.add(peaShooterButton);

        JButton wallNutButton = new JButton(new ImageIcon("WallNutSeed.png"));
        wallNutButton.setContentAreaFilled(false);
        wallNutButton.setPreferredSize(new Dimension(60, 60));
        wallNutButton.addActionListener(e -> {
            int dummyX = 0, dummyY = 0;
            game.selectPlant(new WallNut(dummyX, dummyY, 50, 60));
        });

        menuPanel.add(wallNutButton);

        JButton cherryBombButton = new JButton(new ImageIcon("CherryBombButton.png"));
        cherryBombButton.setContentAreaFilled(false);
        cherryBombButton.setPreferredSize(new Dimension(60, 60));
        cherryBombButton.addActionListener(e -> {
            int dummyX = 0, dummyY = 0;
            game.selectPlant(new CherryBomb(dummyX, dummyY, 50, 60, this));
        });

        menuPanel.add(cherryBombButton);

        JButton snowPeaShooterButton = new JButton(new ImageIcon("SnowPeaShooterSeed.png"));
        snowPeaShooterButton.setContentAreaFilled(false);
        snowPeaShooterButton.setPreferredSize(new Dimension(60, 60));
        snowPeaShooterButton.addActionListener(e -> {
            int dummyX = 0, dummyY = 0;
            game.selectPlant(new SnowPeaShooter(dummyX, dummyY, 50, 70));
        });

        menuPanel.add(snowPeaShooterButton);

        add(menuPanel);


        // Crear una instancia de Background
        Background background = new Background();
        background.setBounds(0, 0, getWidth(), getHeight()+20);
        background.setBounds(0, 0, getWidth(), getHeight());
        background.setGame(game);
        add(background);

        floatingPreview.setSize(60, 60); // tamaño default
        floatingPreview.setVisible(false);
        add(floatingPreview, 0);

        // Agregar el listener de mouse para agregar plantas
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

                    game.addPlant(row, col, selected);
                    game.selectPlant(null); // deselecciona
                }
            }
        });


        // Resto de hilos para animaciones...
        new Thread(() -> {
            while (true) {
                game.reviewPlants();
                game.reviewAttacks();
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        setVisible(true);
    }

    @Override
    public void addPlantUI(Plant p) {
        JComponent drawing = null;
        if (p instanceof PeaShooter ps) {
            drawing = new PeaShooterDrawing(ps);
        } else if (p instanceof SunFlower sf) {
            drawing = new SunFlowerDrawing(sf);
        } else if (p instanceof WallNut wn) {  // Añadimos WallNut
            drawing = new WallNutDrawing(wn);  // Usamos el dibujo de WallNut
        } else if (p instanceof  CherryBomb cb) {
            drawing = new CherryBombDrawing(cb);
        } else if (p instanceof SnowPeaShooter sps) {
            drawing = new SnowPeaShooterDrawing(sps);
        }
        if (drawing != null) {
            getContentPane().add(drawing, 0);  // Añadir la planta al panel
            drawing.repaint();  // Redibujamos para mostrarla
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
        }
    }

    @Override
    public void deleteComponentUI(String id) {
        Component c = getComponentById(id);
        if (c != null) {
            getContentPane().remove(c);
        }
    }

    @Override
    public void updateHealthUI(int id, int health) {

    }

    @Override
    public void explosionUI(CherryBomb cherryBomb) {
        CherryBombDrawing cbDrawing = new CherryBombDrawing(cherryBomb);  // Crear el dibujo de la CherryBomb
        getContentPane().add(cbDrawing, 0);  // Añadir el dibujo al contenedor de la ventana
        cbDrawing.repaint();  // Redibujar para mostrar la explosión

        // Mostrar la animación de la explosión
        Timer timer = new Timer(100, new ActionListener() {
            int explosionFrame = 0;

            @Override
            public void actionPerformed(ActionEvent e) {
                if (explosionFrame == 0) {
                    // Iniciar la animación de la explosión
                    cbDrawing.setExplosion(true);  // Iniciar la animación de explosión
                    cbDrawing.repaint();
                    explosionFrame++;
                } else if (explosionFrame == 1) {
                    // Eliminar el dibujo después de que la explosión ha terminado
                    getContentPane().remove(cbDrawing);  // Quitar el dibujo de la explosión
                    getContentPane().repaint();  // Redibujar la ventana

                    // Detener el temporizador después de 1 segundo (asumimos que el GIF dura 1 segundo)
                    ((Timer) e.getSource()).stop();  // Detener el temporizador
                }
            }
        });
        timer.start();  // Comienza la animación de la explosión
    }


    public Component getComponentById(String id) {
        for (Component c : getContentPane().getComponents()) {
            if (c instanceof GreenPeaDrawing pd && pd.getId().equals(id)) {
                return pd;
            } else if (c instanceof SnowPeaDrawing spd && spd.getId().equals(id)) {
                return spd;
            }
        }
        return null;
    }




    public static void main(String[] args) {
        new Frame();
    }
}
