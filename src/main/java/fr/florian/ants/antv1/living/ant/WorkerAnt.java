package fr.florian.ants.antv1.living.ant;

import fr.florian.ants.antv1.living.LivingEntity;
import fr.florian.ants.antv1.living.ant.entity.AntEntity;
import fr.florian.ants.antv1.living.ant.entity.ResourceHolderAntEntity;
import fr.florian.ants.antv1.map.AntHillTile;
import fr.florian.ants.antv1.map.Map;
import fr.florian.ants.antv1.map.ResourceHoldTile;
import fr.florian.ants.antv1.map.Tile;
import fr.florian.ants.antv1.ui.Application;
import fr.florian.ants.antv1.util.AntOrder;
import fr.florian.ants.antv1.util.Direction;
import fr.florian.ants.antv1.util.Vector;
import fr.florian.ants.antv1.util.fight.Attacker;
import fr.florian.ants.antv1.util.option.OptionKey;
import fr.florian.ants.antv1.util.pheromone.FoodSourcePheromone;
import fr.florian.ants.antv1.util.resource.DeadAnt;
import fr.florian.ants.antv1.util.statemachine.StateMachine;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


/**
 * Class representing a worker ant
 */
public class WorkerAnt extends Ant {

    public WorkerAnt() {
        super(java.util.Set.of(Ants.SOLDIER), 0, 1,8, 1, 5, 1);
    }

    @Override
    public LivingEntity createEntity(Vector initialPosition) {
        return new ResourceHolderAntEntity("food", initialPosition, this);
    }

    private void goForFood(ResourceHolderAntEntity r)
    {
        r.setState("food");
        r.dirtyPath();
        Tile t = Map.getInstance().getTile(r.getPosition());
        if (t != null) {
            if (t instanceof ResourceHoldTile res) {
                if (res.resourceCount() > 0) {
                    r.takeResource(res);
                    return;
                }
                else
                {
                    if(!r.getHeldResources().getAll().stream().filter(reso->reso instanceof DeadAnt da && da.isOf(r.getAntHillId())).toList().isEmpty())
                    {
                        toColony(r);
                    }
                }
            }
            Direction[] dirs = Direction.values();
            List<Direction> selection = new ArrayList<>();
            int pheromoneLvl = 0;
            for (Direction dir : dirs) {
                Vector pos = r.getPosition().add(dir.getOffset());
                Tile next = Map.getInstance().getTile(pos);
                if (next != null && !r.getPath().contains(pos) && !r.getDeadCells().contains(pos)) {
                    if(next instanceof AntHillTile at && at.getUniqueId() != r.getAntHillId())
                    {
                        continue;
                    }
                    if(next instanceof ResourceHoldTile rt && !rt.getResources().isEmpty())
                    {
                        selection.clear();
                        selection.add(dir);
                        break;
                    }
                    if (next.getPheromoneLevel(r.getAntHillId(), FoodSourcePheromone.class) > pheromoneLvl) {
                        selection = new ArrayList<>();
                        pheromoneLvl = next.getPheromoneLevel(r.getAntHillId());
                    }
                    if (next.getPheromoneLevel(r.getAntHillId(), FoodSourcePheromone.class) == pheromoneLvl) {
                        selection.add(dir);
                    }
                }
            }
            if (selection.isEmpty()) {
                r.getDeadCells().add(r.getPosition());
                if(r.getPath().size()>1)
                {
                    r.removeFromPath();
                    Vector newPos = r.getPath().get(r.getPath().size()-1);
                    r.setHeadingDirection(Direction.fromOffset(r.getPosition().add(newPos.multi(-1))));
                    synchronized (Map.getInstance().getTile(r.getPosition())) {
                        r.setPosition(newPos);
                    }
                }
            } else {
                Direction dir = selection.get(Application.random.nextInt(0, selection.size()));
                r.setHeadingDirection(dir);
                synchronized (Map.getInstance().getTile(r.getPosition())) {
                    r.setPosition(r.getPosition().add(dir.getOffset()));
                }
                r.addToPath(r.getPosition());
            }
        }
    }

    private void stayInColony(ResourceHolderAntEntity r)
    {
        r.setState("stay");
        Tile t = Map.getInstance().getTile(r.getPosition());
        if (t != null) {
            if (t instanceof AntHillTile antHill && antHill.getUniqueId() == r.getAntHillId()) {
                if(!r.getHeldResources().isEmpty()){
                    antHill.onInteract(r);
                }
            } else {
                if (r.getPath().isEmpty()) {
                    if (t instanceof ResourceHoldTile rt) {
                        while (!r.getHeldResources().isEmpty()) {
                            rt.placeResource(r.getHeldResources().remove());
                        }
                    }
                } else {
                    r.removeFromPath();
                    if(!r.getPath().isEmpty()) {
                        Vector newPos = r.getPath().get(r.getPath().size() - 1);
                        r.setHeadingDirection(Direction.fromOffset(newPos.add(r.getPosition().multi(-1))));
                        synchronized (Map.getInstance().getTile(r.getPosition())) {
                            t.placePheromone(r.getAntHillId(), new FoodSourcePheromone(r.getColor()));
                            r.setPosition(newPos);
                        }
                    }
                }
            }
        }
    }

    private void toColony(ResourceHolderAntEntity r){
        r.setState("colony");
        r.clearPath();
        Tile t = Map.getInstance().getTile(r.getPosition());
        if (t != null) {
            if (t instanceof AntHillTile antHill && antHill.getUniqueId() == r.getAntHillId()) {
                if(r.getHeldResources().isEmpty())
                {
                    r.resetPath();
                    r.getDeadCells().clear();
                    goForFood(r);
                }
                else {
                    antHill.onInteract(r);
                }
            } else {
                if (r.getPath().isEmpty()) {
                    if (t instanceof ResourceHoldTile rt) {
                        while (!r.getHeldResources().isEmpty()) {
                            rt.placeResource(r.getHeldResources().remove());
                        }
                    }
                    goForFood(r);
                } else {
                    try {
                        r.removeFromPath();
                    }catch(IndexOutOfBoundsException e)
                    {
                        System.out.println(r.getPath());
                    }
                    if(!r.getPath().isEmpty()) {
                        Vector newPos = r.getPath().get(r.getPath().size() - 1);
                        r.setHeadingDirection(Direction.fromOffset(newPos.add(r.getPosition().multi(-1))));
                        synchronized (Map.getInstance().getTile(r.getPosition())) {
                            t.placePheromone(r.getAntHillId(), new FoodSourcePheromone(r.getColor()));
                            r.setPosition(newPos);
                        }
                    }
                }
            }
        }
    }

    @Override
    public void onOrderReceived(AntEntity self, AntOrder order) {
        if(order == AntOrder.SEARCH_FOR_FOOD)
        {
            goForFood((ResourceHolderAntEntity) self);
        }else if(order==AntOrder.BACK_TO_COLONY)
        {
            stayInColony((ResourceHolderAntEntity) self);
        }
    }

    @Override
    public void onKilled(Attacker killer, LivingEntity self) {

    }

    @Override
    public void execute(LivingEntity self) {
        if(self instanceof ResourceHolderAntEntity re) {
            if (re.getHeldResources().isFull() || self.getLifePoints() <= getMaxLifePoints()/2) {
                toColony(re);
            }
            else if(Objects.equals(re.getState(), "food"))
            {
                goForFood(re);
            }
            else if(Objects.equals(re.getState(), "stay"))
            {
                stayInColony(re);
            }
            else if(Objects.equals(re.getState(), "colony"))
            {
                toColony(re);
            }
        }
    }
}
