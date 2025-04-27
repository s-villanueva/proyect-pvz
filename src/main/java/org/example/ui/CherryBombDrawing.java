package org.example.ui;

import lombok.Getter;
import org.example.model.plant.CherryBomb;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

@Getter
public class CherryBombDrawing extends JComponent {
    private BufferedImage bi;

    private CherryBomb cherryBomb;
    private int frame = 0; // Animación de frames
    private int cantFrames = 8; // Número de frames para la animación de CherryBomb
    private long prevTime;
    private boolean exploding = false; // Indica si la CherryBomb está explotando

    public CherryBombDrawing(CherryBomb cherryBomb) {
        this.cherryBomb = cherryBomb;
        prevTime = System.currentTimeMillis();
        setBounds(cherryBomb.getX(), cherryBomb.getY(), cherryBomb.getWidth(), cherryBomb.getHeight()); // Establece las dimensiones

        // Cargar la spritesheet de CherryBomb
        InputStream inputStream = null;
        try {
            inputStream = this.getClass().getClassLoader().getResourceAsStream("CherryBombSprites.png"); // Asegúrate de que el nombre del archivo es correcto
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
        return cherryBomb.getId();
    }

    // Método que cambia el estado de explosión
    public void setExplosion(boolean explosionState) {
        this.exploding = explosionState;
        if (exploding) {
            // Reiniciar la animación si está explotando
            frame = 0;
            prevTime = System.currentTimeMillis(); // Reiniciar el tiempo de animación
        }
        repaint(); // Redibujar el componente para reflejar el cambio de estado
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g); // Limpia el componente antes de dibujar nuevamente
        Graphics2D g2d = (Graphics2D) g;

        long currentTime = System.currentTimeMillis();
        if (currentTime - prevTime > 100) { // Cambia de frame cada 100ms
            if (exploding) {
                // Si está explotando, anima los frames de explosión
                frame = (frame + 1) % cantFrames;
            }
            prevTime = currentTime;
        }

        if (bi != null) {
            // Determina la posición inicial del frame en la spritesheet
            int fxInitial = 168; // Ajustar según la spritesheet
            int fyInitial = 12;
            int frameWidth = 34;  // Ajustar según el ancho de cada frame en la spritesheet
            int frameHeight = 38; // Ajustar según la altura de cada frame en la spritesheet

            // Calcula la posición del sprite para el frame actual
            int xSprite = fxInitial + (frame * (frameWidth + 3)); // 3 es el espaciado entre los frames
            g2d.drawImage(bi, 0, 0, getWidth(), getHeight(),  // Dibuja el frame actual
                    xSprite, fyInitial,
                    xSprite + frameWidth, fyInitial + frameHeight, this);
        }
    }
}
