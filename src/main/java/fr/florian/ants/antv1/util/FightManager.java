package fr.florian.ants.antv1.util;

import fr.florian.ants.antv1.living.Living;
import fr.florian.ants.antv1.living.ant.Ant;
import fr.florian.ants.antv1.map.Map;

import java.util.ArrayList;
import java.util.List;

/**
 * Class used to manage fights between entities
 */
public class FightManager {

    final List<Living> fighters;
    private final Living fighter;
    private final Vector position;

    public FightManager(Living l, Vector position)
    {
        this.position = position;
        this.fighters = Map.getInstance().getLivingsAt(position);
        fighter = l;
    }

    /**
     * Starts the fight
     */
    public void Hajimeru()
    {
        if(position == null || Map.getInstance().getTile(position) == null)
        {
            return;
        }
        synchronized (Map.getInstance().getTile(position)) {
            for (Living opponent : fighters) {
                if (opponent != fighter && opponent.isAlive()) {
                    if (fighter instanceof Ant f && opponent instanceof Ant o) {
                        if(f.getAntHillId() != o.getAntHillId()) {
                            fighter.attack(opponent);
                        }
                    }
                    else
                    {
                        fighter.attack(opponent);
                    }
                }
            }
        }
    }
}
