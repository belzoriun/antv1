package fr.florian.ants.antv1.util;

public class TickAwaiter {
    private static TickAwaiter instance = null;

    public static void waitTick()
    {
        if(instance == null)
        {
            instance = new TickAwaiter();
        }
        synchronized (instance)
        {
            try {
                instance.wait();
            } catch (InterruptedException e) {
            }
        }
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
