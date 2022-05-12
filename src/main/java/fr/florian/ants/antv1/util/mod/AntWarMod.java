package fr.florian.ants.antv1.util.mod;

import fr.florian.ants.antv1.living.insects.Tarentula;
import fr.florian.ants.antv1.map.*;
import fr.florian.ants.antv1.map.tileplacer.TilePlaceProperty;
import fr.florian.ants.antv1.ui.Application;
import fr.florian.ants.antv1.util.GameTimer;
import fr.florian.ants.antv1.util.Vector;
import fr.florian.ants.antv1.util.resource.*;

import java.util.HashSet;
import java.util.Set;

public class AntWarMod implements ModLoader{

    @Override
    public Set<Resource> loadResources() {
        Set<Resource> res = new HashSet<>();
        res.add(new BasicResource(null));
        res.add(new RareResource(null));
        res.add(new ExtremelyRareResource(null));
        res.add(new FoodResource(null));
        return res;
    }

    @Override
    public Set<ChunkUpdateFeature> loadUpdateFeatures() {
        Set<ChunkUpdateFeature> features = new HashSet<>();
        features.add(new ChunkUpdateFeature.ResourceUpdateFeature(new BasicResource(null)));
        features.add(new ChunkUpdateFeature.ResourceUpdateFeature(new RareResource(null)));
        features.add(new ChunkUpdateFeature.ResourceUpdateFeature(new ExtremelyRareResource(null)));
        features.add(new ChunkUpdateFeature.ResourceUpdateFeature(new FoodResource(null)));
        features.add((Vector v, Tile t)->{
            if(!GameTimer.getInstance().isDay() && Application.random.nextDouble()<0.00001)
            {
                Map.getInstance().spawn(new Tarentula(v), false);
            }
        });
        return features;
    }

    @Override
    public Set<TilePlaceProperty> loadTileFactories() {
        Set<TilePlaceProperty> res = new HashSet<>();
        res.add(new TilePlaceProperty(ResourceTile::new, 1, -0.5, 1));
        res.add(new TilePlaceProperty(WaterTile::new, 1, -1, -0.5));
        return res;
    }
}
