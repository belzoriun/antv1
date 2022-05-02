package fr.florian.ants.antv1.util.exception;

import fr.florian.ants.antv1.util.TickAwaiter;

public class TickFreeException extends Exception{
    public TickFreeException()
    {
        super("Can't await a free tick");
    }
}
