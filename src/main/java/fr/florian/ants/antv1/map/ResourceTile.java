package fr.florian.ants.antv1.map;

import fr.florian.ants.antv1.living.Living;
import fr.florian.ants.antv1.living.ant.Ant;
import fr.florian.ants.antv1.living.ant.WorkerAnt;
import fr.florian.ants.antv1.ui.WorldView;
import fr.florian.ants.antv1.util.ResourceLoader;
import fr.florian.ants.antv1.util.Vector;
import fr.florian.ants.antv1.util.resource.Resource;
import javafx.scene.canvas.GraphicsContext;

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
        if(resources.isEmpty()) {
            context.drawImage(ResourceLoader.getInstance().loadResource(ResourceLoader.GRASS_RES_1)
                    , position.getX() * WorldView.TILE_SIZE
                    , position.getY() * WorldView.TILE_SIZE
                    , WorldView.TILE_SIZE
                    , WorldView.TILE_SIZE);
        }else
        {
            context.drawImage(ResourceLoader.getInstance().loadResource(ResourceLoader.GRASS_RES_2)
                    , position.getX() * WorldView.TILE_SIZE
                    , position.getY() * WorldView.TILE_SIZE
                    , WorldView.TILE_SIZE
                    , WorldView.TILE_SIZE);
        }
    }
}
