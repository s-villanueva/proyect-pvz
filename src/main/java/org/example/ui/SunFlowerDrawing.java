package org.example.ui;

import lombok.Getter;
import org.example.model.plant.PeaShooter;
import org.example.model.plant.SunFlower;

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
public class SunFlowerDrawing extends JComponent {
    private BufferedImage bi;

    private SunFlower sunFlower;

    public SunFlowerDrawing(SunFlower sunFlower) {
        this.sunFlower = sunFlower;
        setBounds(sunFlower.getX(), sunFlower.getY(), sunFlower.getWidth(), sunFlower.getHeight()); // obligatorio

        InputStream inputStream = null;
        try {
            // cuando no logre leer de resources, borrar en nombre con las comillas mas y escribirlo manualmente. a veces se copia algun caracter raro
            // si no reconoce la imagen, ejecutar mvn clean compile para que sea agregado a la carpeta target
            inputStream = this.getClass().getClassLoader().getResourceAsStream("sunflower.png"); // no funciona bien con webp
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
        return sunFlower.getId();
    }

    @Override
    public void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.drawImage(bi, 0, 0, sunFlower.getWidth(), sunFlower.getHeight(), this);
    }
}
