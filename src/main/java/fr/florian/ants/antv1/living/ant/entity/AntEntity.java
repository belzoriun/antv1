package fr.florian.ants.antv1.living.ant.entity;

import fr.florian.ants.antv1.living.LivingEntity;
import fr.florian.ants.antv1.living.ant.Ant;
import fr.florian.ants.antv1.map.AntHillTile;
import fr.florian.ants.antv1.map.Map;
import fr.florian.ants.antv1.map.Tile;
import fr.florian.ants.antv1.ui.WorldView;
import fr.florian.ants.antv1.util.*;
import fr.florian.ants.antv1.util.fight.Attacker;
import fr.florian.ants.antv1.util.signals.AntSignal;
import fr.florian.ants.antv1.util.signals.AntSignalReceiver;
import fr.florian.ants.antv1.util.signals.AntSignalSender;
import fr.florian.ants.antv1.util.signals.AntSubscription;
import javafx.scene.Node;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Flow;
import java.util.concurrent.ForkJoinPool;

public class AntEntity extends LivingEntity implements AntSignalReceiver, AntSignalSender {

    protected long uniqueAnthillId;
    protected AntSubscription sub;
    protected final List<AntSignal> signals;
    protected final List<AntSubscription> subs;
    protected Color color;
    protected Ant ant;
    private List<AntEntity> referrers;

    private final ExecutorService executor = ForkJoinPool.commonPool();

    public AntEntity(String initialState, Vector initialPosition, Ant ant) {
        super(initialState, initialPosition, ant);
        this.ant = ant;
        signals = new ArrayList<>();
        subs = new ArrayList<>();
        referrers = new ArrayList<>();
    }

    public void setReferrers(List<AntEntity> r)
    {
        for(AntEntity e : r)
        {
            e.subscribe(this);
        }
        referrers = r;
    }

    public List<AntEntity> getReferrers()
    {
        return referrers;
    }

    public void onUpdate()
    {
        if(sub != null)
        {
            sub.request(1L);
        }
        synchronized (signals) {
            List<AntSignal> trash = new ArrayList<>();
            for (AntSignal sig : new ArrayList<>(signals)) {
                if (sig != null && sig.mayDissipate()) {
                    trash.add(sig);
                }
            }
            signals.removeAll(trash);
        }
        getLiving().onUpdate(this);
    }

    public Color getColor() {
        return color;
    }

    public long getAntHillId()
    {
        return uniqueAnthillId;
    }

    public void setPosition(Vector pos)
    {
        this.position = pos;
    }


    @Override
    public void onSubscribe(Flow.Subscription subscription) {
        if(subscription instanceof AntSubscription antSubscription)
        {
            this.sub = antSubscription;
        }
    }

    /**
     * Called when ant gets killed
     */
    public void onKilled(Attacker killer)
    {
        Tile t = Map.getInstance().getTile(position);
        if(t != null) {
            getLiving().onKilled(killer, this);
            t.onAntDieOn(this);
        }
    }

    @Override
    public void onNext(AntSignal item) {
        AntOrder order = item.getOrder(position);
        if(order != null)
        {
            sub.acknowledge(item);
            ant.onOrderReceived(this, order);
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
        double dotSize = WorldView.TILE_SIZE / (Ant.MAX_SIZE + 1 - ant.getSize());
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
        drawLifepoints(context, position, dotSize);
    }

    public Node getDetailDisplay() {
        VBox box = new VBox();
        box.getChildren().add(new Label("Life : "+Math.round(lifePoints/ant.getMaxLifePoints()*100)+"%"));
        return box;
    }

    @Override
    public void onVictory(LivingEntity l) {

    }

    public void sendSignal(AntSignal newSig) {
        synchronized (signals) {
            for (AntSubscription sub : subs) {
                sub.emitSignal(newSig);
            }
            signals.add(newSig);
            new Thread(newSig).start();
        }
    }

    @Override
    public List<AntSignal> getSignalList() {
        return new ArrayList<>(signals);
    }

    @Override
    public void subscribe(Flow.Subscriber<? super AntSignal> subscriber) {
        AntSubscription subscription = new AntSubscription(subscriber, executor);
        subscriber.onSubscribe(subscription);
        subs.add(subscription);
    }

    public void addToColony(long uniqueAnthillId, Color color)
    {
        this.uniqueAnthillId = uniqueAnthillId;
        this.color=color;
        ResourceLoader.getInstance().saveResource("ant"+color.getRed()+":"+color.getGreen()+":"+color.getBlue(),
                ImageColorMaker.colorAntImage(ResourceLoader.getInstance().loadResource(ResourceLoader.ANT), color));
    }
}
