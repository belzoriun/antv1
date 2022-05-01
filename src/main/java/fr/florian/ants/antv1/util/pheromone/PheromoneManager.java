package fr.florian.ants.antv1.util.pheromone;

import fr.florian.ants.antv1.map.Tile;
import fr.florian.ants.antv1.ui.Application;
import fr.florian.ants.antv1.util.GameTimer;
import fr.florian.ants.antv1.util.TickAwaiter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PheromoneManager extends Thread{

    private static PheromoneManager instance = null;
    Map<PheromoneFollower, Long> managedTiles;

    private static final Object lock = new Object();

    final List<PheromoneFollower> toBeManaged;
    private boolean executing;

    private PheromoneManager()
    {
        managedTiles = new HashMap<>();
        toBeManaged = new ArrayList<>();
        executing = false;
    }

    public void stopExecution()
    {
        executing = false;
    }

    public static void forceInit()
    {
        instance = new PheromoneManager();
    }

    public static PheromoneManager getInstance()
    {
        if(instance == null)
        {
            instance = new PheromoneManager();
        }
        return instance;
    }

    @Override
    public void start()
    {
        executing = true;
        super.start();
    }

    @Override
    public void run()
    {
        while(executing)
        {
            synchronized (lock) {
                while(!toBeManaged.isEmpty())
                {
                    PheromoneFollower follower = toBeManaged.remove(0);
                    for(Map.Entry<PheromoneFollower, Long> entry : managedTiles.entrySet())
                    {
                        if(entry.getKey().getAntHillId() == follower.getAntHillId()
                                && entry.getKey().getPheromone().getClass().equals(follower.getPheromone().getClass()))
                        {
                            entry.setValue(0L);
                        }
                    }
                    managedTiles.put(follower, 0L);
                }
            }
            TickAwaiter.waitTick();
            List<PheromoneFollower> trash = new ArrayList<>();
            for (Map.Entry<PheromoneFollower, Long> entry : managedTiles.entrySet()) {
                synchronized (entry.getKey().getTile()) {
                    if (entry.getKey().getTile().getPheromoneLevel(entry.getKey().getAntHillId()) <= 0) {
                        trash.add(entry.getKey());
                        continue;
                    } else if (entry.getValue() < GameTimer.getInstance().getTickTime() * 50) {
                        managedTiles.put(entry.getKey(), entry.getValue() + GameTimer.getInstance().getTickTime());
                        continue;
                    }
                    entry.getKey().getTile().removePheromone(entry.getKey().getAntHillId(), entry.getKey().getPheromone());
                }
            }
            for (PheromoneFollower t : trash) {
                managedTiles.remove(t);
            }
        }
    }

    public void manageTile(Tile t, Pheromone p, long antHillId)
    {
        synchronized (lock) {
            toBeManaged.add(new PheromoneFollower(t, p, antHillId));
        }
    }

}
