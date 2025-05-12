package org.example.ui;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class MenuPanel extends JPanel {
    private BufferedImage background;
    private BufferedImage[] seedImages; // Semillas a pintar
    private final int seedSize = 60;
    private final int padding = 10;
    private int suns = 50;

    public MenuPanel(String imagePath) {
        try {
            background = ImageIO.read(getClass().getClassLoader().getResourceAsStream(imagePath));
        } catch (IOException e) {
            e.printStackTrace();
        }
        setBorder(BorderFactory.createEmptyBorder(0, 120, 0, 0));
        setLayout(new FlowLayout(FlowLayout.LEFT, 10, 10));
        setOpaque(false);

        loadSeedImages();
    }

    private void loadSeedImages() {
        String[] seedPaths = {
                "SunflowerSeed.png",
                "PeaShooterSeed.png",
                "WallNutSeed.png",
                "CherryBombSeed.png",
                "SnowPeaShooterSeed.png"
        };

        seedImages = new BufferedImage[seedPaths.length];
        for (int i = 0; i < seedPaths.length; i++) {
            try {
                seedImages[i] = ImageIO.read(getClass().getClassLoader().getResourceAsStream(seedPaths[i]));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void setSuns(int suns) {
        this.suns = suns;
        repaint();
    }


    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (background != null) {
            g.drawImage(background, 0, 0, getWidth(), getHeight(), this);
        }

        if (seedImages != null) {
            int x = 130;
            int y = 10;
            for (BufferedImage seed : seedImages) {
                if (seed != null) {
                    g.drawImage(seed, x, y, seedSize, seedSize, this);
                    x += seedSize + padding; // Siguiente posición
                }
            }
        }

        g.setFont(new Font("TimesNewRoman", Font.BOLD, 24));
        g.setColor(Color.BLACK);

        FontMetrics fm = g.getFontMetrics();
        String sunText = Integer.toString(suns);
        int textWidth = fm.stringWidth(sunText);

        int centerX = 59;
        int centerY = 75;

        g.drawString(sunText, centerX - textWidth / 2, centerY);

    }
}
