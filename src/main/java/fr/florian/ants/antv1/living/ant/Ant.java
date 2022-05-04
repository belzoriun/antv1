package fr.florian.ants.antv1.living.ant;

import fr.florian.ants.antv1.living.Living;
import fr.florian.ants.antv1.map.AntHillTile;
import fr.florian.ants.antv1.map.Map;
import fr.florian.ants.antv1.map.Tile;
import fr.florian.ants.antv1.ui.Application;
import fr.florian.ants.antv1.ui.WorldView;
import fr.florian.ants.antv1.util.*;
import fr.florian.ants.antv1.util.signals.AntSignal;
import fr.florian.ants.antv1.util.signals.AntSignalReciever;
import fr.florian.ants.antv1.util.signals.AntSubscription;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.Flow;

public abstract class Ant extends Living implements AntSignalReciever {

    public static final int MAX_SIZE = 10;
    private static long CURRENT_ANT_ID = 0L;

    private double size;
    protected long uniqueAnthillId;
    private boolean weak;
    private final int strenght;
    private long antId;
    private AntSubscription sub;

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
            t.onAntDieOn(this);
            Map.getInstance().addDeadAnt(new DeadAnt(position, color, size));
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
                    if(isWeak())
                    {
                        float rand = Application.random.nextFloat();
                        if (rand > 0.5) {
                            a.kill();
                        } else {
                            kill();
                        }
                    }
                    else {
                        float rand = Application.random.nextFloat();
                        if (rand > 0.5) {
                            a.weaken();
                        } else {
                            weaken();
                        }
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
        this.antId = CURRENT_ANT_ID++;
    }

    @Override
    public void draw(GraphicsContext context, Vector position)
    {
        if(Map.getInstance().getTile(this.position) instanceof AntHillTile ah && ah.getUniqueId() == uniqueAnthillId)
        {
            return;
        }
        double dotSize = WorldView.TILE_SIZE / (MAX_SIZE + 1 - size);
        Image i = ResourceLoader.getInstance().loadResource("ant"+color.getRed()+":"+color.getGreen()+":"+color.getBlue());
        Vector center = position.add(WorldView.TILE_SIZE / 2-dotSize/2);
        double rotation = 0;
        if(headingDirection != null) {
            switch (headingDirection) {
                case LEFT:
                    rotation = 90;
                    break;
                case RIGHT:
                    rotation = -90;
                    break;
                case DOWN:
                    rotation = 180;
                    break;
                case UP:
                default:
                    rotation = 0;
                    break;
            }
        }
        WorldView.drawRotatedImage(context, i, center, rotation, dotSize);
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
