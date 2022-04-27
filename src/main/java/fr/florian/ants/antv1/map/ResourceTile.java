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

import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class ResourceTile extends Tile {

    private List<Resource> resources;

    private java.util.Map<Resource, Vector> displayPositions;

    public ResourceTile(List<Resource> resources)
    {
        this.resources = resources;
        this.displayPositions = new HashMap<>();
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
        Color base = Color.SANDYBROWN;
        int maxInterpolateValue = this.getPheromoneLevel();
        if(maxInterpolateValue > 10) maxInterpolateValue = 10;
        base = base.interpolate(Color.RED, maxInterpolateValue/10.0);
        context.setFill(base);
        context.fillRect(position.getX()*MainPane.TILE_SIZE-1, position.getY()*MainPane.TILE_SIZE-1, MainPane.TILE_SIZE+2, MainPane.TILE_SIZE+2);
        double xMin = 0.3;
        double xMax = 0.7;
        double yMin = 0.3;
        double yMax = 0.7;
        for(int i = resources.size()-1; i>=resources.size()-6; i--)
        {
            if(i<0)
                break;
            Resource resource = resources.get(i);
            if(!displayPositions.containsKey(resource))
            {
                Vector pos = new Vector(new Random().nextDouble(xMin, xMax), new Random().nextDouble(yMin, yMax));
                displayPositions.put(resource, pos);
            }
            Vector pos = displayPositions.get(resource);
            resource.draw(context, position.mult(MainPane.TILE_SIZE).add(pos.mult(MainPane.TILE_SIZE)));
        }
    }
}
