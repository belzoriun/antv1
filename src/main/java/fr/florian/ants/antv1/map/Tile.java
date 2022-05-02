package fr.florian.ants.antv1.map;

import fr.florian.ants.antv1.living.Living;
import fr.florian.ants.antv1.living.ant.Ant;
import fr.florian.ants.antv1.util.Drawable;
import fr.florian.ants.antv1.util.pheromone.PheromoneFollower;
import fr.florian.ants.antv1.util.pheromone.PheromoneManager;
import fr.florian.ants.antv1.util.pheromone.Pheromone;
import fr.florian.ants.antv1.util.pheromone.PheromoneSet;

import java.util.*;

public abstract class Tile implements Drawable {

    private java.util.Map<Long, PheromoneSet> pheromones;

    protected Tile()
    {
        pheromones = new HashMap<>();
    }

    public void placePheromone(long antHillId, Pheromone p)
    {
        if (!pheromones.containsKey(antHillId)) {
            pheromones.put(antHillId, new PheromoneSet());
        }
        pheromones.get(antHillId).add(p);
        PheromoneManager.getInstance().manageTile(this, p, antHillId);
    }

    public void removePheromone(long antHillId, Class<? extends Pheromone> pheromone)
    {
        if(!pheromones.containsKey(antHillId))
        {
            return;
        }
        if (pheromones.containsKey(antHillId)) {
            PheromoneSet l = pheromones.get(antHillId);
            if (l.hasPheromones()) {
                pheromones.remove(antHillId);
                return;
            }
            pheromones.get(antHillId).remove(pheromone);
            if (l.hasPheromones()) {
                pheromones.remove(antHillId);
            }
        }
    }

    public int getPheromoneLevel(long antHillId)
    {
        if(!pheromones.containsKey(antHillId))
        {
            return 0;
        }
        return pheromones.get(antHillId).getTotalLevel();
    }

    public int getPheromoneLevel(long antHillId, Class<? extends Pheromone> pheromoneType)
    {
        if(!pheromones.containsKey(antHillId))
        {
            return 0;
        }
        return pheromones.get(antHillId).getTotalLevel(pheromoneType);
    }

    public List<Pheromone> getAllPheromones()
    {
        List<Pheromone> res = new ArrayList<>();
        for(java.util.Map.Entry<Long, PheromoneSet> entry : pheromones.entrySet())
        {
            res.addAll(entry.getValue().getAllPheromones());
        }
        return res;
    }

    public abstract void onWalkOn(Living l);
    public abstract void onInteract(Ant a);
    public abstract void onAntDieOn(Ant a);
}
