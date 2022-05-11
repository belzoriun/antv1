package fr.florian.ants.antv1.util;

import fr.florian.ants.antv1.living.Living;

import java.util.ArrayList;
import java.util.List;

/**
 * Class used to await ticks
 */
public class TickWaiter {
    private static TickWaiter instance = null;

    private boolean isFree = false;
    private List<Object> except;

    public TickWaiter()
    {
        except = new ArrayList<>();
    }

    private static TickWaiter getInstance()
    {
        if(instance == null)
        {
            instance = new TickWaiter();
        }
        return instance;
    }

    public static void waitTick(Object waiter) {
        synchronized (getInstance())
        {
            if(getInstance().isFree)
            {
                return;
            }
            if(getInstance().except.contains(waiter))
            {
                getInstance().except.remove(waiter);
            }
            try {
                getInstance().wait();
            } catch (InterruptedException ignored) {
            }
        }
    }

    public static void free()
    {
        getInstance().isFree = true;
        emitTick();
    }

    public static void lock()
    {
        getInstance().isFree = false;
    }

    public static void emitTick()
    {
        synchronized (getInstance()) {
            getInstance().notifyAll();
        }
    }

    public static boolean isLocked() {
        return !getInstance().isFree;
    }

    public static void freeFor(Object o) {
        getInstance().except.add(o);
    }
}
