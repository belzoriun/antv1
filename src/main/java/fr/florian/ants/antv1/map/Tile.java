package fr.florian.ants.antv1.map;

import fr.florian.ants.antv1.living.Living;
import fr.florian.ants.antv1.living.ant.Ant;
import fr.florian.ants.antv1.ui.Application;
import fr.florian.ants.antv1.util.Drawable;

public abstract class Tile extends Thread implements Drawable {

    private int pheromoneLevel;

    protected Tile()
    {
        pheromoneLevel = 0;
    }

    public void run()
    {
        while(Application.isExecuting())
        {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                break;
            }
            if(pheromoneLevel>0)pheromoneLevel--;
        }
    }

    public void placePheromone()
    {
        pheromoneLevel++;
    }

    public int getPheromoneLevel()
    {
        return pheromoneLevel;
    }

    public Tile startSelf()
    {
        this.start();
        return this;
    }

    public abstract void onWalkOn(Living l);
    public abstract void onInteract(Ant a);
}
