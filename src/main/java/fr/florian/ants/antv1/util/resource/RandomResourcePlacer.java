package fr.florian.ants.antv1.util.resource;

import fr.florian.ants.antv1.map.ResourceTile;
import fr.florian.ants.antv1.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RandomResourcePlacer implements IResourcePlacer{

    private Random rand;

    public RandomResourcePlacer()
    {
        rand = new Random();
    }

    @Override
    public ResourceTile placeTile(Vector v) {
        List<Resource> res = new ArrayList<>();
        if(rand.nextInt(0, 100) < 30) {
            int amount = rand.nextInt(0, 20);
            for (int i = 0; i < amount; i++) {
                res.add(new BasicResource());
            }
        }
        return new ResourceTile(res);
    }
}
