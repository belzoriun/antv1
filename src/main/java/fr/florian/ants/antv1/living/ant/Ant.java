package fr.florian.ants.antv1.living.ant;

import fr.florian.ants.antv1.living.Living;
import fr.florian.ants.antv1.map.AntHillTile;
import fr.florian.ants.antv1.map.Map;
import fr.florian.ants.antv1.map.Tile;
import fr.florian.ants.antv1.ui.Application;
import fr.florian.ants.antv1.ui.WorldView;
import fr.florian.ants.antv1.util.*;
import fr.florian.ants.antv1.util.signals.AntSignal;
import fr.florian.ants.antv1.util.signals.AntSignalReceiver;
import fr.florian.ants.antv1.util.signals.AntSubscription;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

import java.util.concurrent.Flow;

/**
 * Ant class
 * describing the general behavior of an ant
 */
public abstract class Ant extends Living implements AntSignalReceiver {

    public static final int MAX_SIZE = 10;

    private final double size;
    protected long uniqueAnthillId;
    private boolean weak;
    private final int strength;
    private AntSubscription sub;

    protected Direction headingDirection;

    private final Color color;

    public int getStrength()
    {
        return strength;
    }

    /**
     * Executes an ant action
     */
    protected abstract void executeAction();

    /**
     *  called on each thread loop
     */
    protected void act()
    {
        //request orders
        if(sub != null)
        {
            sub.request(1L);
        }
        Tile t = Map.getInstance().getTile(position);
        if (t == null) {
            return;
        }
        t.onWalkOn(this);
        new FightManager(this, this.position).Hajimeru();
        executeAction();
    }

    /**
     * Called when ant gets killed
     */
    public void onKilled()
    {
        Tile t = Map.getInstance().getTile(position);
        if(t != null) {
            t.onAntDieOn(this);
        }
    }

    public void setPosition(Vector pos)
    {
        this.position = pos;
    }

    public double getSize()
    {
        return size;
    }

    public boolean isWeak()
    {
        return weak;
    }

    /**
     * Called when an ant gets attacked
     * @param l the attacker
     */
    @Override
    public void onAttackedBy(Living l) {
        if(l instanceof Ant a)
        {
            if(a.getStrength() == this.getStrength()) {
                if (a.isWeak() == isWeak()) {
                    if(isWeak())
                    {
                        float rand = Application.random.nextFloat();
                        if (rand > 0.5) {
                            a.kill();
                        } else {
                            kill();
                        }
                    }
                    else {
                        float rand = Application.random.nextFloat();
                        if (rand > 0.5) {
                            a.weaken();
                        } else {
                            weaken();
                        }
                    }
                } else if (isWeak()) {
                    kill();
                } else {
                    a.kill();
                }
            }
            else if(a.getStrength() < this.getStrength())
            {
                if(a.isWeak())
                {
                    a.kill();
                }
                else
                {
                    a.weaken();
                }
            }
            else
            {
                if(isWeak())
                {
                    kill();
                }
                else
                {
                    weaken();
                }
            }
        }
    }

    /**
     * make the ant weak
     */
    public void weaken()
    {
        this.weak = true;
    }

    /**
     * heals the ant, make it no longer weak
     */
    public void heal()
    {
        this.weak = false;
    }

    public long getAntHillId()
    {
        return uniqueAnthillId;
    }

    /**
     * Called when an order is received
     * @param order the recieved order
     */
    protected abstract void onOrderReceived(AntOrder order);

    protected Ant(long anthillId, Color color, Vector ipos, double size, int strenght) {
        super(ipos);
        headingDirection = Direction.UP;
        this.strength = strenght;
        if(size <= 0) size = 1;
        if(size > MAX_SIZE) size = MAX_SIZE;
        this.size = size;
        this.uniqueAnthillId = anthillId;
        this.color = color;
    }

    /**
     * draws the ant
     */
    @Override
    public void draw(GraphicsContext context, Vector position)
    {
        if(Map.getInstance().getTile(this.position) instanceof AntHillTile ah && ah.getUniqueId() == uniqueAnthillId)
        {
            return;
        }
        double dotSize = WorldView.TILE_SIZE / (MAX_SIZE + 1 - size);
        Image i = ResourceLoader.getInstance().loadResource("ant"+color.getRed()+":"+color.getGreen()+":"+color.getBlue());
        Vector center = position.add(WorldView.TILE_SIZE / 2-dotSize/2);
        double rotation = 0;
        if(headingDirection != null) {
            rotation = switch (headingDirection) {
                case LEFT -> 90;
                case RIGHT -> -90;
                case DOWN -> 180;
                default -> 0;
            };
        }
        WorldView.drawRotatedImage(context, i, center, rotation, dotSize);
    }

    public Color getColor() {
        return color;
    }

    @Override
    public void onSubscribe(Flow.Subscription subscription) {
        if(subscription instanceof AntSubscription asub)
        {
            this.sub = asub;
        }
    }

    @Override
    public void onNext(AntSignal item) {
        AntOrder order = item.getOrder(position);
        if(order != null)
        {
            sub.acknowledge(item);
            onOrderReceived(order);
        }
    }

    @Override
    public void onError(Throwable throwable) {
        throwable.printStackTrace();
    }

    @Override
    public void onComplete() {
        this.sub = null;
    }
}
