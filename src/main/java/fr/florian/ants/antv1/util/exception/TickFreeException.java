package fr.florian.ants.antv1.util.exception;

/**
 * Exception used when the tick is awaited when it's free
 */
public class TickFreeException extends Exception{
    public TickFreeException()
    {
        super("Can't await a free tick");
    }
}
