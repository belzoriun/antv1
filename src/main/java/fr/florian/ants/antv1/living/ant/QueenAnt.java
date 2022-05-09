package fr.florian.ants.antv1.living.ant;

import fr.florian.ants.antv1.map.AntHillTile;
import fr.florian.ants.antv1.map.Map;
import fr.florian.ants.antv1.ui.Application;
import fr.florian.ants.antv1.ui.WorldView;
import fr.florian.ants.antv1.util.AntOrder;
import fr.florian.ants.antv1.util.GameTimer;
import fr.florian.ants.antv1.util.Vector;
import fr.florian.ants.antv1.util.option.OptionKey;
import fr.florian.ants.antv1.util.signals.AntSignal;
import fr.florian.ants.antv1.util.signals.AntSignalSender;
import fr.florian.ants.antv1.util.signals.AntSubscription;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Flow;
import java.util.concurrent.ForkJoinPool;

/**
 * Class representing a queen ant
 */
public class QueenAnt extends Ant implements AntSignalSender {
    private final ExecutorService executor = ForkJoinPool.commonPool(); // daemon-based
    private final List<AntSignal> signals;
    private final List<AntSubscription> subs;
    private int timeOperationCounter;

    private static final int TICKS_PER_OPERATION = 50;

    public QueenAnt(long anthillId, Color color, Vector initialPosition) {
        super(anthillId, color, initialPosition, 10, 10);
        signals = new ArrayList<>();
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
            signals.add(newSig);
            new Thread(newSig).start();

            if(Application.random.nextDouble() < 0.25)
            {
                makeSpawnNewAnt();
            }

            timeOperationCounter = TICKS_PER_OPERATION;
        }
        List<AntSignal> trash = new ArrayList<>();
        for (AntSignal sig : signals) {
            if (sig.mayDissipate()) {
                trash.add(sig);
            }
        }
        signals.removeAll(trash);
        timeOperationCounter--;
    }

    private void makeSpawnNewAnt() {
        int amount = Application.random.nextInt(10, 20);
        java.util.Map<SoldierAnt, Integer> companies = new HashMap<>();
        for (Ant a : Map.getInstance().getAntsOf(getAntHillId())) {
            if (a instanceof SoldierAnt s) {
                companies.put(s, 0);
            } else if (a instanceof WorkerAnt w) {
                if (companies.containsKey(w.getSolder())) {
                    companies.put(w.getSolder(), companies.get(w.getSolder()) + 1);
                } else {
                    companies.put(w.getSolder(), 0);
                }
            }
        }

        for(int i = 0; i<amount; i++) {
            SoldierAnt soldier = null;
            for (java.util.Map.Entry<SoldierAnt, Integer> entry : companies.entrySet()) {
                if (entry.getValue() < Application.options.getInt(OptionKey.WORKER_PER_SOLDIER)) {
                    soldier = entry.getKey();
                    break;
                }
            }

            synchronized ((AntHillTile) Map.getInstance().getTile(position)) {

                AntHillTile hill = (AntHillTile) Map.getInstance().getTile(position);
                if (soldier != null && hill.consumeFood(1)) {
                    Ant ant = new WorkerAnt(getAntHillId(), soldier, getColor(), position);
                    Map.getInstance().spawn(ant);
                    companies.put(soldier, companies.get(soldier)+1);
                } else if (soldier == null && hill.consumeFood(3)) {
                    SoldierAnt s = new SoldierAnt(getAntHillId(), this, getColor(), position);
                    Map.getInstance().spawn(s);
                    companies.put(s, 0);
                }
            }
        }
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
        return new ArrayList<>(signals);
    }
}
