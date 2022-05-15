package fr.florian.ants.antv1.util.mod;

import fr.florian.ants.antv1.living.insects.Insects;
import fr.florian.ants.antv1.living.insects.Tarantula;
import fr.florian.ants.antv1.living.insects.entity.InsectEntity;
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
    public Set<TilePlaceProperty> loadTileFactories() {
        Set<TilePlaceProperty> res = new HashSet<>();
        res.add(new TilePlaceProperty(ResourceTile::new, 1, -0.5, 1));
        res.add(new TilePlaceProperty(WaterTile::new, 1, -1, -0.5));
        return res;
    }

    @Override
    public Set<LivingSpawnFactory> loadLivingSpawns() {
        Set<LivingSpawnFactory> factories = new HashSet<>();
        factories.add((Vector v)->{
            if(Application.random.nextDouble() < 0.00001 && !GameTimer.getInstance().isDay())
            {
                Map.getInstance().spawn(Insects.SPIDER.createEntity(v), false);
            }
        });
        return factories;
    }
}
