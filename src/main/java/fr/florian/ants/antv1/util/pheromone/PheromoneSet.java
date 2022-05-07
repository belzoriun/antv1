package fr.florian.ants.antv1.util.pheromone;

import java.util.ArrayList;
import java.util.List;

/**
 * Class used as a pheromone restrictive list (base on arraylist)
 */
public class PheromoneSet {
    private final List<Pheromone> pheromones;

    public PheromoneSet()
    {
        pheromones = new ArrayList<>();
    }

    public void add(Pheromone p)
    {
        if(pheromones.contains(p))
        {
            pheromones.get(pheromones.indexOf(p)).add();
        }
        else
        {
            pheromones.add(p);
        }
    }

    public boolean hasPheromones()
    {
        return pheromones.size()>0;
    }

    public <T extends Pheromone> void remove(Class<T> pheromoneType)
    {
        List<Pheromone> v = pheromones.stream().filter((Pheromone p) -> p != null && p.getClass().equals(pheromoneType)).toList();
        if(!v.isEmpty())
        {
            v.get(0).remove();
            if(v.get(0).getWeight() <= 0)
            {
                pheromones.remove(v.get(0));
            }
        }
    }

    public int getTotalLevel() {
        int lvl = 0;
        for(Pheromone p : pheromones)
        {
            lvl += p.getWeight();
        }
        return lvl;
    }

    public <T extends Pheromone> int getTotalLevel(Class<T> type)
    {
        List<Pheromone> v = pheromones.stream().filter((Pheromone p) -> p != null && p.getClass().equals(type)).toList();
        int lvl = 0;
        for(Pheromone p : v)
        {
            lvl += p.getWeight();
        }
        return lvl;
    }

    public List<Pheromone> getAllPheromones() {
        return pheromones;
    }
}
