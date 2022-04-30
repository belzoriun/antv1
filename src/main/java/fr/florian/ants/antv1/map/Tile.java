package fr.florian.ants.antv1.map;

import fr.florian.ants.antv1.living.Living;
import fr.florian.ants.antv1.living.ant.Ant;
import fr.florian.ants.antv1.util.Drawable;
import fr.florian.ants.antv1.util.pheromone.PheromoneFollower;
import fr.florian.ants.antv1.util.pheromone.PheromoneManager;
import fr.florian.ants.antv1.util.pheromone.Pheromone;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public abstract class Tile implements Drawable {

    private java.util.Map<Long, List<Pheromone>> pheromones;

    protected Tile()
    {
        pheromones = new HashMap<>();
    }

    public void placePheromone(long antHillId, Pheromone p)
    {
        if (!pheromones.containsKey(antHillId)) {
            pheromones.put(antHillId, new ArrayList<>());
        }
        pheromones.get(antHillId).add(p);
        PheromoneManager.getInstance().manageTile(this, p, antHillId);
    }

    public void removePheromone(long antHillId, Pheromone pheromone)
    {
        if (pheromones.containsKey(antHillId)) {
            List<Pheromone> l = pheromones.get(antHillId);
            if (l.isEmpty()) {
                pheromones.remove(antHillId);
                return;
            }
            l.remove(pheromone);
            if (l.isEmpty()) {
                pheromones.remove(antHillId);
            }
        }
    }

    public int getPheromoneLevel(long antHillId)
    {
        List<Pheromone> l = pheromones.get(antHillId);
        if (l != null) {
            return l.size();
        }
        return 0;
    }

    public int getPheromoneLevel(long antHillId, Class<? extends Pheromone> pheromoneType)
    {
        List<Pheromone> l = pheromones.get(antHillId);
        if (l != null) {
            return l.stream().filter((Pheromone p) -> p != null && p.getClass().equals(pheromoneType)).toList().size();
        }
        return 0;
    }

    public List<Pheromone> getAllPheromones()
    {
        List<Pheromone> res = new ArrayList<>();
        for(java.util.Map.Entry<Long, List<Pheromone>> entry : pheromones.entrySet())
        {
            res.addAll(entry.getValue());
        }
        return res;
    }

    public abstract void onWalkOn(Living l);
    public abstract void onInteract(Ant a);
    public abstract void onAntDieOn(Ant a);
}
