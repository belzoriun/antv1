package fr.florian.ants.antv1.util.mod;

import fr.florian.ants.antv1.map.ChunkUpdateFeature;
import fr.florian.ants.antv1.util.resource.Resource;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public interface ModLoader {

    static List<Resource> loadModsResource()
    {
        ModLoader mod = new AntWarMod();
        Set<Resource> resources = new HashSet<>(mod.loadResources());
        return resources.stream().toList();
    }

    static List<ChunkUpdateFeature> loadModsUpdateFeatures()
    {
        ModLoader mod = new AntWarMod();
        Set<ChunkUpdateFeature> features = mod.loadUpdateFeatures();
        return features.stream().toList();
    }

    Set<Resource> loadResources();
    Set<ChunkUpdateFeature> loadUpdateFeatures();
}
