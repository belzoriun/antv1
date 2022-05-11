package fr.florian.ants.antv1.living;

import fr.florian.ants.antv1.map.Map;
import fr.florian.ants.antv1.map.Tile;
import fr.florian.ants.antv1.util.*;
import fr.florian.ants.antv1.util.statemachine.StateMachine;
import javafx.scene.Node;
import javafx.scene.image.Image;

/**
 * The class representing a living entity
 */
public abstract class Living implements Runnable, Drawable {

    public static final Living GOD = God.getInstance();
    private volatile boolean alive;
    protected Vector position;
    protected Direction headingDirection;
    private StateMachine stateMachine;
    protected double lifePoints;
    protected final double maxLifePoints;
    private final double strength;

    protected Living(Vector pos, double maxLifePoints, double strength)
    {
        headingDirection = Direction.UP;
        this.alive= true;
        position = pos;
        this.strength = strength;
        this.maxLifePoints = maxLifePoints;
        stateMachine = new StateMachine.StateMachineBuilder()
                .addState("idle", ()->{})
                .get("idle");
        lifePoints = maxLifePoints;
    }

    protected void initCore(StateMachine machine)
    {
        this.stateMachine = machine;
    }

    public Vector getPosition()
    {
        return position;
    }

    protected abstract String getNextAction();

    @Override
    public void run() {
        try {
            while (this.alive) {
                TickWaiter.waitTick(this);

                Tile t = Map.getInstance().getTile(position);
                if (t == null) {
                    kill(Living.GOD);
                    break;
                }
                new FightManager(this, this.position).Hajimeru();
                String nextTransition = getNextAction();
                if(nextTransition != null && !nextTransition.isEmpty())
                    stateMachine.setTransition(nextTransition);
                stateMachine.step();
                Tile cur = Map.getInstance().getTile(position);
                if(cur == null)
                {
                    return;
                }
                cur.onWalkOn(this);
            }
        }catch(Exception ignored)
        {
            ignored.printStackTrace();
        }
    }

    public Image getStateMachineDisplay()
    {
        return stateMachine.getAsImage();
    }

    /**
     * Kills the entity ant prepare its thread to be stopped
     */
    public void kill(Living killer) {
        this.alive = false;
        if(killer != GOD)
            onKilled(killer);
        TickWaiter.freeFor(this);
    }

    /**
     * Called when a living entity is killed
     */
    public abstract void onKilled(Living killer);

    protected void hit(Living attacker, double damage)
    {
        this.lifePoints -= damage;
        if(this.lifePoints <= 0)
        {
            kill(attacker);
        }
    }

    public double getStrength()
    {
        return strength;
    }

    /**
     * heals the ant
     */
    public void heal(double heal)
    {
        this.lifePoints += heal;
        if(lifePoints > maxLifePoints) lifePoints = maxLifePoints;
    }

    /**
     * Attacks another living entity
     * @param l The entity attacked
     */
    public void attack(Living l)
    {
        l.hit(this, this.strength);
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
