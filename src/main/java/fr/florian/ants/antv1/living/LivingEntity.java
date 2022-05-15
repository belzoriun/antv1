package fr.florian.ants.antv1.living;

import fr.florian.ants.antv1.living.ant.entity.AntEntity;
import fr.florian.ants.antv1.map.Map;
import fr.florian.ants.antv1.map.Tile;
import fr.florian.ants.antv1.ui.WorldView;
import fr.florian.ants.antv1.util.Direction;
import fr.florian.ants.antv1.util.Drawable;
import fr.florian.ants.antv1.util.TickWaiter;
import fr.florian.ants.antv1.util.Vector;
import fr.florian.ants.antv1.util.effect.Effect;
import fr.florian.ants.antv1.util.fight.Attacker;
import fr.florian.ants.antv1.util.fight.FightManager;
import javafx.scene.Node;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

public abstract class LivingEntity implements Runnable, Attacker, Drawable {

    private final ReentrantLock effectLock = new ReentrantLock(true);

    private volatile boolean alive;
    protected Vector position;
    protected Direction headingDirection;
    protected double lifePoints;
    private int tickCounter;
    private int ticksPerExecution;
    private List<Effect> effects;
    private final Living living;
    private String currentstate;

    public void setState(String state)
    {
        currentstate = state;
    }

    public String getState()
    {
        return currentstate;
    }

    public LivingEntity(String initialState, Vector pos, Living l)
    {
        headingDirection = Direction.UP;
        effects = new ArrayList<>();
        this.ticksPerExecution = l.getTicksPerExecution();
        tickCounter = ticksPerExecution;
        this.alive= true;
        currentstate = initialState;
        position = pos;
        lifePoints = l.getMaxLifePoints();
        living = l;
    }

    public void setHeadingDirection(Direction dir)
    {
        headingDirection = dir;
    }

    public Direction getHeadingDirection()
    {
        return headingDirection;
    }

    @Override
    public void run() {
        try {
            while (this.alive && TickWaiter.isLocked()) {
                TickWaiter.waitTick();
                List<Effect> trash = new ArrayList<>();
                effectLock.lock();
                for (Effect e : effects) {
                    e.apply(this);
                    if (e.depleted()) {
                        trash.add(e);
                    }
                }
                for (Effect e : trash) {
                    removeEffect(e);
                }
                effectLock.unlock();
                tickCounter --;
                if(tickCounter <= 0) {
                    Tile t = Map.getInstance().getTile(position);
                    if (t == null) {
                        break;
                    }
                    living.execute(this);
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

    public Vector getPosition()
    {
        return position;
    }

    public void setPosition(Vector v)
    {
        position = v;
    }

    public void applyEffect(Effect e)
    {
        effectLock.lock();
        List<Effect> existing = effects.stream().filter(ef -> e.getClass() == ef.getClass()).toList();
        if (existing.isEmpty()) {
            e.apply(this);
            effects.add(e);
        } else {
            existing.get(0).resetDuration();
        }
        effectLock.unlock();
    }

    public void removeEffect(Effect e)
    {
        effectLock.lock();
        e.clear(this);
        effects.remove(e);
        effectLock.unlock();
    }

    public void clearEffects()
    {
        effectLock.lock();
        for (Effect e : effects) {
            e.clear(this);
        }
        effects.clear();
        effectLock.unlock();
    }

    public abstract void onUpdate();

    public void hit(Attacker attacker, double damage)
    {
        this.lifePoints -= damage;
        if(this.lifePoints <= 0)
        {
            kill(attacker);
        }
    }

    protected abstract void onKilled(Attacker killer);

    /**
     * Kills the entity ant prepare its thread to be stopped
     */
    public void kill(Attacker killer) {
        this.alive = false;
        if(killer != Living.GOD) {
            killer.onVictory(this);
            onKilled(killer);
        }
    }

    public void setTicksPerExecution(int ticks)
    {
        ticksPerExecution = ticks;
    }

    public void resetTicksPerExecution()
    {
        ticksPerExecution = living.getTicksPerExecution();
    }

    /**
     * heals the ant
     */
    public void heal(double heal)
    {
        this.lifePoints += heal;
        if(lifePoints > living.getMaxLifePoints()) lifePoints = living.getMaxLifePoints();
    }

    /**
     * Attacks another living entity
     * @param l The entity attacked
     */
    public void attack(LivingEntity l)
    {
        l.hit(this, living.getStrength());
    }

    public boolean isAlive() {
        return this.alive;
    }

    public void revive()
    {
        alive = true;
        effects = new ArrayList<>();
    }

    protected void drawLifepoints(GraphicsContext context, Vector position, double size)
    {
        if(lifePoints < living.getMaxLifePoints())
        {
            Vector start = position.add(new Vector((WorldView.TILE_SIZE-size)/2, WorldView.TILE_SIZE-(WorldView.TILE_SIZE-size)/2));
            context.setFill(Color.LIGHTGRAY);
            context.fillRect(start.getX(), start.getY(), size, WorldView.TILE_SIZE/10);

            double lifeWidth = size*lifePoints/ living.getMaxLifePoints();
            context.setFill(Color.RED);
            context.fillRect(start.getX(), start.getY(), lifeWidth, WorldView.TILE_SIZE/10);
        }
    }

    public double getLifePoints() {
        return lifePoints;
    }

    public final Living getLiving() {
        return living;
    }

    public abstract Node getDetailDisplay();
}
