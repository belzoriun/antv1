package fr.florian.ants.antv1.util.signals;

import fr.florian.ants.antv1.living.ant.Ant;
import fr.florian.ants.antv1.living.ant.QueenAnt;
import fr.florian.ants.antv1.ui.MainPane;
import fr.florian.ants.antv1.util.AntOrder;
import fr.florian.ants.antv1.util.Drawable;
import fr.florian.ants.antv1.util.TickAwaiter;
import fr.florian.ants.antv1.util.Vector;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

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
            Color color = sender.getColor();
            int width = 25;
            for(int i = 0; i<width; i++) {
                double r = color.getRed()+i/255.0;
                double g = color.getGreen()+i/255.0;
                double b = color.getBlue()+i/255.0;
                double a = 1/Math.exp(0.15*i);
                if(r>1) {
                    r = 1;
                }
                if(g>1) {
                    g = 1;
                }
                if(b>1)
                {
                    b=1;
                }
                color = new Color(r, g, b, a);
                drawSignalCircle(context, position, color, (size* MainPane.TILE_SIZE*2)-i);
            }
        }
    }

    private void drawSignalCircle(GraphicsContext context, Vector position, Color color, double size)
    {
        double worldSize = size;
        context.setLineWidth(2);
        context.setStroke(color);
        context.strokeOval(position.getX() * MainPane.TILE_SIZE+MainPane.TILE_SIZE/2-worldSize/2
                , position.getY()*MainPane.TILE_SIZE+MainPane.TILE_SIZE/2-worldSize/2
                , worldSize
                , worldSize);
    }
}
