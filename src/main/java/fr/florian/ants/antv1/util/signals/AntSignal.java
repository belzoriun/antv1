package fr.florian.ants.antv1.util.signals;

import fr.florian.ants.antv1.living.ant.Ant;
import fr.florian.ants.antv1.ui.WorldView;
import fr.florian.ants.antv1.util.AntOrder;
import fr.florian.ants.antv1.util.Drawable;
import fr.florian.ants.antv1.util.TickAwaiter;
import fr.florian.ants.antv1.util.Vector;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class AntSignal implements Runnable, Drawable {
    private final Vector from;
    private double size;
    private final double maxSize;
    private final AntOrder order;
    private final double signalSpeed;
    private final Ant sender;

    private static final Object lock = new Object();

    public AntSignal(Ant sender, Vector pos, AntOrder order, double maxSize, double signalSpeed)
    {
        this.from = pos;
        this.sender=sender;
        this.order = order;
        this.size = 0;
        this.maxSize = maxSize;
        this.signalSpeed = signalSpeed;
    }

    public Vector getSourcePosition()
    {
        return from;
    }

    public boolean isAccessible(Vector from)
    {
        synchronized (lock) {
            return this.from.delta(from) < this.size;
        }
    }

    public AntOrder getOrder(Vector from)
    {
        synchronized (lock) {
            if (!isAccessible(from)) {
                return null;
            }
            return order;
        }
    }

    @Override
    public void run() {
        while(size < maxSize)
        {
            TickAwaiter.waitTick();
            synchronized (lock) {
                size += signalSpeed;
            }
        }
    }

    public boolean mayDissipate()
    {
        synchronized (lock) {
            return size >= maxSize;
        }
    }

    @Override
    public void draw(GraphicsContext context, Vector position) {
        synchronized (lock)
        {
            context.setLineWidth(5);
            Color color = sender.getColor();
            double r = color.getRed();
            double g = color.getGreen();
            double b = color.getBlue();
            double a = Math.pow(Math.log(size/maxSize), 2);
            if(a>1) a = 1;
            color = new Color(r, g, b, a);
            drawSignalCircle(context, position, color, (size* WorldView.TILE_SIZE*2));
        }
    }

    private void drawSignalCircle(GraphicsContext context, Vector position, Color color, double size)
    {
        double worldSize = size;
        context.setLineWidth(2);
        context.setStroke(color);
        if(order == AntOrder.BACKTOCOLONY) {
            context.setLineDashes(10);
        }
        else if(order == AntOrder.SEARCHFORFOOD)
        {
            context.setLineDashes(0);
        }
        context.strokeOval(position.getX() * WorldView.TILE_SIZE+ WorldView.TILE_SIZE/2-worldSize/2
                , position.getY()* WorldView.TILE_SIZE+ WorldView.TILE_SIZE/2-worldSize/2
                , worldSize
                , worldSize);
    }
}
