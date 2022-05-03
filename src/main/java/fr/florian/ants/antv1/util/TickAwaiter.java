package fr.florian.ants.antv1.util;

import fr.florian.ants.antv1.util.exception.TickFreeException;

public class TickAwaiter {
    private static TickAwaiter instance = null;

    private boolean isFree = false;

    private static TickAwaiter getInstance()
    {
        if(instance == null)
        {
            instance = new TickAwaiter();
        }
        return instance;
    }

    public static void waitTick() throws TickFreeException {
        if(getInstance().isFree)
        {
            throw new TickFreeException();
        }
        synchronized (getInstance())
        {
            try {
                getInstance().wait();
            } catch (InterruptedException e) {
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
