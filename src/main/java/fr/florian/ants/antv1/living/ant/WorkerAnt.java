package fr.florian.ants.antv1.living.ant;

import fr.florian.ants.antv1.map.AntHillTile;
import fr.florian.ants.antv1.map.Map;
import fr.florian.ants.antv1.map.ResourceTile;
import fr.florian.ants.antv1.map.Tile;
import fr.florian.ants.antv1.util.Direction;
import fr.florian.ants.antv1.util.HoldedResourceList;
import fr.florian.ants.antv1.util.Vector;
import fr.florian.ants.antv1.util.resource.Resource;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class WorkerAnt extends Ant {

    private final HoldedResourceList holdedResources;
    private List<Vector> path;
    private List<Vector> deadCells;

    private boolean backToColony;

    public WorkerAnt(long anthillId, Color color, Vector ipos) {
        super(anthillId, color, ipos, 7, 1);
        holdedResources = new HoldedResourceList(5);
        path = new ArrayList<>();
        path.add(ipos);
        deadCells = new ArrayList<>();
        backToColony = false;
    }

    @Override
    protected void executeAction() {
        Tile t = Map.getInstance().getTile(position);
        if (t != null) {
            backToColony = this.holdedResources.isFull() || backToColony;
            if(backToColony)
            {
                t.placePheromone();
                if(t instanceof AntHillTile anth && anth.getUniqueId() == this.uniqueAnthillId)
                {
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
                }
                else
                {
                    Direction h = Direction.fromOffset(path.get(path.size()-1).add(position.mult(-1)));
                    headingDirection = h;
                    this.position = path.remove(path.size()-1);
                }
                return;
            }
            synchronized (t) {
                if (t instanceof ResourceTile res) {
                    if (res.resourceCount() > 0)
                    {
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
                if(next != null && !path.contains(pos) && ! deadCells.contains(pos)) {
                    if(next.getPheromoneLevel() > pheromoneLvl)
                    {
                        selection = new ArrayList<>();
                        pheromoneLvl = next.getPheromoneLevel();
                    }
                    if(next.getPheromoneLevel() == pheromoneLvl) {
                        selection.add(dir);
                    }
                }
            }
            if (selection.isEmpty()) {
                deadCells.add(position);
                Vector newPos = path.remove(path.size()-1);
                headingDirection = Direction.fromOffset(position.add(newPos.mult(-1)));
                position = newPos;
            } else {
                Direction dir = selection.get(new Random().nextInt(0, selection.size()));
                headingDirection = dir;
                position = position.add(dir.getOffset());
                path.add(position);
            }
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
