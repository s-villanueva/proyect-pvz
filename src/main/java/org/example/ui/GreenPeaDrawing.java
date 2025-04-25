package org.example.ui;

import lombok.Getter;
import org.example.model.attack.GreenPea;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

/**
 * ShapeDrawing
 *
 * @author Marcos Quispe
 * @since 1.0
 */
@Getter
public class GreenPeaDrawing extends JComponent {
    private GreenPea greenPea;
    private BufferedImage bi;

    public GreenPeaDrawing(GreenPea greenPea) {
        this.greenPea = greenPea;
        setBounds(greenPea.getX(), greenPea.getY(), greenPea.getWidth(), greenPea.getHeight()); // obligatorio

        InputStream inputStream = null;
        try {
            inputStream = this.getClass().getClassLoader().getResourceAsStream("ProjectilePea.png"); // funciona con png o jpg, pero no con webp
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
        return greenPea.getId();
    }

    public void updatePosition() {
        setLocation(greenPea.getX(), greenPea.getY());
        repaint();
    }

    @Override
    public void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.drawImage(bi, 0, 0, greenPea.getWidth(), greenPea.getHeight(), this);
    }
}
