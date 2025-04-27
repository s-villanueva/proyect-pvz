package org.example.ui;

import lombok.Getter;
import org.example.model.plant.WallNut;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

/**
 * WallNutDrawing
 *
 * @author Marcos Quispe
 * @since 1.0
 */
@Getter
public class WallNutDrawing extends JComponent {
    private BufferedImage bi;

    private WallNut wallNut;

    public WallNutDrawing(WallNut wallNut) {
        this.wallNut = wallNut;
        setBounds(wallNut.getX(), wallNut.getY(), wallNut.getWidth(), wallNut.getHeight()); // obligatorio

        InputStream inputStream = null;
        try {

            inputStream = this.getClass().getClassLoader().getResourceAsStream("WallNut.png"); // Ajusta el nombre si es necesario
            bi = ImageIO.read(inputStream);
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

    public String getId() {
        return wallNut.getId();
    }

    @Override
    public void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.drawImage(bi, 0, 0, wallNut.getWidth(), wallNut.getHeight(), this);
    }
}
