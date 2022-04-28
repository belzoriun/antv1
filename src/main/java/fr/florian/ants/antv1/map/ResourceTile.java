package fr.florian.ants.antv1.map;

import fr.florian.ants.antv1.living.Living;
import fr.florian.ants.antv1.living.ant.Ant;
import fr.florian.ants.antv1.living.ant.WorkerAnt;
import fr.florian.ants.antv1.ui.MainPane;
import fr.florian.ants.antv1.util.ResourceLoader;
import fr.florian.ants.antv1.util.Vector;
import fr.florian.ants.antv1.util.resource.Resource;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

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
    public void onAntDieOn(Ant a) {
        if(a instanceof WorkerAnt w)
        {
            this.resources.addAll(w.getResources().getAll());
        }
    }

    @Override
    public void draw(GraphicsContext context, Vector position) {
        int maxInterpolateValue = this.getPheromoneLevel();
        if(maxInterpolateValue > 10) maxInterpolateValue = 10;
        Color base = new Color(49/255, 60/255, 100/255, 0.6).interpolate(new Color(1, 0, 0, 0.6), maxInterpolateValue/10.0);
        if(resources.isEmpty()) {
            context.drawImage(ResourceLoader.getInstance().loadResource(ResourceLoader.GRASS_RES_1)
                    , position.getX() * MainPane.TILE_SIZE
                    , position.getY() * MainPane.TILE_SIZE
                    , MainPane.TILE_SIZE
                    , MainPane.TILE_SIZE);
        }else
        {
            context.drawImage(ResourceLoader.getInstance().loadResource(ResourceLoader.GRASS_RES_2)
                    , position.getX() * MainPane.TILE_SIZE
                    , position.getY() * MainPane.TILE_SIZE
                    , MainPane.TILE_SIZE
                    , MainPane.TILE_SIZE);
        }
        if(getPheromoneLevel() >= 1) {
            context.setFill(base);
            context.fillRect(position.getX() * MainPane.TILE_SIZE, position.getY() * MainPane.TILE_SIZE, MainPane.TILE_SIZE, MainPane.TILE_SIZE);
        }
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
