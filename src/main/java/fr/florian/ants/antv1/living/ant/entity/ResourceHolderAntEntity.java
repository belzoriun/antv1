package fr.florian.ants.antv1.living.ant.entity;

import fr.florian.ants.antv1.living.Living;
import fr.florian.ants.antv1.living.LivingEntity;
import fr.florian.ants.antv1.living.ant.Ant;
import fr.florian.ants.antv1.living.ant.WorkerAnt;
import fr.florian.ants.antv1.map.AntHillTile;
import fr.florian.ants.antv1.map.Map;
import fr.florian.ants.antv1.map.ResourceHoldTile;
import fr.florian.ants.antv1.map.Tile;
import fr.florian.ants.antv1.ui.Application;
import fr.florian.ants.antv1.util.Direction;
import fr.florian.ants.antv1.util.HeldResourceList;
import fr.florian.ants.antv1.util.Vector;
import fr.florian.ants.antv1.util.pheromone.FoodSourcePheromone;
import fr.florian.ants.antv1.util.resource.DeadAnt;
import fr.florian.ants.antv1.util.statemachine.StateMachine;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ResourceHolderAntEntity extends AntEntity{

    private final HeldResourceList heldResources;
    private List<Vector> path;
    private final List<Vector> deadCells;
    private boolean pathCleared;
    private String nextStep;

    public ResourceHolderAntEntity(String initialState, Vector initialPosition, Ant ant)
    {
        super(initialState, initialPosition, ant);
        nextStep = "";
        heldResources = new HeldResourceList(5);
        path = new ArrayList<>();
        path.add(initialPosition);
        deadCells = new ArrayList<>();
    }

    public void takeResource(ResourceHoldTile tile) {
        if (!heldResources.isFull()) {
            try {
                heldResources.add(tile.take());
            } catch (Exception ignored) {
            }
        }
    }

    public List<Vector> getPath()
    {
        return new ArrayList<>(path);
    }

    public Vector removeFromPath()
    {
        return path.remove(path.size()-1);
    }

    public void addToPath(Vector v)
    {
        path.add(v);
    }

    /**
     * Remove loops inside path
     * @param in The path to clean
     */
    public void clearPath()
    {
        if(!pathCleared) {
            pathCleared = true;
            List<Vector> in = path;
            int i = in.size() - 2;
            while (i >= 2) {
                List<Vector> trash = new ArrayList<>();
                List<List<Vector>> tests = new ArrayList<>();
                for (int j = i - 1; j >= 0; j--) {
                    if ((in.get(j).left().equals(in.get(i)) ||
                            in.get(j).right().equals(in.get(i)) ||
                            in.get(j).up().equals(in.get(i)) ||
                            in.get(j).down().equals(in.get(i))) && j + 1 != i) {
                        List<Vector> trashTest = new ArrayList<>();
                        for (int s = j + 1; s <= i - 1; s++) {
                            trashTest.add(in.get(s));
                        }
                        if (!trashTest.isEmpty()) {
                            tests.add(trashTest);
                        }
                    }
                }
                for (List<Vector> test : tests) {
                    if (test.size() > trash.size()) {
                        trash = test;
                    }
                }
                if (!trash.isEmpty()) {
                    for (Vector rem : trash) {
                        in.remove(rem);
                    }
                } else {
                    i--;
                }
                if (i > in.size() - 2) {
                    i = in.size() - 2;
                }
            }
            path = in;
        }
    }

    @Override
    public Node getDetailDisplay() {
        VBox box = new VBox();
        box.getChildren().add(new Label("Held resource amount : "+heldResources.size()));
        box.getChildren().add(new Label("Life : "+Math.round(lifePoints/ant.getMaxLifePoints()*100)+"%"));
        box.getChildren().add(new Label("On ant hill : "+(Map.getInstance().getTile(position) instanceof AntHillTile ah && ah.getUniqueId() == getAntHillId())));
        box.getChildren().add(new Label("Next step : "+nextStep));
        return box;
    }

    public void resetPath() {
        path.clear();
        this.path.add(position);
    }

    public HeldResourceList getHeldResources() {
        return heldResources;
    }

    public void dirtyPath()
    {
        pathCleared = false;
    }

    public List<Vector> getDeadCells() {
        return deadCells;
    }
}
