package fr.florian.ants.antv1.util.pheromone;

import fr.florian.ants.antv1.map.Tile;
import fr.florian.ants.antv1.util.GameTimer;
import fr.florian.ants.antv1.util.TickWaiter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class used to manage pheromones
 */
public class PheromoneManager extends Thread{

    private static PheromoneManager instance = null;
    private final Map<PheromoneFollower, Long> managedTiles;

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
        try {
            while (executing) {
                synchronized (lock) {
                    while (!toBeManaged.isEmpty()) {
                        PheromoneFollower follower = toBeManaged.remove(0);
                        boolean found = false;
                        for (Map.Entry<PheromoneFollower, Long> entry : managedTiles.entrySet()) {
                            if (entry.getKey().getAntHillId() == follower.getAntHillId()
                                    && entry.getKey().getPheromone().getClass().equals(follower.getPheromone().getClass())
                                    && entry.getKey().getTile() == follower.getTile()) {
                                entry.setValue(0L);
                                found = true;
                                break;
                            }
                        }
                        if (!found) {
                            managedTiles.put(follower, 0L);
                        }
                    }
                }
                TickWaiter.waitTick();
                List<PheromoneFollower> trash = new ArrayList<>();
                for (Map.Entry<PheromoneFollower, Long> entry : managedTiles.entrySet()) {
                    synchronized (entry.getKey().getTile()) {
                        if (entry.getKey().getTile().getPheromoneLevel(entry.getKey().getAntHillId()) <= 0) {
                            trash.add(entry.getKey());
                        } else if (entry.getValue() < GameTimer.getInstance().getTickTime() * entry.getKey().getPheromone().getLifetime()) {
                            managedTiles.put(entry.getKey(), entry.getValue() + GameTimer.getInstance().getTickTime());
                        } else {
                            entry.getKey().getTile().removePheromone(entry.getKey().getAntHillId(), entry.getKey().getPheromone());
                            managedTiles.put(entry.getKey(), 0L);
                        }
                    }
                }
                for (PheromoneFollower t : trash) {
                    managedTiles.remove(t);
                }
            }
        }catch(Exception ignored)
        {
        }
    }

    public void manageTile(Tile t, Pheromone p, long antHillId)
    {
        synchronized (lock) {
            toBeManaged.add(new PheromoneFollower(t, p, antHillId));
        }
    }

}
