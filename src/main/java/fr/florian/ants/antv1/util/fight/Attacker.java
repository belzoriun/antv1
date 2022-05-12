package fr.florian.ants.antv1.util.fight;

import fr.florian.ants.antv1.living.Living;

public interface Attacker {
    public void attack(Living l);
    public void onVictory(Living l);
}
