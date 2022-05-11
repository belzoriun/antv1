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
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Class representing a resource spot (can be empty)
 */
public class ResourceTile extends Tile {

    private final List<Resource> resources;

    private java.util.Map<Living, HBox> followBottons;
    private VBox livings;
    private VBox resourceList;
    private VBox detailNode;
    private Label totalResources;

    public ResourceTile(List<Resource> resources)
    {
        this.resources = resources;
        livings = new VBox();
        totalResources = new Label();
        resourceList = new VBox();
        detailNode = new VBox();
        ScrollPane scroll = new ScrollPane();
        scroll.setContent(livings);
        scroll.setMaxHeight(200);
        scroll.setPrefWidth(200);
        detailNode.getChildren().add(totalResources);
        detailNode.getChildren().add(resourceList);
        detailNode.getChildren().add(scroll);
        followBottons = new HashMap<>();
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
                } catch (Exception ignore) {
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
            totalResources.setText("Total resources : " + resources.size());
            resourceList.getChildren().clear();
            java.util.Map<Class<? extends Resource>, Integer> resourceMap = new HashMap<>();
            for(Resource r : resources)
            {
                if(resourceMap.containsKey(r.getClass()))
                {
                    resourceMap.put(r.getClass(), resourceMap.get(r.getClass())+1);
                }
                else
                {
                    resourceMap.put(r.getClass(), 1);
                }
            }
            for(java.util.Map.Entry<Class<? extends Resource>, Integer> entry : resourceMap.entrySet())
            {
                resourceList.getChildren().add(new Label(entry.getKey().getSimpleName()+" : "+entry.getValue()));
            }

            List<Living> livingList = Map.getInstance().getLivingsAt(Map.getInstance().getTilePosition(this));

            List<Node> trash = new ArrayList<>();
            List<Living> trashLiving = new ArrayList<>();
            for(java.util.Map.Entry<Living, HBox> entry : followBottons.entrySet()) {
                if (!livingList.contains(entry.getKey())) {
                    trash.add(entry.getValue());
                    trashLiving.add(entry.getKey());
                    break;
                }
            }

            for (Living liv : livingList) {
                for(java.util.Map.Entry<Living, HBox> entry : followBottons.entrySet()) {
                    if (entry.getKey().equals(liv)) {
                        if (!liv.isAlive()) {
                            trash.add(entry.getValue());
                            trashLiving.add(entry.getKey());
                        }
                        else {
                            ((Label) entry.getValue().getChildren().get(0)).setText(liv.getClass().getSimpleName() + " " + liv.getPosition());
                        }
                        break;
                    }
                }
                if (!followBottons.containsKey(liv)) {
                    HBox box = new HBox();
                    Label l = new Label(liv.getClass().getSimpleName() + " " + liv.getPosition());
                    box.getChildren().add(l);
                    Button b = new Button("Follow");
                    b.setOnAction(e -> {
                        WorldView.follow(liv);
                    });
                    box.getChildren().add(b);
                    livings.getChildren().add(box);
                    followBottons.put(liv, box);
                }
            }
            for(Node n:trash) {
                livings.getChildren().remove(n);
            }
            for(Living n:trashLiving) {
                followBottons.remove(n);
            }

            return detailNode;
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

    public void placeResource(Resource remove) {
        resources.add(remove);
    }
}
