package org.example.ui;

import lombok.Getter;
import org.example.model.zombie.ConeheadZombie;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

@Getter
public class ConeheadZombieDrawing extends JComponent implements IComponentID {
    private ConeheadZombie zombie;
    private BufferedImage spriteSheetConehead;
    private int frame = 0;
    private final int totalFrames = 7;
    private final int frameWidth = 44;
    private final int frameHeight = 58;
    private final int frameSpacing = 8;
    private final int yOffset = 65;

    private Timer animationTimer;
    private ZombieDrawing fallbackDrawing;

    public ConeheadZombieDrawing(ConeheadZombie zombie) {
        this.zombie = zombie;
        setBounds(zombie.getX(), zombie.getY(), zombie.getWidth(), zombie.getHeight());

        try (InputStream coneStream = getClass().getClassLoader().getResourceAsStream("ConeheadZombie.png")) {
            spriteSheetConehead = ImageIO.read(coneStream);
        } catch (IOException | NullPointerException e) {
            e.printStackTrace();
        }
        fallbackDrawing = new ZombieDrawing(zombie);
        startAnimation();
    }

    public void updatePosition() {
        setLocation(zombie.getX(), zombie.getY());
        if (fallbackDrawing != null) {
            fallbackDrawing.updatePosition();
        }
        repaint();
    }

    private void startAnimation() {
        animationTimer = new Timer(100, e -> {
            frame = (frame + 1) % totalFrames;
            repaint();
        });
        animationTimer.start();
    }

    public String getId() {
        return zombie.getId();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (!zombie.isConeIntact()) {
            fallbackDrawing.setBounds(0, 0, getWidth(), getHeight());
            fallbackDrawing.paintComponent(g);
            return;
        }

        if (spriteSheetConehead != null) {
            int spriteStartX = frame * (frameWidth + frameSpacing);
            BufferedImage frameImage = spriteSheetConehead.getSubimage(
                    spriteStartX, yOffset,
                    frameWidth, frameHeight
            );
            g.drawImage(frameImage, 0, 0, getWidth(), getHeight(), this);
        } else {
            g.setColor(Color.RED);
            g.fillRect(0, 0, getWidth(), getHeight());
        }
    }
}
