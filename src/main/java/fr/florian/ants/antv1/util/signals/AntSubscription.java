package fr.florian.ants.antv1.util.signals;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Flow;
import java.util.concurrent.Future;

/**
 * Class used as subscription media between ants
 */
public class AntSubscription implements Flow.Subscription {

    private final Flow.Subscriber<? super AntSignal> subscriber;
    private final ExecutorService executor;
    private Future<?> future; // to allow cancellation
    private final List<AntSignal> signals;

    private final Object lock = new Object();

    public AntSubscription(Flow.Subscriber<? super AntSignal> sub, ExecutorService executor)
    {
        this.subscriber = sub;
        this.executor = executor;
        this.signals = new ArrayList<>();
        sub.onSubscribe(this);
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

    public void acknowledge(AntSignal sig)
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
