package fr.florian.ants.antv1.map;

import fr.florian.ants.antv1.living.Living;
import fr.florian.ants.antv1.living.ant.Ant;
import fr.florian.ants.antv1.util.resource.DeadAnt;
import fr.florian.ants.antv1.living.ant.WorkerAnt;
import fr.florian.ants.antv1.ui.WorldView;
import fr.florian.ants.antv1.util.ResourceLoader;
import fr.florian.ants.antv1.util.Vector;
import fr.florian.ants.antv1.util.resource.Resource;
import javafx.scene.Node;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class representing a resource spot (can be empty)
 */
public class ResourceTile extends Tile {

    private final List<Resource> resources;

    public ResourceTile(List<Resource> resources)
    {
        this.resources = resources;
    }

    public List<Resource> getResources()
    {
        return new ArrayList<>(resources);
    }

    public int resourceCount()
    {
        synchronized (resources) {
            return resources.size();
        }
    }

    /**
     * Take a resource from the tile, removing it
     * @return A resource
     */
    public Resource take()
    {
        synchronized (resources) {
            if (resources.isEmpty()) {
                return null;
            }
            return resources.remove(0);
        }
    }

    @Override
    public void onWalkOn(Living l) {
        //does nothing
    }

    @Override
    public void onInteract(Ant a) {
        if(a instanceof WorkerAnt worker) {
            Resource r = take();
            if (r != null) {
                try {
                    worker.getResources().add(r);
                } catch (Exception e) {
                    System.err.println(e.getMessage());
                }
            }
        }
    }

    @Override
    public void onAntDieOn(Ant a) {
        synchronized (resources) {
            if (a instanceof WorkerAnt w) {
                this.resources.addAll(w.getResources().getAll());
            }
            this.resources.add(new DeadAnt(a));
        }
    }

    @Override
    public Node getInfoDisplay() {
        synchronized (resources) {
            Label totalResource = new Label("Total resources : " + resources.size());
            VBox detail = new VBox();
            java.util.Map<Class<? extends Resource>, Integer> res = new HashMap<>();
            for (Resource r : resources) {
                if (res.containsKey(r.getClass())) {
                    res.put(r.getClass(), res.get(r.getClass()) + 1);
                } else {
                    res.put(r.getClass(), 1);
                }
            }
            for(Map.Entry<Class<? extends Resource>, Integer> entry : res.entrySet())
            {
                if(entry.getValue() > 0) {
                    Label l = new Label(entry.getKey().getSimpleName() + " : " + entry.getValue());
                    detail.getChildren().add(l);
                }
            }
            VBox node = new VBox();
            node.getChildren().add(totalResource);
            node.getChildren().add(detail);
            return node;
        }
    }

    @Override
    public void draw(GraphicsContext context, Vector position) {
        context.drawImage(ResourceLoader.getInstance().loadResource(ResourceLoader.GRASS_RES_1)
                , position.getX()
                , position.getY()
                , WorldView.TILE_SIZE
                , WorldView.TILE_SIZE);
    }
}
