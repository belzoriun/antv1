package fr.florian.ants.antv1.util.pheromone;

import fr.florian.ants.antv1.living.ant.Ant;
import javafx.scene.paint.Color;

public class FoodSourcePheromone extends Pheromone{
    public FoodSourcePheromone(Color color) {
        super(color);
    }

    @Override
    public void onDetect(Ant a) {
        //do nothing
    }
}
