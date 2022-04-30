package fr.florian.ants.antv1.util.pheromone;

import fr.florian.ants.antv1.map.Tile;

public class PheromoneFollower {
    private Pheromone pheromone;
    private long antHillId;
    private Tile tile;

    public PheromoneFollower(Tile t, Pheromone pheromone, long hillId)
    {
        this.antHillId = hillId;
        this.pheromone=pheromone;
        tile= t;
    }

    public Pheromone getPheromone()
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
