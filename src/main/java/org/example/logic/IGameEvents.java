package org.example.logic;

import org.example.model.attack.Attack;
import org.example.model.attack.Sun;
import org.example.model.plant.CherryBomb;
import org.example.model.plant.Plant;
import org.example.model.zombie.Zombie;

/**
 * IEventosShapes
 *
 * @author Marcos Quispe
 * @since 1.0
 */
public interface IGameEvents {

    void addPlantUI(Plant plant);

    void addSunUI(Sun sun);

    void updateSunCounter(int suns);

    void throwAttackUI(Attack attack);

    void updatePositionUI(String id);

    void deleteComponentUI(String id);

    void updateHealthUI(int id, int health);  // Mostrar salud de una planta (en este caso la Nuez)

    void explosionUI(CherryBomb cherryBomb); // Método para mostrar la explosión

    void spawnZombieUI(Zombie z);

    void updateZombiePositionUI(String id);

    void removeZombieUI(String id);

    void addZombieUI(Zombie z);
}
