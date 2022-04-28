package fr.florian.ants.antv1.util;

import fr.florian.ants.antv1.living.Living;
import fr.florian.ants.antv1.living.ant.Ant;
import fr.florian.ants.antv1.living.ant.SoldierAnt;
import fr.florian.ants.antv1.living.ant.WorkerAnt;
import fr.florian.ants.antv1.map.Map;

import java.util.List;
import java.util.Random;

public class FightManager {

    List<Living> fighters;
    private Living fighter;

    public FightManager(Living l, Vector position)
    {
        this.fighters = Map.getInstance().getLivingsAt(position);
        fighter = l;
    }

    public void ajime()
    {
        synchronized (fighter) {
            for (Living opponent : fighters) {
                if (opponent != fighter) {
                    if ((fighter instanceof Ant f && opponent instanceof Ant o && f.getAntHillId() != o.getAntHillId())
                            || fighter instanceof Ant && !(opponent instanceof Ant)) {
                        fighter.attack(opponent);
                    }
                }
            }
        }
    }
}
