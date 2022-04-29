package fr.florian.ants.antv1.util.signals;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Flow;
import java.util.concurrent.Future;

public class AntSubscription implements Flow.Subscription {

    private Flow.Subscriber<? super AntSignal> subscriber;
    private final ExecutorService executor;
    private Future<?> future; // to allow cancellation
    private List<AntSignal> signals;

    private static final Object lock = new Object();

    public AntSubscription(Flow.Subscriber<? super AntSignal> sub, ExecutorService executor)
    {
        this.subscriber = sub;
        this.executor = executor;
        this.signals = new ArrayList<>();
    }

    public void emitSignal(AntSignal sig)
    {
        synchronized (lock) {
            signals.add(sig);
        }
    }

    @Override
    public void request(long n) {
        List<AntSignal> trash = new ArrayList<>();
        synchronized (lock) {
            for (AntSignal sig : signals) {
                if (sig.mayDissipate()) {
                    trash.add(sig);
                }
            }
        }
        signals.removeAll(trash);
        future = executor.submit(() -> {
            synchronized (lock) {
                for (AntSignal sig : signals) {
                    subscriber.onNext(sig);
                }
            }
        });
    }

    public void acknoledge(AntSignal sig)
    {
        synchronized (lock) {
            signals.remove(sig);
        }
    }

    @Override
    public void cancel() {
        if(future != null) future.cancel(false);
    }
}
