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
    private BufferedImage spriteSheetNormal;

    private int frame = 0;
    private final int totalFrames = 7;
    private final int frameWidth = 44;
    private final int frameHeight = 58;
    private final int frameSpacing = 8; // ajusta según tu spritesheet

    private Timer animationTimer;

    public ConeheadZombieDrawing(ConeheadZombie zombie) {
        this.zombie = zombie;
        setBounds(zombie.getX(), zombie.getY(), zombie.getWidth(), zombie.getHeight());

        try (InputStream coneStream = getClass().getClassLoader().getResourceAsStream("ConeheadZombie.png");){
//             InputStream normalStream = getClass().getClassLoader().getResourceAsStream("ZombieSprites.png"))

            spriteSheetConehead = ImageIO.read(coneStream);
//            spriteSheetNormal = ImageIO.read(normalStream);

        } catch (IOException e) {
            e.printStackTrace();
        }

        startAnimation();
    }

    public void updatePosition() {
        setLocation(zombie.getX(), zombie.getY());
        repaint();
    }

    private void startAnimation() {
        animationTimer = new Timer(100, e -> {
            frame = (frame + 1) % totalFrames;
            repaint();
        });
        animationTimer.start();
    }

    public void stopAnimation() {
        if (animationTimer != null) {
            animationTimer.stop();
        }
    }

    public String getId() {
        return zombie.getId();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        BufferedImage spriteSheet = zombie.isConeIntact() ? spriteSheetConehead : spriteSheetNormal;

        if (spriteSheet != null) {
            int spriteStartX = frame * (frameWidth + frameSpacing);
            int spriteStartY = 65;

            g.drawImage(
                    spriteSheet,
                    0, 0, getWidth(), getHeight(),
                    spriteStartX, spriteStartY,
                    spriteStartX + frameWidth, spriteStartY + frameHeight,
                    this
            );
        } else {
            g.setColor(Color.RED);
            g.fillRect(0, 0, getWidth(), getHeight());
        }
    }


}
