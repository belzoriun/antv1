package fr.florian.ants.antv1.living;

import fr.florian.ants.antv1.map.Map;
import fr.florian.ants.antv1.util.Drawable;
import fr.florian.ants.antv1.util.GameTimer;
import fr.florian.ants.antv1.util.TickAwaiter;
import fr.florian.ants.antv1.util.Vector;

public abstract class Living implements Runnable, Drawable {

    private boolean alive;
    protected Vector position;

    private long lastTimeAct;

    protected Living(Vector pos)
    {
        this.alive= true;
        lastTimeAct = 0L;
        position = pos;
    }

    public Vector getPosition()
    {
        return position;
    }

    protected abstract void act();

    @Override
    public void run() {
        while(this.alive)
        {
            TickAwaiter.waitTick();
            act();
        }
    }

    public void kill() {
        this.alive = false;
        onKilled();
    }

    public abstract void onKilled();

    public void attack(Living l)
    {
        l.onAttackedBy(this);
    }

    protected abstract void onAttackedBy(Living l);

    public boolean isAlive() {
        return this.alive;
    }
}
