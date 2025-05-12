package org.example.ui;

import lombok.Getter;
import org.example.model.plant.WallNut;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

@Getter
public class WallNutDrawing extends JComponent implements IComponentID{
    private BufferedImage spriteSheet;
    private BufferedImage[] frames;
    private int currentFrame = 0;
    private Timer animationTimer;

    private WallNut wallNut;

    private final int frameCount = 5;
    private final int frameWidth;
    private final int frameHeight;
    private final int animationSpeed = 150;

    private final int startX = 168;
    private final int startY = 12;

    private final double scaleFactor = 2.0;

    public WallNutDrawing(WallNut wallNut) {
        this.wallNut = wallNut;

        InputStream inputStream = null;
        try {
            inputStream = this.getClass().getClassLoader().getResourceAsStream("WallNutSprites.png"); // <- sprite sheet
            spriteSheet = ImageIO.read(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        if (spriteSheet != null) {
            frameWidth = 29;
            frameHeight = 33;

            int scaledWidth = (int) (frameWidth * scaleFactor);
            int scaledHeight = (int) (frameHeight * scaleFactor);
            setBounds(wallNut.getX(), wallNut.getY(), scaledWidth, scaledHeight);

            loadFrames();
            startAnimation();
        } else {
            frameWidth = 0;
            frameHeight = 0;
            setBounds(wallNut.getX(), wallNut.getY(), wallNut.getWidth(), wallNut.getHeight());
        }
    }

    private void loadFrames() {
        frames = new BufferedImage[frameCount];
        for (int i = 0; i < frameCount; i++) {
            frames[i] = spriteSheet.getSubimage(
                    startX + i * frameWidth,
                    startY,
                    frameWidth,
                    frameHeight
            );
        }
    }

    private void startAnimation() {
        animationTimer = new Timer(animationSpeed, e -> {
            currentFrame = (currentFrame + 1) % frameCount;
            repaint();
        });
        animationTimer.start();
    }

    public String getId() {
        return wallNut.getId();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (frames != null && frames.length > 0) {
            Graphics2D g2d = (Graphics2D) g.create();

            int scaledWidth = (int) (frameWidth * scaleFactor);
            int scaledHeight = (int) (frameHeight * scaleFactor);

            int x = (getWidth() - scaledWidth) / 2;
            int y = (getHeight() - scaledHeight) / 2;

            g2d.drawImage(frames[currentFrame], x, y, scaledWidth, scaledHeight, this);

            g2d.dispose();
        }
    }
}
