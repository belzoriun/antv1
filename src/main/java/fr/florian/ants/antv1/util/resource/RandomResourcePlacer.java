package fr.florian.ants.antv1.util.resource;

import fr.florian.ants.antv1.map.ResourceTile;
import fr.florian.ants.antv1.util.Vector;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class RandomResourcePlacer implements IResourcePlacer{

    private Random rand;
    private List<Resource> selection;

    public RandomResourcePlacer(List<Resource> factories)
    {
        rand = new Random();
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
    public ResourceTile placeTile(Vector v) {
        double xMin = 0.3;
        double xMax = 0.7;
        double yMin = 0.3;
        double yMax = 0.7;
        List<Resource> res = new ArrayList<>();
        if(rand.nextInt(0, 100) < 30) {
            if(!selection.isEmpty()) {
                int amount = rand.nextInt(0, 20);
                for (int i = 0; i < amount; i++) {
                    Vector pos = new Vector(new Random().nextDouble(xMin, xMax), new Random().nextDouble(yMin, yMax));
                    res.add(selection.get(rand.nextInt(0, selection.size())).clone(pos));
                }
            }
        }
        return new ResourceTile(res);
    }
}
