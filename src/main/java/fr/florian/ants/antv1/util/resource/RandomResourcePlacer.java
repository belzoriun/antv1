package fr.florian.ants.antv1.util.resource;

import fr.florian.ants.antv1.map.ResourceHoldTile;
import fr.florian.ants.antv1.map.ResourceTile;
import fr.florian.ants.antv1.ui.Application;
import fr.florian.ants.antv1.util.Vector;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Class placing resources randomly
 */
public class RandomResourcePlacer implements IResourcePlacer{

    private final Random rand;
    private final List<Resource> selection;

    public RandomResourcePlacer(List<Resource> factories)
    {
        rand = Application.random;
        selection = new ArrayList<>();
        for(Resource resource : factories)
        {
            int amount = (int) ((1-resource.getRarity())*100);
            for(int i = 0; i<amount; i++)
            {
                selection.add(resource);
            }
        }
        Collections.shuffle(selection);
    }

    @Override
    public void placeResources(Vector v, ResourceHoldTile t) {
        double xMin = 0.3;
        double xMax = 0.7;
        double yMin = 0.3;
        double yMax = 0.7;
        if(rand.nextInt(0, 100) < 30) {
            if(!selection.isEmpty()) {
                int amount = rand.nextInt(0, 20);
                Resource selected = selection.get(rand.nextInt(0, selection.size()));
                for (int i = 0; i < amount; i++) {
                    Vector pos = new Vector(Application.random.nextDouble(xMin, xMax), Application.random.nextDouble(yMin, yMax));
                    t.placeResource(selected.clone(pos));
                }
            }
        }
    }
}
