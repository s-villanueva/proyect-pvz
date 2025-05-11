package org.example.ui;

import lombok.Getter;
import org.example.model.attack.LawnMower;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

@Getter
public class LawnMowerDrawing extends JComponent implements IComponentID {
    private final LawnMower lawnMower;
    private BufferedImage mowerImage;
    private final String id;
    private final int row;

    public LawnMowerDrawing(LawnMower lawnMower, int row) {
        this.lawnMower = lawnMower;
        this.row = row;
        this.id = "LawnMower-" + row;
        setBounds(lawnMower.getX(), lawnMower.getY(), 60, 60);

        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream("LawnMower.png")) {
            mowerImage = ImageIO.read(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (mowerImage != null) {
            g.drawImage(mowerImage, 0, 0, getWidth(), getHeight(), this);
        } else {
            g.setColor(Color.GRAY);
            g.fillRect(0, 0, getWidth(), getHeight());
        }
    }

    public void updatePosition() {
        setLocation(lawnMower.getX(), lawnMower.getY());
        repaint();
    }
}
