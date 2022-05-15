package fr.florian.ants.antv1.util.fight;

import fr.florian.ants.antv1.living.LivingEntity;
import fr.florian.ants.antv1.living.ant.entity.AntEntity;
import fr.florian.ants.antv1.map.Map;
import fr.florian.ants.antv1.util.Vector;

import java.util.List;

/**
 * Class used to manage fights between entities
 */
public class FightManager {

    final List<LivingEntity> fighters;
    private final LivingEntity fighter;
    private final Vector position;

    public FightManager(LivingEntity l, Vector position)
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
            for (LivingEntity opponent : fighters) {
                if (opponent != fighter && opponent.isAlive()) {
                    if (fighter instanceof AntEntity f && opponent instanceof AntEntity o) {
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
