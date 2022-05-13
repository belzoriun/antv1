package fr.florian.ants.antv1.living;

import fr.florian.ants.antv1.map.Map;
import fr.florian.ants.antv1.map.Tile;
import fr.florian.ants.antv1.ui.WorldView;
import fr.florian.ants.antv1.util.*;
import fr.florian.ants.antv1.util.effect.Effect;
import fr.florian.ants.antv1.util.fight.Attacker;
import fr.florian.ants.antv1.util.fight.FightManager;
import fr.florian.ants.antv1.util.statemachine.StateMachine;
import javafx.scene.Node;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;

/**
 * The class representing a living entity
 */
public abstract class Living implements Runnable, Attacker, Drawable {

    public static final God GOD = God.getInstance();
    private volatile boolean alive;
    protected Vector position;
    protected Direction headingDirection;
    private StateMachine stateMachine;
    protected double lifePoints;
    protected final double maxLifePoints;
    protected double strength;
    private int tickCounter;
    private int ticksPerExecution;
    private final int initialTicksPerOperation;
    private List<Effect> effects;

    protected Living(Vector pos, int ticksPerExecution, double maxLifePoints, double strength)
    {
        headingDirection = Direction.UP;
        effects = new ArrayList<>();
        this.ticksPerExecution = ticksPerExecution;
        tickCounter = ticksPerExecution;
        initialTicksPerOperation = ticksPerExecution;
        this.alive= true;
        position = pos;
        this.strength = strength;
        this.maxLifePoints = maxLifePoints;
        stateMachine = new StateMachine.StateMachineBuilder()
                .addState("idle", ()->{})
                .get("idle");
        lifePoints = maxLifePoints;
    }

    public void applyEffect(Effect e)
    {
        synchronized (Effect.lock) {
            List<Effect> existing = effects.stream().filter(ef -> e.getClass() == ef.getClass()).toList();
            if (existing.isEmpty()) {
                e.setup(this);
                effects.add(e);
            } else {
                existing.get(0).resetDuration();
            }
        }
    }

    public void removeEffect(Effect e)
    {
        synchronized (Effect.lock) {
            e.clear(this);
            effects.remove(e);
        }
    }

    public void clearEffects()
    {
        synchronized (Effect.lock) {
            for (Effect e : effects) {
                e.clear(this);
            }
            effects.clear();
        }
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

    protected void executeDirectly(String nextTransition)
    {
        if (nextTransition != null && !nextTransition.isEmpty())
            stateMachine.setTransition(nextTransition);
        stateMachine.step();
    }

    @Override
    public void run() {
        try {
            while (this.alive && TickWaiter.isLocked()) {
                TickWaiter.waitTick();
                List<Effect> trash = new ArrayList<>();
                synchronized (Effect.lock) {
                    for (Effect e : effects) {
                        e.apply(this);
                        if (e.depleted()) {
                            trash.add(e);
                        }
                    }
                    for (Effect e : trash) {
                        removeEffect(e);
                    }
                }
                tickCounter --;
                if(tickCounter <= 0) {
                    Tile t = Map.getInstance().getTile(position);
                    if (t == null) {
                        break;
                    }
                    String nextTransition = getNextAction();
                    if (nextTransition != null && !nextTransition.isEmpty())
                        stateMachine.setTransition(nextTransition);
                    stateMachine.step();
                    Tile cur = Map.getInstance().getTile(position);
                    if (cur == null) {
                        return;
                    }
                    new FightManager(this, this.position).Hajimeru();
                    cur.onWalkOn(this);
                    tickCounter = ticksPerExecution;
                }
                onUpdate();
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
    public void kill(Attacker killer) {
        this.alive = false;
        if(killer != GOD) {
            killer.onVictory(this);
            onKilled(killer);
        }
    }

    /**
     * Called when a living entity is killed
     */
    public abstract void onKilled(Attacker killer);

    public void hit(Attacker attacker, double damage)
    {
        this.lifePoints -= damage;
        if(this.lifePoints <= 0)
        {
            kill(attacker);
        }
    }

    public void onVictory(Living l)
    {
        //does nothing (may apply a special effect)
    }

    public double getStrength()
    {
        return strength;
    }

    public void setTicksPerExecution(int ticks)
    {
        ticksPerExecution = ticks;
    }

    public void resetTicksPerExecution()
    {
        ticksPerExecution = initialTicksPerOperation;
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
        effects = new ArrayList<>();
    }
    public abstract Node getDetailDisplay();

    protected void drawLifepoints(GraphicsContext context, Vector position, double size)
    {
        if(lifePoints < maxLifePoints)
        {
            Vector start = position.add(new Vector((WorldView.TILE_SIZE-size)/2, WorldView.TILE_SIZE-(WorldView.TILE_SIZE-size)/2));
            context.setFill(Color.LIGHTGRAY);
            context.fillRect(start.getX(), start.getY(), size, WorldView.TILE_SIZE/10);

            double lifeWidth = size*lifePoints/maxLifePoints;
            context.setFill(Color.RED);
            context.fillRect(start.getX(), start.getY(), lifeWidth, WorldView.TILE_SIZE/10);
        }
    }

    public void onUpdate(){ }

    public int getTicksPerExecution() {
        return ticksPerExecution;
    }
}
