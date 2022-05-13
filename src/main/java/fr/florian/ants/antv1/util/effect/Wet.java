package fr.florian.ants.antv1.util.effect;

import fr.florian.ants.antv1.living.Living;
import fr.florian.ants.antv1.util.fight.Attacker;

public class Wet extends Effect implements Attacker {

    private boolean triggeredSlowness;

    public Wet() {
        super(10, 50);
        triggeredSlowness = false;
    }

    @Override
    protected void trigger(Living l) {
        if(!triggeredSlowness)
        {
            l.setTicksPerExecution((int) ((l.getTicksPerExecution()+5)*1.3));
            triggeredSlowness = true;
        }
        attack(l);
    }

    @Override
    public void clear(Living l) {
        l.resetTicksPerExecution();
    }

    @Override
    public void setup(Living living) {

    }

    @Override
    public void attack(Living l) {
        l.hit(this, 0.2);
    }

    @Override
    public void onVictory(Living l) {

    }
}
