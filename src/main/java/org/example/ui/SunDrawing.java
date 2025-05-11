package org.example.ui;

import lombok.Getter;
import org.example.model.attack.Sun;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

@Getter
public class SunDrawing extends JComponent implements IComponentID{
    private BufferedImage bi;
    private Sun sun;

    public SunDrawing(Sun sun) {
        this.sun = sun;
        setBounds(sun.getX(), sun.getY(), sun.getWidth(), sun.getHeight());

        InputStream inputStream = null;
        try {
            inputStream = this.getClass().getClassLoader().getResourceAsStream("sun.png"); // tu imagen del sol
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
        return sun.getId();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.drawImage(bi, 0, 0, sun.getWidth(), sun.getHeight(), this);
    }
}
