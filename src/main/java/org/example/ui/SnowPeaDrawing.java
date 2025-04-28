package org.example.ui;

import lombok.Getter;
import org.example.model.attack.SnowPea;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

@Getter
public class SnowPeaDrawing extends JComponent {
    private SnowPea snowPea;
    private BufferedImage sprite;
    private BufferedImage scaledSprite;

    public SnowPeaDrawing(SnowPea snowPea) {
        this.snowPea = snowPea;
        setBounds(snowPea.getX(), snowPea.getY(), snowPea.getWidth(), snowPea.getHeight());

        try (InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("SnowPeaProjectile.png")) {
            if (inputStream != null) {
                sprite = ImageIO.read(inputStream);
                rescaleSprite(); // Reescalar de una vez
            } else {
                System.err.println("No se encontró la imagen ProjectilePea.png");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void rescaleSprite() {
        if (sprite != null) {
            int scaledWidth = sprite.getWidth() * 2;
            int scaledHeight = sprite.getHeight() * 2;

            scaledSprite = new BufferedImage(
                    scaledWidth,
                    scaledHeight,
                    BufferedImage.TYPE_INT_ARGB
            );

            Graphics2D g2d = scaledSprite.createGraphics();
            g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g2d.drawImage(sprite, 0, 0, scaledWidth, scaledHeight, null);
            g2d.dispose();

            // También actualizamos el tamaño del componente para que encaje
            setSize(scaledWidth, scaledHeight);
            setBounds(snowPea.getX(), snowPea.getY(), scaledWidth, scaledHeight);
        }
    }


    public String getId() {
        return snowPea.getId();
    }

    public void updatePosition() {
        setLocation(snowPea.getX(), snowPea.getY());
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (scaledSprite != null) {
            g.drawImage(scaledSprite, 0, 0, this);
        }
    }
}
