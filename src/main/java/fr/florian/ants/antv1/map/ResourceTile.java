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
    private VBox detailNode;
    private java.util.Map<Living, Button> followBottons;

    public ResourceTile(List<Resource> resources)
    {
        this.resources = resources;
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
            for(java.util.Map.Entry<Class<? extends Resource>, Integer> entry : res.entrySet())
            {
                if(entry.getValue() > 0) {
                    Label l = new Label(entry.getKey().getSimpleName() + " : " + entry.getValue());
                    detail.getChildren().add(l);
                }
            }
            if(detailNode == null) {

                detailNode=new VBox();
                VBox antsPane = new VBox();
                List<Living> ants = Map.getInstance().getLivingsAt(Map.getInstance().getTilePosition(this));
                for (Living ant : ants) {
                    HBox box = new HBox();
                    Label l = new Label(ant.getClass().getSimpleName() + " " + ant.getPosition());
                    box.getChildren().add(l);
                    if (!followBottons.containsKey(ant)) {
                        Button b = new Button("Follow");
                        b.setOnAction(e -> {
                            WorldView.follow(ant);
                        });
                        followBottons.put(ant, b);
                    }
                    box.getChildren().add(followBottons.get(ant));
                    antsPane.getChildren().add(box);
                }

                detailNode.getChildren().add(totalResource);
                detailNode.getChildren().add(detail);
                ScrollPane antScroll = new ScrollPane();
                antScroll.setContent(antsPane);
                antScroll.setMaxHeight(200);
                detailNode.getChildren().add(antScroll);
            }
            else
            {
                detailNode.getChildren().get(2).setVisible(true);
                detailNode.getChildren().set(0, totalResource);
                detailNode.getChildren().set(1, detail);
                VBox pane = (VBox) ((ScrollPane)detailNode.getChildren().get(2)).getContent();

                List<Living> ants = Map.getInstance().getLivingsAt(Map.getInstance().getTilePosition(this));
                if(ants.isEmpty())
                {
                    pane.getChildren().clear();
                    detailNode.getChildren().get(2).setVisible(false);
                }
                for (Living ant : ants) {
                    boolean found = false;
                    List<Node> trash = new ArrayList<>();
                    for(Node node : pane.getChildren())
                    {
                        HBox box = (HBox)node;
                        for(java.util.Map.Entry<Living, Button> entry : followBottons.entrySet()) {
                            if (entry.getValue().equals(box.getChildren().get(1))) {
                                found = true;
                                if (!ant.isAlive()) {
                                    trash.add(node);
                                } else {
                                    ((Label) box.getChildren().get(0)).setText(ant.getClass().getSimpleName() + " " + ant.getPosition());
                                }
                                break;
                            }
                        }
                    }
                    if(!found)
                    {
                        HBox box = new HBox();
                        Label l = new Label(ant.getClass().getSimpleName() + " " + ant.getPosition());
                        box.getChildren().add(l);
                        if (!followBottons.containsKey(ant)) {
                            Button b = new Button("Follow");
                            b.setOnAction(e -> {
                                WorldView.follow(ant);
                            });
                            followBottons.put(ant, b);
                        }
                        box.getChildren().add(followBottons.get(ant));
                        pane.getChildren().add(box);
                    }
                    for(Node n:trash)
                    {
                        pane.getChildren().remove(n);
                    }
                }

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
}
