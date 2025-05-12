package org.example.ui;

import lombok.Getter;
import org.example.model.plant.CherryBomb;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Getter
public class CherryBombDrawing extends JComponent implements IComponentID {
    private CherryBomb cherryBomb;
    private BufferedImage spriteSheet;
    private List<BufferedImage> normalFrames = new ArrayList<>();
    private List<BufferedImage> explosionFrames = new ArrayList<>();
    private int frame = 0;

    private Timer animationTimer;


    public CherryBombDrawing(CherryBomb cherryBomb) {
        this.cherryBomb = cherryBomb;
        setBounds(cherryBomb.getX(), cherryBomb.getY(), cherryBomb.getWidth(), cherryBomb.getHeight());
        setOpaque(false);

        try (InputStream stream = getClass().getClassLoader().getResourceAsStream("CherryBombSprites.png")) {
            spriteSheet = ImageIO.read(stream);
            loadFrames();
        } catch (Exception e) {
            e.printStackTrace();
        }

        startAnimation();
    }

    private void loadFrames() {
        normalFrames.add(spriteSheet.getSubimage(168, 19, 37, 34));
        normalFrames.add(spriteSheet.getSubimage(202, 19, 37, 34));
        normalFrames.add(spriteSheet.getSubimage(242, 15, 45, 39));
        normalFrames.add(spriteSheet.getSubimage(290, 15, 45, 39));
        normalFrames.add(spriteSheet.getSubimage(338, 13, 50, 37));
        normalFrames.add(spriteSheet.getSubimage(391, 13, 50, 37));

        explosionFrames.add(spriteSheet.getSubimage(169, 74, 67, 59));
        explosionFrames.add(spriteSheet.getSubimage(237, 66, 96, 72));
        explosionFrames.add(spriteSheet.getSubimage(417, 62, 91, 73));
        explosionFrames.add(spriteSheet.getSubimage(512, 61, 91, 73));
        explosionFrames.add(spriteSheet.getSubimage(697, 61, 92, 69));
        explosionFrames.add(spriteSheet.getSubimage(790, 64, 89, 67));

    }

    private void startAnimation() {
        animationTimer = new Timer(120, e -> {
            frame++;
            int total = cherryBomb.isExploded() ? explosionFrames.size() : normalFrames.size();
            if (frame >= total) {
                if (cherryBomb.isExploded()) {
                    // Detener después de la explosión
                    animationTimer.stop();
                    frame = total - 1; // Quedarse en el último frame
                } else {
                    frame = 0;
                }
            }
            repaint();
        });
        animationTimer.start();
    }

    public void updatePosition() {
        setLocation(cherryBomb.getX(), cherryBomb.getY());
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        List<BufferedImage> currentFrames = cherryBomb.isExploded() ? explosionFrames : normalFrames;

        if (!currentFrames.isEmpty() && frame < currentFrames.size()) {
            BufferedImage currentFrame = currentFrames.get(frame);
            int imgW = currentFrame.getWidth();
            int imgH = currentFrame.getHeight();
            g.drawImage(currentFrame, (getWidth() - imgW) / 2, (getHeight() - imgH) / 2, this);
        } else {
            g.setColor(Color.RED);
            g.fillRect(0, 0, getWidth(), getHeight());
        }
    }

    @Override
    public String getId() {
        return cherryBomb.getId();
    }
}
