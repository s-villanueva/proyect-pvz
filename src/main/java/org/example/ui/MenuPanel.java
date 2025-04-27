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

    public MenuPanel(String imagePath) {
        try {
            background = ImageIO.read(getClass().getClassLoader().getResourceAsStream(imagePath));
        } catch (IOException e) {
            e.printStackTrace();
        }
        setBorder(BorderFactory.createEmptyBorder(0, 120, 0, 0));
        setLayout(new FlowLayout(FlowLayout.LEFT, 10, 10));
        setOpaque(false);

        // Cargar imágenes de semillas
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

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Dibujar el fondo
        if (background != null) {
            g.drawImage(background, 0, 0, getWidth(), getHeight(), this);
        }

        // Dibujar las imágenes de semillas encima
        if (seedImages != null) {
            int x = 130; // Posición inicial (corregido por Border)
            int y = 10;
            for (BufferedImage seed : seedImages) {
                if (seed != null) {
                    g.drawImage(seed, x, y, seedSize, seedSize, this);
                    x += seedSize + padding; // Siguiente posición
                }
            }
        }
    }
}
