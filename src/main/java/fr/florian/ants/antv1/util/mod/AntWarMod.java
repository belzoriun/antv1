package fr.florian.ants.antv1.util.mod;

import fr.florian.ants.antv1.map.ChunkUpdateFeature;
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
        return features;
    }
}
