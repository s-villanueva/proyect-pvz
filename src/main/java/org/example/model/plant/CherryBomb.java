package org.example.model.plant;

import lombok.Getter;
import lombok.Setter;
import org.example.ui.Frame;

import java.util.UUID;

@Getter
@Setter
public class CherryBomb extends Plant {

    private long explosionTime = 2000; // 2000ms = 2 segundos (tiempo antes de explotar)
    private long prevTime;
    private boolean exploded;

    // Referencia a la ventana (o Frame) para actualizar la UI
    private Frame gameFrame;

    public CherryBomb(int x, int y, int width, int height, Frame gameFrame) {
        this.id = UUID.randomUUID().toString();
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.prevTime = System.currentTimeMillis();
        this.exploded = false;
        this.gameFrame = gameFrame; // Guardar referencia a la UI
    }

    // Método que maneja la explosión de la CherryBomb
    public void explode() {
        if (!exploded) {
            exploded = true;
            // Llamamos a la función explosionUI para mostrar la animación
            gameFrame.explosionUI(this);

            // Imprime un mensaje para probar que la explosión ha ocurrido
            System.out.println("La CherryBomb ha explotado en las coordenadas: (" + x + ", " + y + ")");
        }
    }

    @Override
    public String toString() {
        return "CherryBomb{" +
                "explosionTime=" + explosionTime +
                ", prevTime=" + prevTime +
                ", exploded=" + exploded +
                ", id='" + id + '\'' +
                ", x=" + x +
                ", y=" + y +
                ", width=" + width +
                ", height=" + height +
                ", reloadTime=" + reloadTime +
                ", defenseInitial=" + defenseInitial +
                ", defense=" + defense +
                '}';
    }
}
