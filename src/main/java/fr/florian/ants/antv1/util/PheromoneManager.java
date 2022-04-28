package fr.florian.ants.antv1.util;

import fr.florian.ants.antv1.map.Tile;
import fr.florian.ants.antv1.ui.Application;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PheromoneManager extends Thread{

    private static PheromoneManager instance = null;
    Map<Tile, Long> managedTiles;

    private PheromoneManager()
    {
        managedTiles = new HashMap<>();
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
    public void run()
    {
        while(Application.isExecuting())
        {
            TickAwaiter.waitTick();
            List<Tile> trash = new ArrayList<>();
            synchronized (managedTiles) {
                for (Map.Entry<Tile, Long> entry : managedTiles.entrySet()) {
                    if (entry.getKey().getPheromoneLevel() <= 0) {
                        trash.add(entry.getKey());
                        continue;
                    } else if (entry.getValue() < GameTimer.getInstance().getTickTime()*40) {
                        managedTiles.put(entry.getKey(), entry.getValue() + GameTimer.getInstance().getTickTime());
                        continue;
                    }
                    entry.getKey().removePheromone();
                }
                for (Tile t : trash) {
                    managedTiles.remove(t);
                }
            }
        }
    }

    public void manageTile(Tile t)
    {
        synchronized (managedTiles)
        {
            managedTiles.put(t, 0L);
        }
    }

}
