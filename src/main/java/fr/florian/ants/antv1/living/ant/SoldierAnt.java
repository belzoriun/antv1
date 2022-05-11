package fr.florian.ants.antv1.living.ant;

import fr.florian.ants.antv1.living.Living;
import fr.florian.ants.antv1.map.AntHillTile;
import fr.florian.ants.antv1.map.Map;
import fr.florian.ants.antv1.util.AntOrder;
import fr.florian.ants.antv1.util.Direction;
import fr.florian.ants.antv1.util.Vector;
import fr.florian.ants.antv1.util.signals.AntSignal;
import fr.florian.ants.antv1.util.signals.AntSignalSender;
import fr.florian.ants.antv1.util.signals.AntSubscription;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Flow;
import java.util.concurrent.ForkJoinPool;

/**
 * Class representing a commander ant
 */
public class SoldierAnt extends Ant implements AntSignalSender{
    private final ExecutorService executor = ForkJoinPool.commonPool(); // daemon-based

    private final List<AntSignal> signals;
    private final List<AntSubscription> subs;
    private final Vector initialPosition;
    private int actionCounter;

    private static final double MAX_ANTHILL_DISTANCE = 20;
    private static final int TICKS_PER_ACTION = 3;

    public SoldierAnt(long anthillId, QueenAnt q, Color color, Vector initialPosition) {
        super(anthillId, color, initialPosition, 9.2, 10, 3);
        signals = new ArrayList<>();
        subs = new ArrayList<>();
        q.subscribe(this);
        this.initialPosition = initialPosition;
        actionCounter = TICKS_PER_ACTION;
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    protected String executeAction() {
        if(actionCounter <= 0) {
            headingDirection = Direction.random();
            while (position.add(headingDirection.getOffset()).delta(initialPosition) > MAX_ANTHILL_DISTANCE
                    || Map.getInstance().getTile(position.add(headingDirection.getOffset())) == null
                    || (Map.getInstance().getTile(position.add(headingDirection.getOffset())) instanceof AntHillTile a && a.getUniqueId() != uniqueAnthillId)) {
                headingDirection = Direction.random();
            }
            position = position.add(headingDirection.getOffset());
            synchronized (signals) {
                List<AntSignal> trash = new ArrayList<>();
                for (AntSignal sig : signals) {
                    if (sig.mayDissipate()) {
                        trash.add(sig);
                    }
                }
                signals.removeAll(trash);
            }
            actionCounter = TICKS_PER_ACTION;
        }
        actionCounter --;
        return "";
    }

    @Override
    protected void onOrderReceived(AntOrder order) {
        AntSignal newSig = new AntSignal(this, position, order, 15, 0.3);
        for(AntSubscription sub : subs)
        {
            sub.emitSignal(newSig);
        }
        synchronized (signals) {
            signals.add(newSig);
            new Thread(newSig).start();
        }
    }

    @Override
    public List<AntSignal> getSignalList() {
        return signals;
    }

    @Override
    public void subscribe(Flow.Subscriber<? super AntSignal> subscriber) {
        AntSubscription sub = new AntSubscription(subscriber, executor);
        subs.add(sub);
        subscriber.onSubscribe(sub);
    }

    @Override
    protected void onAttackedBy(Living l) {

    }

    @Override
    public Node getDetailDisplay() {
        VBox box = new VBox();
        box.getChildren().add(new Label("Life : "+(lifePoints/maxLifePoints*100)+"%"));
        box.getChildren().add(new Label("This ant is purely random (for now)"));
        return box;
    }
}
