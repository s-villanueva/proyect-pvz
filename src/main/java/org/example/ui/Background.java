package org.example.ui;

import lombok.Setter;
import org.example.logic.Game;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

public class Background extends JPanel {
    @Setter
    private Game game;
    private BufferedImage bufferedImage;


    public Background() {
        InputStream inputStream = null;
        try {
            inputStream = this.getClass().getClassLoader().getResourceAsStream("pvz-jardin-full.png");
            bufferedImage = ImageIO.read(inputStream);
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
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        ImageIcon backgroundImage = new ImageIcon("pvz-jardin-full.png");
        System.out.println("Imagen cargada");
        Image img = backgroundImage.getImage();

        g.drawImage(img, 0, 0, getWidth(), getHeight(), this);

        int rows = 5;
        int cols = 9;
        int cellWidth = 100;
        int cellHeight = 125;

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                int x = col * cellWidth + 100;
                int y = row * cellHeight + 100;
                g.setColor(Color.LIGHT_GRAY);
                g.drawRect(x, y, cellWidth, cellHeight);
            }
        }

    }

    @Override
    public void paint(Graphics g) {
        g.drawImage(bufferedImage, 0, 0, getWidth(), getHeight()
                , 175, 0
                , 990, 570, this);
    }
}
