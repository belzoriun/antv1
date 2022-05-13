package fr.florian.ants.antv1.util.effect;

import fr.florian.ants.antv1.living.Living;

public abstract class Effect {

    public static final Object lock = new Object();

    private int counter;
    private final int ticksPerTrigger;
    private int duration;
    private final int totalDuration;

    protected Effect(int ticksPerTrigger, int duration)
    {
        this.ticksPerTrigger = ticksPerTrigger;
        this.totalDuration = duration;
        this.duration = totalDuration;
        counter = 0;
    }

    public void resetDuration()
    {
        this.duration = totalDuration;
    }

    public boolean depleted()
    {
        return duration <= 0;
    }

    public void apply(Living l)
    {
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

    protected abstract void trigger(Living l);

    public abstract void clear(Living l);

    public abstract void setup(Living living);
}
