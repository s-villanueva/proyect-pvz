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
public class ZombieDrawing extends JComponent implements IComponentID {
    private BufferedImage spriteSheet;
    private BufferedImage[] frames;
    private int currentFrame = 0;

    private final int totalFrames = 7;
    private final int frameWidth = 44;
    private final int frameHeight = 58;
    private final int spacing = 6; // Espacio horizontal entre frames
    private final int yOffset = 57; // Desplazamiento vertical (ajusta según tu sprite)
    private final int frameDelay = 150; // ms entre frames

    private final double scale = 2; // ⬅️ ESCALA

    private Timer animationTimer;

    private Zombie zombie;
    private String id;

    public ZombieDrawing(Zombie zombie) {
        this.zombie = zombie;
        this.id = zombie.getId();

        int scaledWidth = (int) (zombie.getWidth() * scale);
        int scaledHeight = (int) (zombie.getHeight() * scale);
        setBounds(zombie.getX(), zombie.getY(), scaledWidth, scaledHeight);

        loadSpriteSheet();
        startAnimation();
    }

    private void loadSpriteSheet() {
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream("Zombie.png")) {
            spriteSheet = ImageIO.read(inputStream);

            frames = new BufferedImage[totalFrames];
            for (int i = 0; i < totalFrames; i++) {
                int x = i * (frameWidth + spacing);
                frames[i] = spriteSheet.getSubimage(x, yOffset, frameWidth, frameHeight);
            }
        } catch (IOException | NullPointerException e) {
            e.printStackTrace();
            // Fallback en caso de error
            frames = new BufferedImage[1];
            frames[0] = new BufferedImage(frameWidth, frameHeight, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = frames[0].createGraphics();
            g2.setColor(Color.RED);
            g2.fillRect(0, 0, frameWidth, frameHeight);
            g2.dispose();
        }
    }

    private void startAnimation() {
        animationTimer = new Timer(frameDelay, e -> {
            currentFrame = (currentFrame + 1) % totalFrames;
            repaint();
        });
        animationTimer.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (frames != null && frames[currentFrame] != null) {
            Graphics2D g2d = (Graphics2D) g;
            int drawWidth = (int) (frameWidth * scale);
            int drawHeight = (int) (frameHeight * scale);

            g2d.drawImage(frames[currentFrame], 0, 0, drawWidth, drawHeight, this);
        }
    }

    public void updatePosition() {
        int scaledWidth = (int) (zombie.getWidth() * scale);
        int scaledHeight = (int) (zombie.getHeight() * scale);
        setLocation(zombie.getX(), zombie.getY());
        setSize(scaledWidth, scaledHeight);
        repaint();
    }
}
