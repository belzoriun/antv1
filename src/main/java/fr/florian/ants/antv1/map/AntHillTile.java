package fr.florian.ants.antv1.map;

import fr.florian.ants.antv1.living.Living;
import fr.florian.ants.antv1.living.ant.Ant;
import fr.florian.ants.antv1.living.ant.WorkerAnt;
import fr.florian.ants.antv1.ui.MainPane;
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
        for(int i = 0; i<50; i++)
            Map.getInstance().spawn(new WorkerAnt(uniqueId, color, pos));
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

    }

    @Override
    public void onInteract(Ant a) {
        if(a instanceof WorkerAnt want)
        {
            this.score += want.getResources().remove().getResourceScore();
        }
    }

    public int getScore()
    {
        return score;
    }

    @Override
    public void draw(GraphicsContext context, Vector position) {
        context.setFill(color);
        context.fillRect(position.getX()* MainPane.TILE_SIZE, position.getY()*MainPane.TILE_SIZE, MainPane.TILE_SIZE, MainPane.TILE_SIZE);
    }
}
