package org.example.ui;

import lombok.Getter;
import org.example.model.plant.SnowPeaShooter;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

@Getter
public class SnowPeaShooterDrawing extends JComponent {
    private BufferedImage spriteSheet;
    private SnowPeaShooter snowPeaShooter;

    private int frame = 0;
    private final int totalFrames = 8;
    private final int frameWidth = 31;
    private final int frameHeight = 32;
    private final int frameSpacing = 1;

    private Timer animationTimer;

    public SnowPeaShooterDrawing(SnowPeaShooter snowPeaShooter) {
        this.snowPeaShooter = snowPeaShooter;
        setBounds(snowPeaShooter.getX(), snowPeaShooter.getY(), snowPeaShooter.getWidth(), snowPeaShooter.getHeight());

        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream("SnowPeaShooterSprites.png")) {
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
        return snowPeaShooter.getId();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (spriteSheet != null) {
            int spriteStartX = 187 + (frame * (frameWidth + frameSpacing));
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
