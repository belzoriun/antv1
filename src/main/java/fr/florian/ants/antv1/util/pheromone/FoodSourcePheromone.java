package fr.florian.ants.antv1.util.pheromone;

import javafx.scene.paint.Color;

/**
 * Class representing a pheromone used by ants to mark food location
 */
public class FoodSourcePheromone extends Pheromone{
    public FoodSourcePheromone(Color color) {
        super(50, color);
    }
}
