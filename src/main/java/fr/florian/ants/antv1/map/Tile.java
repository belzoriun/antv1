package fr.florian.ants.antv1.map;

import fr.florian.ants.antv1.living.Living;
import fr.florian.ants.antv1.living.ant.Ant;
import fr.florian.ants.antv1.ui.Application;
import fr.florian.ants.antv1.util.Drawable;
import fr.florian.ants.antv1.util.PheromoneManager;

public abstract class Tile implements Drawable {

    private int pheromoneLevel;

    protected Tile()
    {
        pheromoneLevel = 0;
    }

    public void placePheromone()
    {
        pheromoneLevel++;
        PheromoneManager.getInstance().manageTile(this);
    }

    public void removePheromone()
    {
        pheromoneLevel--;
    }

    public int getPheromoneLevel()
    {
        return pheromoneLevel;
    }

    public abstract void onWalkOn(Living l);
    public abstract void onInteract(Ant a);
}
