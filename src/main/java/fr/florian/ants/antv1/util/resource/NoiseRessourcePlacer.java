package fr.florian.ants.antv1.util.resource;

import fr.florian.ants.antv1.map.ResourceTile;
import fr.florian.ants.antv1.util.OpenSimplexNoise;
import fr.florian.ants.antv1.util.Vector;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class NoiseRessourcePlacer implements IResourcePlacer{
    private List<Resource> selection;
    private OpenSimplexNoise noise;
    private Random rand;

    public NoiseRessourcePlacer(long seed, List<Resource> factories)
    {
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
        noise = new OpenSimplexNoise(seed);
        rand = new Random(seed);
    }
    @Override
    public ResourceTile placeTile(Vector v) {
        double xMin = 0.3;
        double xMax = 0.7;
        double yMin = 0.3;
        double yMax = 0.7;
        double scale = 0.4;
        List<Resource> res = new ArrayList<>();
        if(!selection.isEmpty()) {
            int amount = (int) ((noise.eval(v.getX()*scale, v.getY()*scale))*10);
            for (int i = 0; i < amount; i++) {
                Vector pos = new Vector(new Random().nextDouble(xMin, xMax), new Random().nextDouble(yMin, yMax));
                res.add(selection.get(rand.nextInt(0, selection.size())).clone(pos));
            }
        }
        return new ResourceTile(res);
    }
}
