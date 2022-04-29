package fr.florian.ants.antv1.living.ant;

import fr.florian.ants.antv1.living.Living;
import fr.florian.ants.antv1.map.Map;
import fr.florian.ants.antv1.util.AntOrder;
import fr.florian.ants.antv1.util.Direction;
import fr.florian.ants.antv1.util.Drawable;
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

public class SoldierAnt extends Ant implements AntSignalSender{
    private final ExecutorService executor = ForkJoinPool.commonPool(); // daemon-based

    private List<AntSignal> sigs;
    private List<AntSubscription> subs;

    public SoldierAnt(long anthillId, Color color, Vector ipos) {
        super(anthillId, color, ipos, 9.2, 3);
        sigs = new ArrayList<>();
        subs = new ArrayList<>();
    }

    @Override
    protected void executeAction() {
        headingDirection = Direction.random();
        while(Map.getInstance().getTile(position.add(headingDirection.getOffset())) == null) {
            headingDirection = Direction.random();
        }
        position = position.add(headingDirection.getOffset());
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