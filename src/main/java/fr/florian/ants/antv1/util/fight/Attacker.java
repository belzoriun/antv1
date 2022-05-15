package fr.florian.ants.antv1.util.fight;

import fr.florian.ants.antv1.living.Living;
import fr.florian.ants.antv1.living.LivingEntity;

public interface Attacker {
    public void attack(LivingEntity l);
    public void onVictory(LivingEntity l);
}
