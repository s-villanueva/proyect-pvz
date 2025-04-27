package org.example.ui;

import org.example.model.plant.CherryBomb;

import javax.swing.*;
import java.awt.*;

public class PowExplosionDrawing extends JComponent {
    private final CherryBomb cherryBomb;
    private int powFrame = 0; // Para la animación del Pow
    private final int powFrames = 5; // Número de frames de la animación del Pow
    private long prevTime;

    public PowExplosionDrawing(CherryBomb cherryBomb) {
        this.cherryBomb = cherryBomb;
        this.prevTime = System.currentTimeMillis();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        long currentTime = System.currentTimeMillis();

        if (currentTime - prevTime > 100) { // Cambia de frame cada 100ms
            powFrame++;
            if (powFrame >= powFrames) {
                // Finaliza la animación del Pow
                Container parent = getParent();
                if (parent != null) {
                    parent.remove(this); // Eliminar el dibujo del Pow
                    parent.revalidate();
                    parent.repaint();
                }
                return;
            }
            prevTime = currentTime;
            repaint();
        }

        // Dibuja la animación del Pow (representado como círculos de colores)
        g2d.setColor(Color.RED);
        g2d.fillOval(cherryBomb.getX() - 50, cherryBomb.getY() - 50, 200, 200);  // Círculo rojo grande
        g2d.setColor(Color.YELLOW);
        g2d.fillOval(cherryBomb.getX() - 30, cherryBomb.getY() - 30, 140, 140);  // Círculo amarillo más pequeño
        g2d.setColor(Color.WHITE);
        g2d.fillOval(cherryBomb.getX() - 10, cherryBomb.getY() - 10, 100, 100);  // Círculo blanco más pequeño
    }
}
