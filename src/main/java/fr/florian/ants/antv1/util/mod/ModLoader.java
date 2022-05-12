package fr.florian.ants.antv1.util.mod;

import fr.florian.ants.antv1.map.ChunkUpdateFeature;
import fr.florian.ants.antv1.map.Tile;
import fr.florian.ants.antv1.map.tileplacer.TilePlaceProperty;
import fr.florian.ants.antv1.util.resource.Resource;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

public interface ModLoader {

    static List<Resource> loadModsResource()
    {
        ModLoader mod = new AntWarMod();
        Set<Resource> resources = new HashSet<>(mod.loadResources());
        return resources.stream().toList();
    }

    static List<TilePlaceProperty> loadModsTiles()
    {
        ModLoader mod = new AntWarMod();
        Set<TilePlaceProperty> tiles = new HashSet<>(mod.loadTileFactories());
        return tiles.stream().toList();
    }

    static List<ChunkUpdateFeature> loadModsUpdateFeatures()
    {
        ModLoader mod = new AntWarMod();
        Set<ChunkUpdateFeature> features = mod.loadUpdateFeatures();
        return features.stream().toList();
    }

    Set<Resource> loadResources();
    Set<ChunkUpdateFeature> loadUpdateFeatures();
    Set<TilePlaceProperty> loadTileFactories();
}
