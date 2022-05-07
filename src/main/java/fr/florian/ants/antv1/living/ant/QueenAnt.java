package fr.florian.ants.antv1.living.ant;

import fr.florian.ants.antv1.util.AntOrder;
import fr.florian.ants.antv1.util.GameTimer;
import fr.florian.ants.antv1.util.Vector;
import fr.florian.ants.antv1.util.signals.AntSignal;
import fr.florian.ants.antv1.util.signals.AntSignalSender;
import fr.florian.ants.antv1.util.signals.AntSubscription;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Flow;
import java.util.concurrent.ForkJoinPool;

/**
 * Class representing a queen ant
 */
public class QueenAnt extends Ant implements AntSignalSender {
    private final ExecutorService executor = ForkJoinPool.commonPool(); // daemon-based
    private final List<AntSignal> sigs;
    private final List<AntSubscription> subs;
    private int timeOperationCounter;

    private static final int TICKS_PER_OPERATION = 50;

    public QueenAnt(long anthillId, Color color, Vector ipos) {
        super(anthillId, color, ipos, 10, 10);
        sigs = new ArrayList<>();
        subs = new ArrayList<>();
        timeOperationCounter = TICKS_PER_OPERATION;
    }

    @Override
    protected void executeAction() {
        if(timeOperationCounter <= 0) {
            AntOrder order = AntOrder.SEARCH_FOR_FOOD;
            if(GameTimer.getInstance().getRemainingTimeFraction()<=0.3)
            {
                order = AntOrder.BACK_TO_COLONY;
            }
            AntSignal newSig = new AntSignal(this, position, order, 30, 0.3);
            for (AntSubscription sub : subs) {
                sub.emitSignal(newSig);
            }
            sigs.add(newSig);
            new Thread(newSig).start();
            timeOperationCounter = TICKS_PER_OPERATION;
        }
        List<AntSignal> trash = new ArrayList<>();
        for (AntSignal sig : sigs) {
            if (sig.mayDissipate()) {
                trash.add(sig);
            }
        }
        sigs.removeAll(trash);
        timeOperationCounter--;
    }

    @Override
    protected void onOrderReceived(AntOrder order) {
        //ignore all orders
    }

    @Override
    public void subscribe(Flow.Subscriber<? super AntSignal> subscriber) {
        AntSubscription sub = new AntSubscription(subscriber, executor);
        subs.add(sub);
        subscriber.onSubscribe(sub);
    }

    @Override
    public List<AntSignal> getSignalList() {
        return new ArrayList<>(sigs);
    }
}
