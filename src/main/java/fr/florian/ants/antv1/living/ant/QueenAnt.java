package fr.florian.ants.antv1.living.ant;

import fr.florian.ants.antv1.util.AntOrder;
import fr.florian.ants.antv1.util.signals.AntSignal;
import fr.florian.ants.antv1.util.Vector;
import fr.florian.ants.antv1.util.signals.AntSignalSender;
import fr.florian.ants.antv1.util.signals.AntSubscription;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Flow;
import java.util.concurrent.ForkJoinPool;

public class QueenAnt extends Ant implements AntSignalSender {
    private final ExecutorService executor = ForkJoinPool.commonPool(); // daemon-based
    private List<AntSignal> sigs;
    private List<AntSubscription> subs;
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
            AntSignal newSig = new AntSignal(this, position, AntOrder.SEARCHFORFOOD, 30, 0.3);
            for (AntSubscription sub : subs) {
                sub.emitSignal(newSig);
            }
            synchronized (sigs) {
                sigs.add(newSig);
                new Thread(newSig).start();
            }
            timeOperationCounter = TICKS_PER_OPERATION;
        }
        synchronized (sigs)
        {
            List<AntSignal> trash = new ArrayList<>();
            for (AntSignal sig : sigs) {
                if (sig.mayDissipate()) {
                    trash.add(sig);
                }
            }
            sigs.removeAll(trash);
        }
        timeOperationCounter--;
    }

    @Override
    protected void onOrderRecieved(AntOrder order) {
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
        synchronized (sigs) {
            return sigs;
        }
    }
}
