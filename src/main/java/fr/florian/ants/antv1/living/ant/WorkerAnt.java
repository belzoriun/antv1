package fr.florian.ants.antv1.living.ant;

import fr.florian.ants.antv1.map.AntHillTile;
import fr.florian.ants.antv1.map.Map;
import fr.florian.ants.antv1.map.ResourceTile;
import fr.florian.ants.antv1.map.Tile;
import fr.florian.ants.antv1.util.AntOrder;
import fr.florian.ants.antv1.util.Direction;
import fr.florian.ants.antv1.util.HoldedResourceList;
import fr.florian.ants.antv1.util.Vector;
import fr.florian.ants.antv1.util.pheromone.FoodSourcePheromone;
import fr.florian.ants.antv1.util.resource.Resource;
import fr.florian.ants.antv1.util.stagemachine.StateMachine;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class WorkerAnt extends Ant {

    private final HoldedResourceList holdedResources;
    private List<Vector> path;
    private List<Vector> deadCells;
    private StateMachine stageMachine;

    private boolean backToColony;

    public WorkerAnt(long anthillId, Color color, Vector ipos) {
        super(anthillId, color, ipos, 8, 1);
        holdedResources = new HoldedResourceList(5);
        path = new ArrayList<>();
        path.add(ipos);
        deadCells = new ArrayList<>();
        backToColony = false;

        stageMachine = new StateMachine.StateMachineBuilder()
            .addState("food", ()->{
                Tile t = Map.getInstance().getTile(position);
                if (t != null) {
                    synchronized (t) {
                        if (t instanceof ResourceTile res) {
                            if (res.resourceCount() > 0) {
                                try {
                                    takeResource(res);
                                } catch (Exception e) {
                                }
                                return;
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
                            synchronized (next) {
                                if (next.getPheromoneLevel(uniqueAnthillId, FoodSourcePheromone.class) > pheromoneLvl) {
                                    selection = new ArrayList<>();
                                    pheromoneLvl = next.getPheromoneLevel(uniqueAnthillId);
                                }
                                if (next.getPheromoneLevel(uniqueAnthillId, FoodSourcePheromone.class) == pheromoneLvl) {
                                    selection.add(dir);
                                }
                            }
                        }
                    }
                    if (selection.isEmpty()) {
                        deadCells.add(position);
                        Vector newPos = path.remove(path.size() - 1);
                        headingDirection = Direction.fromOffset(position.add(newPos.mult(-1)));
                        position = newPos;
                    } else {
                        Direction dir = selection.get(new Random().nextInt(0, selection.size()));
                        headingDirection = dir;
                        position = position.add(dir.getOffset());
                        path.add(position);
                    }
                }
            })
            .addState("stayincolony", ()->{

                Tile t = Map.getInstance().getTile(position);
                if (t != null) {
                    synchronized (t) {
                        t.placePheromone(uniqueAnthillId, new FoodSourcePheromone(this.getColor()));
                    }
                    if (t instanceof AntHillTile anth && anth.getUniqueId() == this.uniqueAnthillId) {
                        if(!holdedResources.isEmpty()){
                            anth.onInteract(this);
                        }
                    } else {
                        if (path.isEmpty()) {
                            if (t instanceof ResourceTile rt) {
                                while (!getResources().isEmpty()) {
                                    rt.getResources().add(getResources().remove());
                                }
                            }
                        } else {
                            Direction h = Direction.fromOffset(path.get(path.size() - 1).add(position.mult(-1)));
                            headingDirection = h;
                            this.position = path.remove(path.size() - 1);
                        }
                    }
                }
            })
            .addState("tocolony", ()->{
                Tile t = Map.getInstance().getTile(position);
                if (t != null) {
                    synchronized (t) {
                        t.placePheromone(uniqueAnthillId, new FoodSourcePheromone(this.getColor()));
                    }
                    if (t instanceof AntHillTile anth && anth.getUniqueId() == this.uniqueAnthillId) {
                        if(holdedResources.isEmpty())
                        {
                            backToColony = false;
                            this.path = new ArrayList<>();
                            this.path.add(position);
                            this.deadCells.clear();
                        }
                        else {
                            anth.onInteract(this);
                        }
                    } else {
                        if (path.isEmpty()) {
                            if (t instanceof ResourceTile rt) {
                                while (!getResources().isEmpty()) {
                                    rt.getResources().add(getResources().remove());
                                }
                            }
                        } else {
                            Direction h = Direction.fromOffset(path.get(path.size() - 1).add(position.mult(-1)));
                            headingDirection = h;
                            this.position = path.remove(path.size() - 1);
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
            .get("food");
    }

    @Override
    protected void executeAction() {
        backToColony = this.holdedResources.isFull() || backToColony;
        if(backToColony)
        {
            stageMachine.setTransition("fullofresources");
        }
        else
        {
            stageMachine.setTransition("backtofood");
        }
        stageMachine.step();
    }

    @Override
    protected void onOrderRecieved(AntOrder order) {
        if(order == AntOrder.SEARCHFORFOOD)
        {
            stageMachine.setTransition("backtofood");
        }else if(order==AntOrder.BACKTOCOLONY)
        {
            stageMachine.setTransition("stayincolony");
        }
    }

    private void takeResource(ResourceTile tile) {
        if (!holdedResources.isFull()) {
            Resource r = tile.take();
            if (r != null) {
                try {
                    holdedResources.add(r);
                } catch (Exception e) {
                    System.err.println(e);
                }
            }
        }
    }

    public HoldedResourceList getResources() {
        return this.holdedResources;
    }
}
