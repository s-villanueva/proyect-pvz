package org.example.ui;

import lombok.Getter;
import org.example.model.plant.PeaShooter;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

@Getter
public class PeaShooterDrawing extends JComponent {
    private BufferedImage spriteSheet;
    private PeaShooter peaShooter;

    private int frame = 0;
    private final int totalFrames = 8;
    private final int frameWidth = 26;
    private final int frameHeight = 32;
    private final int frameSpacing = 3; // espacio entre frames

    private Timer animationTimer;

    public PeaShooterDrawing(PeaShooter peaShooter) {
        this.peaShooter = peaShooter;
        setBounds(peaShooter.getX(), peaShooter.getY(), peaShooter.getWidth(), peaShooter.getHeight());

        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream("PeashooterSprites.png")) {
            spriteSheet = ImageIO.read(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }

        startAnimation();
    }

    private void startAnimation() {
        animationTimer = new Timer(100, e -> {
            frame = (frame + 1) % totalFrames;
            repaint();
        });
        animationTimer.start();
    }

    public String getId() {
        return peaShooter.getId();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (spriteSheet != null) {
            int spriteStartX = 170 + (frame * (frameWidth + frameSpacing));
            int spriteStartY = 13;

            Graphics2D g2d = (Graphics2D) g;
            g2d.drawImage(
                    spriteSheet,
                    0, 0, getWidth(), getHeight(),
                    spriteStartX, spriteStartY, spriteStartX + frameWidth, spriteStartY + frameHeight,
                    this
            );
        }
    }
}
