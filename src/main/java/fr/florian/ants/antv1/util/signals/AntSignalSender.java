package fr.florian.ants.antv1.util.signals;

import java.util.List;
import java.util.concurrent.Flow;

/**
 * Interface for ant signal send
 */
public interface AntSignalSender extends Flow.Publisher<AntSignal>{
    List<AntSignal> getSignalList();
}
