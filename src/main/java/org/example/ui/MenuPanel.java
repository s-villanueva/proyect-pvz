package org.example.ui;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class MenuPanel extends JPanel {
    private BufferedImage background;

    public MenuPanel(String imagePath) {
        setLayout(new FlowLayout(FlowLayout.LEFT, 10, 10)); // centro horizontal
        try {
            background = ImageIO.read(getClass().getClassLoader().getResourceAsStream(imagePath));
        } catch (IOException e) {
            e.printStackTrace();
        }
        setLayout(new FlowLayout(FlowLayout.LEFT, 10, 10));
        setOpaque(false); // importante para que JFrame no pinte encima
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (background != null) {
            g.drawImage(background, 0, 0, getWidth(), getHeight(), this);
        }
    }
}

