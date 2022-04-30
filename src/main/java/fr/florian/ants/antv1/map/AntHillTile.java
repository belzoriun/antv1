package fr.florian.ants.antv1.map;

import fr.florian.ants.antv1.living.Living;
import fr.florian.ants.antv1.living.ant.Ant;
import fr.florian.ants.antv1.living.ant.QueenAnt;
import fr.florian.ants.antv1.living.ant.SoldierAnt;
import fr.florian.ants.antv1.living.ant.WorkerAnt;
import fr.florian.ants.antv1.ui.WorldView;
import fr.florian.ants.antv1.util.Vector;
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
        color = Color.rgb(new Random().nextInt(0, 160), new Random().nextInt(0, 160), new Random().nextInt(0, 160));
    }

    public void makeInitialSpawns(Vector pos)
    {
        QueenAnt q = new QueenAnt(uniqueId, color, pos);
        synchronized (Map.getInstance()) {
            Map.getInstance().spawn(q);
        }
        for(int i = 0; i<5; i++) {
            SoldierAnt s = new SoldierAnt(uniqueId, color, pos);
            synchronized (Map.getInstance()) {
                q.subscribe(s);
                Map.getInstance().spawn(s);
            }
            for(int j = 0; j<10; j++) {
                synchronized (Map.getInstance()) {
                    WorkerAnt w = new WorkerAnt(uniqueId, color, pos);
                    s.subscribe(w);
                    Map.getInstance().spawn(w);
                }
            }
        }
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
        if(l instanceof Ant a && a.getAntHillId() != this.uniqueId)
        {
            l.kill();
        }
        else if(l instanceof Ant a && a.getAntHillId() == this.uniqueId)
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
        context.setFill(color);
        context.fillRect(position.getX()* WorldView.TILE_SIZE, position.getY()* WorldView.TILE_SIZE, WorldView.TILE_SIZE, WorldView.TILE_SIZE);
    }
}
