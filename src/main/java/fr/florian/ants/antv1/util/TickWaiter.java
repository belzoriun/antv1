package fr.florian.ants.antv1.util;

/**
 * Class used to await ticks
 */
public class TickWaiter {
    private static TickWaiter instance = null;

    private boolean isFree = false;

    private static TickWaiter getInstance()
    {
        if(instance == null)
        {
            instance = new TickWaiter();
        }
        return instance;
    }

    public static void waitTick() {
        synchronized (getInstance())
        {
            if(getInstance().isFree)
            {
                return;
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
}
