package fr.florian.ants.antv1.living;

import fr.florian.ants.antv1.util.Drawable;
import fr.florian.ants.antv1.util.Vector;

public abstract class Living implements Runnable, Drawable {

    private float actionThreshold; //in ms
    private boolean alive;
    protected Vector position;

    private long lastTimeAct;

    protected Living(Vector pos, float actionThreshold)
    {
        this.actionThreshold = actionThreshold;
        this.alive= true;
        lastTimeAct = 0L;
        position = pos;
    }

    public Vector getPosition()
    {
        return position;
    }

    protected abstract void executeAction();

    @Override
    public void run() {
        while(this.alive)
        {
            executeAction();
            try {
                Thread.sleep((long) actionThreshold);
            } catch (InterruptedException e) {
                this.alive = false;
            }
        }
    }

    public void kill() {
        this.alive = false;
    }
}
