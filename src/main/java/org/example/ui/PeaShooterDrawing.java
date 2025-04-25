package org.example.ui;

import lombok.Getter;
import org.example.model.plant.PeaShooter;

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
public class PeaShooterDrawing extends JComponent {
    private BufferedImage bi;

    private PeaShooter peaShooter;
    private boolean finalizar = false;
    private int frame = 0; // 0 - 7
    private int cantframes = 8;
    private long prevTime;

    public PeaShooterDrawing(PeaShooter peaShooter) {
        this.peaShooter = peaShooter;
        prevTime = System.currentTimeMillis();
        setBounds(peaShooter.getX(), peaShooter.getY(), peaShooter.getWidth(), peaShooter.getHeight()); // obligatorio

        InputStream inputStream = null;
        try {
            // cuando no logre leer de resources, borrar en nombre con las comillas mas y escribirlo manualmente. a veces se copia algun caracter raro
            inputStream = this.getClass().getClassLoader().getResourceAsStream("PeashooterSprites.png"); // no funciona bien con webp
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
        return peaShooter.getId();
    }

    @Override
    public void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;

        long currentTime = System.currentTimeMillis();
        if (currentTime - prevTime > 300) {
            frame = (frame + 1) % cantframes;
            prevTime = currentTime;
        }

        if (bi != null) {
            //System.out.println("dibujando frame: " + frame);
            int fxInitial = 170;
            int fyInitial = 13;
            //frame = 9; // quitar luego
            int xSprite = fxInitial + (frame * 26) + (frame * 3);
            g.drawImage(bi, 0, 0, peaShooter.getWidth(), peaShooter.getHeight()
                    , xSprite, fyInitial
                    , xSprite + 26, fyInitial + 32, this);
        }
    }
}
