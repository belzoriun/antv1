package fr.florian.ants.antv1.living.ant;

import fr.florian.ants.antv1.map.AntHillTile;
import fr.florian.ants.antv1.map.Map;
import fr.florian.ants.antv1.map.ResourceTile;
import fr.florian.ants.antv1.map.Tile;
import fr.florian.ants.antv1.ui.Application;
import fr.florian.ants.antv1.util.AntOrder;
import fr.florian.ants.antv1.util.Direction;
import fr.florian.ants.antv1.util.HeldResourceList;
import fr.florian.ants.antv1.util.Vector;
import fr.florian.ants.antv1.util.pheromone.FoodSourcePheromone;
import fr.florian.ants.antv1.util.resource.DeadAnt;
import fr.florian.ants.antv1.util.resource.Resource;
import fr.florian.ants.antv1.util.statemachine.StateMachine;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;


/**
 * Class representing a worker ant
 */
public class WorkerAnt extends Ant {

    private final HeldResourceList heldResources;
    private List<Vector> path;
    private final List<Vector> deadCells;
    private boolean pathCleared;
    private final SoldierAnt soldier;

    public WorkerAnt(long anthillId, SoldierAnt soldier, Color color, Vector initialPosition) {
        super(anthillId, color, initialPosition, 8, 1);
        heldResources = new HeldResourceList(5);
        path = new ArrayList<>();
        path.add(initialPosition);
        deadCells = new ArrayList<>();
        soldier.subscribe(this);
        this.soldier = soldier;

        initCore(new StateMachine.StateMachineBuilder()
            .addState("food", ()->{
                pathCleared = false;
                Tile t = Map.getInstance().getTile(position);
                if (t != null) {
                    if (t instanceof ResourceTile res) {
                        if (res.resourceCount() > 0) {
                            takeResource(res);
                            return;
                        }
                        else
                        {
                            if(!heldResources.getAll().stream().filter(r->r instanceof DeadAnt).toList().isEmpty())
                            {
                                stateMachine.setTransition("fullofresources");
                            }
                        }
                    }
                    Direction[] dirs = Direction.values();
                    List<Direction> selection = new ArrayList<>();
                    int pheromoneLvl = 0;
                    for (Direction dir : dirs) {
                        Vector pos = this.position.add(dir.getOffset());
                        Tile next = Map.getInstance().getTile(pos);
                        if (next != null && !path.contains(pos) && !deadCells.contains(pos)) {
                            if(next instanceof AntHillTile a && a.getUniqueId() != uniqueAnthillId)
                            {
                                continue;
                            }
                            if(next instanceof ResourceTile rt && !rt.getResources().isEmpty())
                            {
                                selection.clear();
                                selection.add(dir);
                                break;
                            }
                            if (next.getPheromoneLevel(uniqueAnthillId, FoodSourcePheromone.class) > pheromoneLvl) {
                                selection = new ArrayList<>();
                                pheromoneLvl = next.getPheromoneLevel(uniqueAnthillId);
                            }
                            if (next.getPheromoneLevel(uniqueAnthillId, FoodSourcePheromone.class) == pheromoneLvl) {
                                selection.add(dir);
                            }
                        }
                    }
                    if (selection.isEmpty()) {
                        deadCells.add(position);
                        path.remove(path.size() - 1);
                        if(!path.isEmpty())
                        {
                            Vector newPos = path.get(path.size()-1);
                            headingDirection = Direction.fromOffset(position.add(newPos.multi(-1)));
                            synchronized (Map.getInstance().getTile(position)) {
                                position = newPos;
                            }
                        }
                    } else {
                        Direction dir = selection.get(Application.random.nextInt(0, selection.size()));
                        headingDirection = dir;
                        synchronized (Map.getInstance().getTile(position)) {
                            position = position.add(dir.getOffset());
                        }
                        path.add(position);
                    }
                }
            })
            .addState("stayincolony", ()->{

                Tile t = Map.getInstance().getTile(position);
                if (t != null) {
                    if (t instanceof AntHillTile antHill && antHill.getUniqueId() == this.uniqueAnthillId) {
                        if(!heldResources.isEmpty()){
                            antHill.onInteract(this);
                        }
                    } else {
                        if (path.isEmpty()) {
                            if (t instanceof ResourceTile rt) {
                                while (!getResources().isEmpty()) {
                                    rt.getResources().add(getResources().remove());
                                }
                            }
                        } else {
                            headingDirection = Direction.fromOffset(path.get(path.size() - 1).add(position.multi(-1)));
                            path.remove(path.size() - 1);
                            if(!path.isEmpty()) {
                                Vector newPos = path.get(path.size() - 1);
                                synchronized (Map.getInstance().getTile(position)) {
                                    t.placePheromone(uniqueAnthillId, new FoodSourcePheromone(this.getColor()));
                                    this.position = newPos;
                                }
                            }
                        }
                    }
                }
            })
            .addState("tocolony", ()->{
                if(!pathCleared) {
                    pathCleared = true;
                    clearPath(path);
                }
                Tile t = Map.getInstance().getTile(position);
                if (t != null) {
                    if (t instanceof AntHillTile antHill && antHill.getUniqueId() == this.uniqueAnthillId) {
                        if(heldResources.isEmpty())
                        {
                            this.path.clear();
                            this.path.add(position);
                            this.deadCells.clear();
                            stateMachine.setTransition("backtofood");
                        }
                        else {
                            antHill.onInteract(this);
                        }
                    } else {
                        path.remove(path.size() - 1);
                        if (path.isEmpty()) {
                            if (t instanceof ResourceTile rt) {
                                while (!getResources().isEmpty()) {
                                    rt.placeResource(getResources().remove());
                                }
                            }
                            stateMachine.setTransition("backToFood");
                        } else {
                            headingDirection = Direction.fromOffset(path.get(path.size() - 1).add(position.multi(-1)));
                            Vector newPos =path.get(path.size() - 1) ;
                            synchronized (Map.getInstance().getTile(position)) {
                                t.placePheromone(uniqueAnthillId, new FoodSourcePheromone(this.getColor()));
                                this.position = newPos;
                            }
                        }
                    }
                }
            })
            .addTransition("fullofresources")
            .addTransition("backtofood")
            .addTransition("stayincolony")
            .addStateLink("food", "tocolony", "fullofresources")
            .addStateLink("tocolony", "food", "backtofood")
            .addStateLink("tocolony", "stayincolony", "stayincolony")
            .addStateLink("food", "stayincolony", "stayincolony")
            .addStateLink("stayincolony", "food", "backtofood")
            .get("food"));
    }

    public final SoldierAnt getSoldier()
    {
        return this.soldier;
    }

    public List<Vector> getPath()
    {
        return new ArrayList<>(path);
    }

    /**
     * Remove loops inside path
     * @param in The path to clean
     */
    private void clearPath(List<Vector> in)
    {
        int i = in.size()-1;
        while(i >= 1)
        {
            List<Vector> trash = new ArrayList<>();
            List<List<Vector>> tests = new ArrayList<>();
            for(int j = i-1; j>=0; j--)
            {
                if((in.get(j).left().equals(in.get(i)) ||
                        in.get(j).right().equals(in.get(i)) ||
                        in.get(j).up().equals(in.get(i)) ||
                        in.get(j).down().equals(in.get(i))) && j+1 != i)
                {
                    List<Vector> trashTest = new ArrayList<>();
                    for(int s = j+1; s<=i-1; s++)
                    {
                        trashTest.add(in.get(s));
                    }
                    if(!trashTest.isEmpty()) {
                        tests.add(trashTest);
                    }
                }
            }
            for(List<Vector> test : tests)
            {
                if(test.size() > trash.size())
                {
                    trash = test;
                }
            }
            if(!trash.isEmpty())
            {
                in.removeAll(trash);
            }
            else
            {
                i--;
            }
            if(i > in.size()-2)
            {
                i = in.size()-1;
            }
        }
    }

    @Override
    protected void executeAction() {
        if(this.heldResources.isFull() || this.isWeak())
        {
            stateMachine.setTransition("fullofresources");
        }
        stateMachine.step();
    }

    @Override
    protected void onOrderReceived(AntOrder order) {
        if(order == AntOrder.SEARCH_FOR_FOOD)
        {
            stateMachine.setTransition("backtofood");
        }else if(order==AntOrder.BACK_TO_COLONY)
        {
            stateMachine.setTransition("stayincolony");
        }
    }

    private void takeResource(ResourceTile tile) {
        if (!heldResources.isFull()) {
            tile.onInteract(this);
        }
    }

    public HeldResourceList getResources() {
        return this.heldResources;
    }

    @Override
    public Node getDetailDisplay() {
        VBox box = new VBox();
        box.getChildren().add(new Label("Held resource amount : "+heldResources.size()));
        box.getChildren().add(new ImageView(stateMachine.getAsImage()));
        return box;
    }
}
