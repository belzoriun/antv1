package fr.florian.ants.antv1.map;

import fr.florian.ants.antv1.living.Living;
import fr.florian.ants.antv1.living.ant.Ant;
import fr.florian.ants.antv1.living.ant.WorkerAnt;
import fr.florian.ants.antv1.ui.MainPane;
import fr.florian.ants.antv1.util.Vector;
import fr.florian.ants.antv1.util.resource.Resource;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.Shape;
import javafx.scene.text.Font;

import java.util.List;

public class ResourceTile implements Tile {

    private List<Resource> resources;

    public ResourceTile(List<Resource> resources)
    {
        this.resources = resources;
    }

    public List<Resource> getResources()
    {
        return resources;
    }

    public int resourceCount()
    {
        return resources.size();
    }

    public Resource take()
    {
        if(resources.isEmpty())
        {
            return null;
        }
        return resources.remove(0);
    }

    @Override
    public void onWalkOn(Living l) {

    }

    @Override
    public void onInteract(Ant a) {
        if(a instanceof WorkerAnt worker)
        {
            //give worker a resource
        }
    }

    @Override
    public void draw(GraphicsContext context, Vector position) {
        context.setFill(Color.SANDYBROWN);
        context.fillRect(position.getX()*MainPane.TILE_SIZE, position.getY()*MainPane.TILE_SIZE, MainPane.TILE_SIZE, MainPane.TILE_SIZE);
        Vector center = new Vector(position.getX()*MainPane.TILE_SIZE+MainPane.TILE_SIZE/2, position.getY()*MainPane.TILE_SIZE+MainPane.TILE_SIZE/2);
        context.setFill(Color.GREENYELLOW);
        int dotCloseness = 6;
        if(resources.size() >= 5)
        {
            context.fillOval(center.getX()+MainPane.TILE_SIZE/dotCloseness, center.getY()+MainPane.TILE_SIZE/dotCloseness, MainPane.TILE_SIZE/4, MainPane.TILE_SIZE/4);
        }
        if(resources.size() >= 4)
        {
            context.fillOval(center.getX()+MainPane.TILE_SIZE/dotCloseness, center.getY()-MainPane.TILE_SIZE/dotCloseness, MainPane.TILE_SIZE/4, MainPane.TILE_SIZE/4);
        }
        if(resources.size() >= 3)
        {
            context.fillOval(center.getX()-MainPane.TILE_SIZE/dotCloseness, center.getY()+MainPane.TILE_SIZE/dotCloseness, MainPane.TILE_SIZE/4, MainPane.TILE_SIZE/4);
        }
        if(resources.size() >= 2)
        {
            context.fillOval(center.getX()-MainPane.TILE_SIZE/dotCloseness, center.getY()-MainPane.TILE_SIZE/dotCloseness, MainPane.TILE_SIZE/4, MainPane.TILE_SIZE/4);
        }
        if(resources.size() >= 1)
        {
            context.fillOval(center.getX(), center.getY(), MainPane.TILE_SIZE/4, MainPane.TILE_SIZE/4);
        }
    }
}
