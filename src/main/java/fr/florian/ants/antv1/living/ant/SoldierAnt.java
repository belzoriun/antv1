package fr.florian.ants.antv1.living.ant;

import fr.florian.ants.antv1.living.Living;
import fr.florian.ants.antv1.map.Map;
import fr.florian.ants.antv1.util.*;
import fr.florian.ants.antv1.util.signals.AntSignal;
import fr.florian.ants.antv1.util.signals.AntSignalSender;
import fr.florian.ants.antv1.util.signals.AntSubscription;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Flow;
import java.util.concurrent.ForkJoinPool;

public class SoldierAnt extends Ant implements AntSignalSender{
    private final ExecutorService executor = ForkJoinPool.commonPool(); // daemon-based

    private List<AntSignal> sigs;
    private List<AntSubscription> subs;
    private Vector initialPosition;
    private int actionCounter;

    private static final double MAX_ANTHILL_DISTANCE = 20;
    private static final int TICKS_PER_ACTION = 3;

    public SoldierAnt(long anthillId, Color color, Vector ipos) {
        super(anthillId, color, ipos, 9.2, 3);
        sigs = new ArrayList<>();
        subs = new ArrayList<>();
        initialPosition = ipos;
        actionCounter = TICKS_PER_ACTION;
    }

    @Override
    protected void executeAction() {
        if(actionCounter <= 0) {
            headingDirection = Direction.random();
            while (position.add(headingDirection.getOffset()).delta(initialPosition) > MAX_ANTHILL_DISTANCE || Map.getInstance().getTile(position.add(headingDirection.getOffset())) == null) {
                headingDirection = Direction.random();
            }
            position = position.add(headingDirection.getOffset());
            synchronized (sigs) {
                List<AntSignal> trash = new ArrayList<>();
                for (AntSignal sig : sigs) {
                    if (sig.mayDissipate()) {
                        trash.add(sig);
                    }
                }
                sigs.removeAll(trash);
            }
            actionCounter = TICKS_PER_ACTION;
        }
        actionCounter --;
    }

    @Override
    protected void onOrderRecieved(AntOrder order) {
        AntSignal newSig = new AntSignal(this, position, order, 15, 0.3);
        for(AntSubscription sub : subs)
        {
            sub.emitSignal(newSig);
        }
        synchronized (sigs) {
            sigs.add(newSig);
            new Thread(newSig).start();
        }
    }

    @Override
    public List<AntSignal> getSignalList() {
        return sigs;
    }

    @Override
    public void subscribe(Flow.Subscriber<? super AntSignal> subscriber) {
        AntSubscription sub = new AntSubscription(subscriber, executor);
        subs.add(sub);
        subscriber.onSubscribe(sub);
    }
}
