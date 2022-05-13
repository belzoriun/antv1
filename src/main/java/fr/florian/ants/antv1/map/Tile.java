package fr.florian.ants.antv1.map;

import fr.florian.ants.antv1.living.Living;
import fr.florian.ants.antv1.living.ant.Ant;
import fr.florian.ants.antv1.util.Drawable;
import fr.florian.ants.antv1.util.pheromone.Pheromone;
import fr.florian.ants.antv1.util.pheromone.PheromoneManager;
import fr.florian.ants.antv1.util.pheromone.PheromoneSet;
import javafx.scene.Node;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Class representing basic tile behavior
 */
public abstract class Tile implements Drawable {

    private final java.util.Map<Long, PheromoneSet> pheromones;

    protected Tile()
    {
        pheromones = new HashMap<>();
    }

    /**
     * Add 1 to the pheromone value of the given pheromone type for an ant colony
     * @param antHillId The ant hill of the colony
     * @param p The pheromone type to add
     */
    public void placePheromone(long antHillId, Pheromone p)
    {
        if (!pheromones.containsKey(antHillId) || pheromones.get(antHillId) == null) {
            pheromones.put(antHillId, new PheromoneSet());
        }
        pheromones.get(antHillId).add(p);
        PheromoneManager.getInstance().manageTile(this, p, antHillId);
    }

    /**
     * Remove 1 from the pheromone value for the given pheromone type and the given ant colony
     * @param antHillId The ant hill id of the colony
     * @param pheromone The pheromone type
     */
    public void removePheromone(long antHillId, Pheromone pheromone)
    {
        if(pheromones.containsKey(antHillId))
        {
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
        PheromoneSet p = pheromones.get(antHillId);
        if(p==null)
        {
            return 0;
        }
        return p.getTotalLevel();
    }

    public int getPheromoneLevel(long antHillId, Class<? extends Pheromone> pheromoneType)
    {
        PheromoneSet set = pheromones.get(antHillId);
        if(set == null)
        {
            return 0;
        }
        return set.getTotalLevel(pheromoneType);
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

    /**
     * Called when an entity walks over this tile
     * @param l The entity
     */
    public abstract void onWalkOn(Living l);

    /**
     * Called when an ant interacts with this tile
     * @param a The ant interacting
     */
    public abstract void onInteract(Ant a);

    /**
     * Called when an ant dies on this tile
     * @param a The dying ant
     */
    public abstract void onAntDieOn(Ant a);

    /**
     * Gets the display specification of this tile
     * @return The node to display
     */
    public abstract Node getInfoDisplay();

    public abstract void tickUpdate();
}
