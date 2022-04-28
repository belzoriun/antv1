package fr.florian.ants.antv1.living.ant;

import fr.florian.ants.antv1.living.Living;
import fr.florian.ants.antv1.map.Map;
import fr.florian.ants.antv1.map.Tile;
import fr.florian.ants.antv1.ui.MainPane;
import fr.florian.ants.antv1.util.Direction;
import fr.florian.ants.antv1.util.FightManager;
import fr.florian.ants.antv1.util.ResourceLoader;
import fr.florian.ants.antv1.util.Vector;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.scene.transform.Rotate;

import java.util.HashMap;
import java.util.Random;

public abstract class Ant extends Living{

    private static final int MAX_SIZE = 10;

    private double size;
    protected long uniqueAnthillId;
    private boolean weak;
    private final int strenght;

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
        synchronized (t) {
            t.onAntDieOn(this);
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
}
