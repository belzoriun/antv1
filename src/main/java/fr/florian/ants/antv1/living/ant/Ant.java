package fr.florian.ants.antv1.living.ant;

import fr.florian.ants.antv1.living.Living;
import fr.florian.ants.antv1.map.Map;
import fr.florian.ants.antv1.map.Tile;
import fr.florian.ants.antv1.ui.MainPane;
import fr.florian.ants.antv1.util.*;
import fr.florian.ants.antv1.util.signals.AntSignal;
import fr.florian.ants.antv1.util.signals.AntSignalReciever;
import fr.florian.ants.antv1.util.signals.AntSubscription;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.transform.Rotate;

import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.Flow;

public abstract class Ant extends Living implements AntSignalReciever {

    private static final int MAX_SIZE = 10;
    private static long CURRENT_ANT_ID = 0L;

    private double size;
    protected long uniqueAnthillId;
    private boolean weak;
    private final int strenght;
    private long antId;
    private AntSubscription sub;

    private java.util.Map<Direction, Image> antImages;

    protected Direction headingDirection;

    private Color color;

    public int getStrenght()
    {
        return strenght;
    }

    protected abstract void executeAction();

    protected void act()
    {
        if(sub != null)
        {
            sub.request(1L);
        }
        synchronized (Map.getInstance()) {
            Tile t = Map.getInstance().getTile(position);
            if (t == null) {
                return;
            }
            t.onWalkOn(this);
            new FightManager(this, this.position).ajime();
        }
        executeAction();
    }

    public void onKilled()
    {
        Tile t = Map.getInstance().getTile(position);
        if(t != null) {
            synchronized (t) {
                t.onAntDieOn(this);
            }
        }
    }

    public boolean isWeak()
    {
        return weak;
    }

    @Override
    public void onAttackedBy(Living l) {
        if(l instanceof Ant a)
        {
            if(a.getStrenght() == this.getStrenght()) {
                if (a.isWeak() == isWeak()) {
                    float rand = new Random().nextFloat();
                    if (rand > 0.5) {
                        a.weaken();
                    } else {
                        weaken();
                    }
                } else if (isWeak()) {
                    kill();
                } else {
                    a.kill();
                }
            }
            else if(a.getStrenght() < this.getStrenght())
            {
                if(a.isWeak())
                {
                    a.kill();
                }
                else
                {
                    a.weaken();
                }
            }
            else
            {
                if(isWeak())
                {
                    kill();
                }
                else
                {
                    weaken();
                }
            }
        }
    }

    public long getId()
    {
        return antId;
    }


    public void weaken()
    {
        this.weak = true;
    }

    public void heal()
    {
        this.weak = false;
    }

    public long getAntHillId()
    {
        return uniqueAnthillId;
    }

    protected abstract void onOrderRecieved(AntOrder order);

    protected Ant(long anthillId, Color color, Vector ipos, double size, int strenght) {
        super(ipos);
        headingDirection = Direction.UP;
        this.strenght = strenght;
        if(size <= 0) size = 1;
        if(size > MAX_SIZE) size = MAX_SIZE;
        this.size = size;
        this.uniqueAnthillId = anthillId;
        this.color = color;
        antImages = new HashMap<>();
        this.antId = CURRENT_ANT_ID++;

        antImages.put(Direction.LEFT, colorAntImage(ResourceLoader.getInstance().loadResource(ResourceLoader.ANT_LEFT), color));
        antImages.put(Direction.RIGHT, colorAntImage(ResourceLoader.getInstance().loadResource(ResourceLoader.ANT_RIGHT), color));
        antImages.put(Direction.UP, colorAntImage(ResourceLoader.getInstance().loadResource(ResourceLoader.ANT_UP), color));
        antImages.put(Direction.DOWN, colorAntImage(ResourceLoader.getInstance().loadResource(ResourceLoader.ANT_DOWN), color));
    }

    private Image colorAntImage(Image i, Color c)
    {
        WritableImage res = new WritableImage((int) i.getWidth(), (int) i.getHeight());
        PixelReader reader = i.getPixelReader();
        for(int x = 0; x<i.getWidth(); x++)
        {
            for(int y = 0; y<i.getHeight(); y++)
            {
                double redColor = Math.round(reader.getColor(x, y).getRed() * 100000.0) / 100000.0;
                double redColorA1 = Math.round(84/255.0 * 100000.0) / 100000.0;
                double redColorA2 = Math.round(125/255.0 * 100000.0) / 100000.0;
                double redColorA3 = Math.round(161/255.0 * 100000.0) / 100000.0;
                if(redColor == redColorA1)
                {
                    res.getPixelWriter().setColor(x, y, c.darker());
                }
                else if(redColor == redColorA2)
                {
                    res.getPixelWriter().setColor(x, y, c);
                }
                else if(redColor == redColorA3)
                {
                    res.getPixelWriter().setColor(x, y, c.brighter());
                }
                else
                {
                    res.getPixelWriter().setColor(x, y, reader.getColor(x, y));
                }
            }
        }
        return res;
    }

    @Override
    public void draw(GraphicsContext context, Vector position)
    {
        double dotSize = MainPane.TILE_SIZE / (MAX_SIZE + 1 - size);
        Image i = antImages.get(headingDirection);
        Vector center = position.mult(MainPane.TILE_SIZE).add(MainPane.TILE_SIZE / 2 - dotSize / 2);
        context.drawImage(i, center.getX(), center.getY(), dotSize, dotSize);
    }

    public Color getColor() {
        return color;
    }



    @Override
    public void onSubscribe(Flow.Subscription subscription) {
        if(subscription instanceof AntSubscription asub)
        {
            this.sub = asub;
        }
    }

    @Override
    public void onNext(AntSignal item) {
        AntOrder order = item.getOrder(position);
        if(order != null)
        {
            sub.acknoledge(item);
            onOrderRecieved(order);
        }
    }

    @Override
    public void onError(Throwable throwable) {
        throwable.printStackTrace();
    }

    @Override
    public void onComplete() {
        this.sub = null;
    }
}
