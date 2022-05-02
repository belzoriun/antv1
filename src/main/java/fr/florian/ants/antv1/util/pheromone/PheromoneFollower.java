package fr.florian.ants.antv1.util.pheromone;

import fr.florian.ants.antv1.map.Tile;

public class PheromoneFollower {
    private Class<? extends Pheromone> pheromone;
    private long antHillId;
    private Tile tile;

    public PheromoneFollower(Tile t, Pheromone pheromone, long hillId)
    {
        this.antHillId = hillId;
        this.pheromone=pheromone.getClass();
        tile= t;
    }

    public Class<? extends Pheromone> getPheromone()
    {
        return pheromone;
    }

    public Tile getTile()
    {
        return tile;
    }

    public long getAntHillId()
    {
        return antHillId;
    }
}
