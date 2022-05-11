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
import fr.florian.ants.antv1.util.statemachine.StateMachine;
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
    protected final long uniqueAnthillId;
    private AntSubscription sub;

    private final Color color;

    protected Ant(long anthillId, Color color, Vector initialPosition, double size, double lifePoints, int strength) {
        super(initialPosition, lifePoints, strength);
        if(size <= 0) size = 1;
        if(size > MAX_SIZE) size = MAX_SIZE;
        this.size = size;
        this.uniqueAnthillId = anthillId;
        this.color = color;
    }

    /**
     * Executes an ant action
     */
    protected abstract String executeAction();

    /**
     *  called on each thread loop
     */
    protected String getNextAction()
    {
        //request orders
        if(sub != null)
        {
            sub.request(1L);
        }
        return executeAction();
    }

    /**
     * Called when ant gets killed
     */
    public void onKilled(Living killer)
    {
        Tile t = Map.getInstance().getTile(position);
        if(t != null) {
            t.onAntDieOn(this);
        }
    }

    public void onGettingKilledBy(Living killer){}

    public void setPosition(Vector pos)
    {
        this.position = pos;
    }

    public double getSize()
    {
        return size;
    }

    public long getAntHillId()
    {
        return uniqueAnthillId;
    }

    /**
     * Called when an order is received
     * @param order the received order
     */
    protected abstract void onOrderReceived(AntOrder order);

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
        Vector center = position.add(WorldView.TILE_SIZE / 2);
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
        if(lifePoints < maxLifePoints)
        {
            Vector start = position.add(new Vector(WorldView.TILE_SIZE/8, WorldView.TILE_SIZE-WorldView.TILE_SIZE/6));
            context.setFill(Color.LIGHTGRAY);
            context.fillRect(start.getX(), start.getY(), WorldView.TILE_SIZE-WorldView.TILE_SIZE/4, WorldView.TILE_SIZE/10);

            double lifeWidth = (WorldView.TILE_SIZE-WorldView.TILE_SIZE/4)*lifePoints/maxLifePoints;
            context.setFill(Color.RED);
            context.fillRect(start.getX(), start.getY(), lifeWidth, WorldView.TILE_SIZE/10);
        }
    }

    public Color getColor() {
        return color;
    }

    @Override
    public void onSubscribe(Flow.Subscription subscription) {
        if(subscription instanceof AntSubscription antSubscription)
        {
            this.sub = antSubscription;
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
