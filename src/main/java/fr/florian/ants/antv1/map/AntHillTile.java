package fr.florian.ants.antv1.map;

import fr.florian.ants.antv1.living.Living;
import fr.florian.ants.antv1.living.ant.Ant;
import fr.florian.ants.antv1.living.ant.QueenAnt;
import fr.florian.ants.antv1.living.ant.SoldierAnt;
import fr.florian.ants.antv1.living.ant.WorkerAnt;
import fr.florian.ants.antv1.ui.Application;
import fr.florian.ants.antv1.ui.WorldView;
import fr.florian.ants.antv1.util.ImageColorer;
import fr.florian.ants.antv1.util.ResourceLoader;
import fr.florian.ants.antv1.util.Vector;
import fr.florian.ants.antv1.util.option.OptionKey;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.Random;

public class AntHillTile extends Tile{

    private static long currentId = 0L;

    private long uniqueId;
    private Color color;

    private int score;

    public AntHillTile()
    {
        this.uniqueId = currentId;
        currentId+= 1L;
        score = 0;
        color = Color.rgb(Application.random.nextInt(0, 160), Application.random.nextInt(0, 160), Application.random.nextInt(0, 160));
        ResourceLoader.getInstance().saveResource("ant"+color.getRed()+":"+color.getGreen()+":"+color.getBlue()
                , ImageColorer.colorAntImage(ResourceLoader.getInstance().loadResource(ResourceLoader.ANT), color));
        ResourceLoader.getInstance().saveResource("anthill"+color.getRed()+":"+color.getGreen()+":"+color.getBlue()
                , ImageColorer.colorAntImage(ResourceLoader.getInstance().loadResource(ResourceLoader.ANTHILL), color));
    }

    public void makeInitialSpawns(Vector pos)
    {
        QueenAnt q = new QueenAnt(uniqueId, color, pos);
        synchronized (Map.getInstance()) {
            Map.getInstance().spawn(q);
        }
        System.out.println("spawned queen");
        for (int i = 0; i < Application.options.getInt(OptionKey.SOLDIER_PER_QUEEN); i++) {
            SoldierAnt s = new SoldierAnt(uniqueId, color, pos);
            synchronized (Map.getInstance()) {
                q.subscribe(s);
                Map.getInstance().spawn(s);
            }
            for (int j = 0; j < Application.options.getInt(OptionKey.WORKER_PER_SOLDIER); j++) {
                synchronized (Map.getInstance()) {
                    WorkerAnt w = new WorkerAnt(uniqueId, color, pos);
                    s.subscribe(w);
                    Map.getInstance().spawn(w);
                }
            }
        }
        System.out.println("spawned "+Application.options.getInt(OptionKey.WORKER_PER_SOLDIER)*Application.options.getInt(OptionKey.SOLDIER_PER_QUEEN)+" workers");
    }

    public final long getUniqueId()
    {
        return uniqueId;
    }

    public final Color getColor()
    {
        return color;
    }

    @Override
    public void onWalkOn(Living l) {
        if(l instanceof Ant a && a.getAntHillId() == this.uniqueId)
        {
            a.heal();
        }
    }

    @Override
    public void onInteract(Ant a) {
        if(a instanceof WorkerAnt want)
        {
            this.score += want.getResources().remove().getResourceScore();
        }
    }

    @Override
    public void onAntDieOn(Ant a) {
        if(a instanceof WorkerAnt want)
        {
            while(!want.getResources().isEmpty()) {
                this.score += want.getResources().remove().getResourceScore();
            }
        }
    }

    public int getScore()
    {
        return score;
    }

    @Override
    public void draw(GraphicsContext context, Vector position) {
        context.drawImage(ResourceLoader.getInstance().loadResource(ResourceLoader.GRASS_RES_1)
                , position.getX()
                , position.getY()
                , WorldView.TILE_SIZE
                , WorldView.TILE_SIZE);
        Vector hpos = position.add(new Vector(0, -WorldView.TILE_SIZE/2));
        WorldView.drawRotatedImage(context,
                ResourceLoader.getInstance().loadResource("anthill"+color.getRed()+":"+color.getGreen()+":"+color.getBlue()),
                hpos, 0, WorldView.TILE_SIZE);
    }
}
