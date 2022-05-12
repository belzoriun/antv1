package fr.florian.ants.antv1.util.resource;

import fr.florian.ants.antv1.map.ResourceHoldTile;
import fr.florian.ants.antv1.map.ResourceTile;
import fr.florian.ants.antv1.map.Tile;
import fr.florian.ants.antv1.ui.Application;
import fr.florian.ants.antv1.util.OpenSimplexNoise;
import fr.florian.ants.antv1.util.Vector;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Class placing resources using open simplex noise
 */
public class NoiseResourcePlacer implements IResourcePlacer{
    private final List<Resource> selection;
    private final OpenSimplexNoise noise;
    private final Random rand;

    public NoiseResourcePlacer(long seed, List<Resource> factories)
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
        rand = Application.random;
    }
    @Override
    public void placeResources(Vector v, ResourceHoldTile t) {
        double xMin = 0.3;
        double xMax = 0.7;
        double yMin = 0.3;
        double yMax = 0.7;
        double scale = 0.4;
        if(!selection.isEmpty()) {
            int amount = (int) ((noise.eval(v.getX()*scale, v.getY()*scale))*10);
            for (int i = 0; i < amount; i++) {
                Resource selected = selection.get(rand.nextInt(0, selection.size()));
                Vector pos = new Vector(Application.random.nextDouble(xMin, xMax), Application.random.nextDouble(yMin, yMax));
                t.placeResource(selected.clone(pos));
            }
        }
    }
}
