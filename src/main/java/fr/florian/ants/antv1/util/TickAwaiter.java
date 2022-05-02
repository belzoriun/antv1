package fr.florian.ants.antv1.util;

import fr.florian.ants.antv1.util.exception.TickFreeException;

public class TickAwaiter {
    private static TickAwaiter instance = null;

    private boolean isFree = false;

    public static void waitTick() throws TickFreeException {
        if(instance == null)
        {
            instance = new TickAwaiter();
        }
        if(instance.isFree)
        {
            throw new TickFreeException();
        }
        synchronized (instance)
        {
            try {
                instance.wait();
            } catch (InterruptedException e) {
            }
        }
    }

    public static void free()
    {
        instance.isFree = true;
        emitTick();
    }

    public static void emitTick()
    {
        if(instance == null)
        {
            instance = new TickAwaiter();
        }
        synchronized (instance) {
            instance.notifyAll();
        }
    }
}
