package fr.florian.ants.antv1.map.tileplacer;

import fr.florian.ants.antv1.map.Tile;
import fr.florian.ants.antv1.util.OpenSimplexNoise;
import fr.florian.ants.antv1.util.Vector;
import fr.florian.ants.antv1.util.mod.TileFactory;

import java.util.List;

public class OpenSimplexNoiseTilePlacer implements TilePlacer{

    private List<TilePlaceProperty> properties;
    private OpenSimplexNoise noise;

    public OpenSimplexNoiseTilePlacer(Long seed, List<TilePlaceProperty> properties)
    {
        this.properties = properties;
        noise = new OpenSimplexNoise(seed);
    }

    @Override
    public Tile placeTile(Vector pos) {
        double scale = 0.2;
        double value = noise.eval(pos.getX()*scale, pos.getY()*scale);
        for(TilePlaceProperty property : properties)
        {
            if(property.satisfies(value))
            {
                return property.create();
            }
        }
        return null;
    }
}
