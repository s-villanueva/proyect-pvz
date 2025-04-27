package org.example.logic;

import org.example.model.attack.GreenPea;
import org.example.model.plant.CherryBomb;
import org.example.model.plant.Plant;

/**
 * IEventosShapes
 *
 * @author Marcos Quispe
 * @since 1.0
 */
public interface IGameEvents {

    void addPlantUI(Plant plant);

    void throwAttackUI(GreenPea greenPea);

    void updatePositionUI(String id);

    void deleteComponentUI(String id);

    void updateHealthUI(int id, int health);  // Mostrar salud de una planta (en este caso la Nuez)

    void explosionUI(CherryBomb cherryBomb); // Método para mostrar la explosión

}
