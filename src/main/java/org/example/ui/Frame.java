package org.example.ui;

import org.example.logic.Game;
import org.example.logic.IGameEvents;
import org.example.model.attack.GreenPea;
import org.example.model.plant.PeaShooter;
import org.example.model.plant.Plant;
import org.example.model.plant.SunFlower;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Frame extends JFrame implements IGameEvents {
    private Game game;

    public Frame() {
        setTitle("Plantas vs Zombies - Manual Grid");
        setSize(1010, 735);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(null);

        game = new Game(this);

        // Crear una instancia de Background
        Background background = new Background();
        background.setBounds(0, 0, getWidth(), getHeight());  
        add(background);

        // Agregar el listener de mouse para agregar plantas
        background.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int mouseX = e.getX();
                int mouseY = e.getY();
                int col = (mouseX - 100) / 100;
                int row = (mouseY - 100) / 120;

                if (row >= 0 && row < 5 && col >= 0 && col < 9) {
                    int x = 100 + col * 100 + (100 - Game.PEA_SHOOTER_WIDTH) / 2;
                    int y = 100 + row * 120 + (120 - Game.PEA_SHOOTER_HEIGHT) / 2;
                    PeaShooter newPlant = new PeaShooter(x, y, Game.PEA_SHOOTER_WIDTH, Game.PEA_SHOOTER_HEIGHT);
                    game.addPlant(row, col, newPlant);
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
        }
        if (drawing != null) {
            getContentPane().add(drawing, 0);
            drawing.repaint();
        }
    }

    @Override
    public void throwAttackUI(GreenPea p) {
        GreenPeaDrawing pd = new GreenPeaDrawing(p);
        getContentPane().add(pd, 0);
        pd.repaint();
    }

    @Override
    public void updatePositionUI(String id) {
        Component c = getComponentById(id);
        if (c instanceof GreenPeaDrawing pd) {
            pd.updatePosition();
        }
    }

    @Override
    public void deleteComponentUI(String id) {
        Component c = getComponentById(id);
        if (c != null) {
            getContentPane().remove(c);
        }
    }

    public Component getComponentById(String id) {
        for (Component c : getContentPane().getComponents()) {
            if (c instanceof GreenPeaDrawing pd && pd.getId().equals(id)) {
                return pd;
            }
        }
        return null;
    }

    public static void main(String[] args) {
        new Frame();
    }
}
