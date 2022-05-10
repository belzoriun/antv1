package fr.florian.ants.antv1.living;

import fr.florian.ants.antv1.util.Direction;
import fr.florian.ants.antv1.util.Drawable;
import fr.florian.ants.antv1.util.TickWaiter;
import fr.florian.ants.antv1.util.Vector;
import javafx.scene.Node;

/**
 * The class representing a living entity
 */
public abstract class Living implements Runnable, Drawable {

    private boolean alive;
    protected Vector position;
    protected Direction headingDirection;

    protected Living(Vector pos)
    {
        headingDirection = Direction.UP;
        this.alive= true;
        position = pos;
    }

    public Vector getPosition()
    {
        return position;
    }

    protected abstract void act();

    @Override
    public void run() {
        try {
            while (this.alive) {
                TickWaiter.waitTick();
                act();
            }
        }catch(Exception ignored)
        {
        }
    }

    /**
     * Kills the entity ant prepare its thread to be stopped
     */
    public void kill() {
        this.alive = false;
        onKilled();
    }

    /**
     * Called when a living entity is killed
     */
    public abstract void onKilled();

    /**
     * Attacks another living entity
     * @param l The entity attacked
     */
    public void attack(Living l)
    {
        l.onAttackedBy(this);
    }

    /**
     * Called when attacked by another living entity
     * @param l The attacker
     */
    protected abstract void onAttackedBy(Living l);

    public boolean isAlive() {
        return this.alive;
    }
    public void revive()
    {
        alive = true;
    }
    public abstract Node getDetailDisplay();
}
