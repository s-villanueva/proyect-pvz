package org.example.ui;

import lombok.Getter;
import org.example.model.plant.SunFlower;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

@Getter
public class SunFlowerDrawing extends JComponent {
    private BufferedImage spriteSheet;
    private BufferedImage[] frames;
    private int currentFrame = 0;
    private Timer animationTimer;

    private SunFlower sunFlower;

    private final int frameCount = 6; // Número total de frames
    private final int frameWidth;
    private final int frameHeight;
    private final int animationSpeed = 150; // Milisegundos entre frames

    private final int startX = 174; // <- Cambia estos valores según donde empiezan los frames
    private final int startY = 119;

    public SunFlowerDrawing(SunFlower sunFlower) {
        this.sunFlower = sunFlower;
        setBounds(sunFlower.getX(), sunFlower.getY(), sunFlower.getWidth(), sunFlower.getHeight());

        InputStream inputStream = null;
        try {
            inputStream = this.getClass().getClassLoader().getResourceAsStream("SunFlowerSprites.png");
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
            frameWidth = 31; // <- Ajusta al tamaño de cada frame
            frameHeight = 34;
            loadFrames();
            startAnimation();
        } else {
            frameWidth = 0;
            frameHeight = 0;
        }
    }

    private void loadFrames() {
        frames = new BufferedImage[frameCount];
        for (int i = 0; i < frameCount; i++) {
            frames[i] = spriteSheet.getSubimage(startX + i * frameWidth, startY, frameWidth, frameHeight);
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
        return sunFlower.getId();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (frames != null && frames.length > 0) {
            Graphics2D g2d = (Graphics2D) g.create();

            double scaleFactor = 2.0; // Escala deseada (2x más grande)

            int scaledWidth = (int) (frameWidth * scaleFactor);
            int scaledHeight = (int) (frameHeight * scaleFactor);
            setBounds(sunFlower.getX()-10, sunFlower.getY(), scaledWidth, scaledHeight);

            int x = (getWidth() - scaledWidth) / 2;
            int y = (getHeight() - scaledHeight) / 2;

            g2d.drawImage(frames[currentFrame], x, y, scaledWidth+2, scaledHeight, this);

            g2d.dispose();
        }
    }

}
