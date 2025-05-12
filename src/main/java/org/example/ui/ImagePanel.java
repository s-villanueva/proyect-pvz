package org.example.ui;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class ImagePanel extends JPanel {
    private Image backgroundImage;

    public ImagePanel() {
        String imagePath = ("ShovelPanel.png");
        try {
            backgroundImage = ImageIO.read(getClass().getClassLoader().getResourceAsStream(imagePath));
            setOpaque(false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        }
    }
}
