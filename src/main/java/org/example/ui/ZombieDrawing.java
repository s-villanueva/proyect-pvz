package org.example.ui;

import lombok.Getter;
import org.example.model.zombie.Zombie;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

@Getter
public class ZombieDrawing extends JComponent {
    private BufferedImage zombieImage;  // Imagen estática del zombi
    private Zombie zombie;
    private String id;

    public ZombieDrawing(Zombie zombie) {
        this.zombie = zombie;
        this.id = zombie.getId();
        setBounds(zombie.getX(), zombie.getY(), zombie.getWidth(), zombie.getHeight());

        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream("Zombie.png")) {
            zombieImage = ImageIO.read(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (zombieImage != null) {
            g.drawImage(zombieImage, 0, 0, getWidth(), getHeight(), this);
        } else {
            g.setColor(Color.RED);
            g.fillRect(0, 0, getWidth(), getHeight());
        }
    }

    public void updatePosition() {
        setLocation(zombie.getX(), zombie.getY());
        repaint();
    }
}
