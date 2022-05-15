package fr.florian.ants.antv1.util.effect;

import fr.florian.ants.antv1.living.Living;
import fr.florian.ants.antv1.living.LivingEntity;
import fr.florian.ants.antv1.util.fight.Attacker;

public class Wet extends Effect implements Attacker {

    private boolean triggeredSlowness;

    public Wet() {
        super(10, 50);
        triggeredSlowness = false;
    }

    @Override
    protected void trigger(LivingEntity l) {
        if(!triggeredSlowness)
        {
            l.setTicksPerExecution((int) ((l.getLiving().getTicksPerExecution()+5)*1.3));
            triggeredSlowness = true;
        }
        attack(l);
    }

    @Override
    public void clear(LivingEntity l) {
        l.resetTicksPerExecution();
    }

    @Override
    public void attack(LivingEntity l) {
        l.hit(this, 0.2);
    }

    @Override
    public void onVictory(LivingEntity l) {

    }
}
