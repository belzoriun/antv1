package fr.florian.ants.antv1.map.tileplacer;

import fr.florian.ants.antv1.map.Tile;
import fr.florian.ants.antv1.ui.Application;
import fr.florian.ants.antv1.util.mod.TileFactory;

public class TilePlaceProperty {
    private TileFactory factory;
    private double minNoiseLevel;
    private double maxNoiseLevel;
    private double probability;

    public TilePlaceProperty(TileFactory factory, double probability, double minNoiseLevel, double maxNoiseLevel)
    {
        this.factory = factory;
        this.minNoiseLevel = minNoiseLevel;
        this.maxNoiseLevel = maxNoiseLevel;
        this.probability = probability;
    }

    public boolean satisfies(double value)
    {
        return value >= minNoiseLevel && value <= maxNoiseLevel && Application.random.nextDouble()<probability;
    }

    public Tile create()
    {
        return factory.create();
    }
}
