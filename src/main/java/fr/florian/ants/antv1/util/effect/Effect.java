package fr.florian.ants.antv1.util.effect;

import fr.florian.ants.antv1.living.Living;
import fr.florian.ants.antv1.living.LivingEntity;

import java.util.concurrent.locks.ReentrantLock;

public abstract class Effect {

    private int counter;
    private final int ticksPerTrigger;
    private int duration;
    private final int totalDuration;
    private boolean firstApplyDone;

    protected Effect(int ticksPerTrigger, int duration)
    {
        this.ticksPerTrigger = ticksPerTrigger;
        this.totalDuration = duration;
        this.duration = totalDuration;
        counter = 0;
        firstApplyDone = false;
    }

    public void resetDuration()
    {
        this.duration = totalDuration;
    }

    public boolean depleted()
    {
        return duration <= 0;
    }

    public void apply(LivingEntity l)
    {
        if(!firstApplyDone)
        {
            trigger(l);
            firstApplyDone = true;
        }
        if(duration <= 0)
        {
            clear(l);
        }
        else {
            if (counter >= ticksPerTrigger) {
                trigger(l);
                counter = 0;
            }
            counter++;
            duration--;
        }
    }

    protected abstract void trigger(LivingEntity l);

    public abstract void clear(LivingEntity l);
}
