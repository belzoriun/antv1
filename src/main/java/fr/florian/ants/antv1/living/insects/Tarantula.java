package fr.florian.ants.antv1.living.insects;

import fr.florian.ants.antv1.living.Living;
import fr.florian.ants.antv1.living.LivingEntity;
import fr.florian.ants.antv1.living.ant.Ant;
import fr.florian.ants.antv1.living.insects.entity.InsectEntity;
import fr.florian.ants.antv1.map.Map;
import fr.florian.ants.antv1.map.ResourceTile;
import fr.florian.ants.antv1.map.Tile;
import fr.florian.ants.antv1.ui.Application;
import fr.florian.ants.antv1.util.Direction;
import fr.florian.ants.antv1.util.GameTimer;
import fr.florian.ants.antv1.util.Vector;
import fr.florian.ants.antv1.util.fight.Attacker;
import fr.florian.ants.antv1.util.resource.FoodResource;

import java.util.Objects;

public class Tarantula extends Insect {

    private static final int VIEW_FIELD = 3;

    public Tarantula() {
        super(30, 50, 5);
    }

    private void setPath(InsectEntity entity)
    {
        entity.resetTarget();
        for (double x = entity.getPosition().getX() - VIEW_FIELD; x <= entity.getPosition().getX() + VIEW_FIELD; x++) {
            for (double y = entity.getPosition().getY() - VIEW_FIELD; y <= entity.getPosition().getY() + VIEW_FIELD; y++) {
                Vector enemy = new Vector(x, y);
                if (!enemy.equals(entity.getPosition()) && !Map.getInstance().getLivingsAt(enemy).isEmpty()) {
                    if (entity.getTarget() == null || entity.getTarget().delta(entity.getPosition()) >
                            enemy.delta(entity.getPosition())) {
                        entity.setTarget(enemy);
                    }
                }
            }
        }
        if (entity.getTarget() == null) {
            entity.setHeadingDirection(Direction.random());
            while (Map.getInstance().getTile(entity.getPosition().add(entity.getHeadingDirection().getOffset())) == null) {
                entity.setHeadingDirection(Direction.random());
            }
            entity.setPosition(entity.getPosition().add(entity.getHeadingDirection().getOffset()));
        } else {
            moveOnPath(entity);
        }
    }

    private void moveOnPath(InsectEntity entity)
    {
        if (entity.getPosition().getX() < entity.getTarget().getX()) {
            entity.setHeadingDirection(Direction.RIGHT);
        } else if (entity.getPosition().getX() > entity.getTarget().getX()) {
            entity.setHeadingDirection(Direction.LEFT);
        } else {
            if (entity.getPosition().getY() < entity.getTarget().getY()) {
                entity.setHeadingDirection(Direction.DOWN);
            } else if (entity.getPosition().getY() > entity.getTarget().getY()) {
                entity.setHeadingDirection(Direction.UP);
            } else {
                return;
            }
        }
        entity.setPosition(entity.getPosition().add(entity.getHeadingDirection().getOffset()));
    }

    @Override
    public LivingEntity createEntity(Vector initialPosition) {
        return new InsectEntity("set", initialPosition, this);
    }

    public void onUpdate(LivingEntity self){
        if(GameTimer.getInstance().isDay())
        {
            Living.GOD.attack(self, 2);
        }
    }

    @Override
    public void onKilled(Attacker killer, LivingEntity self) {
        if(killer instanceof Ant)
        {
            for(int i = 0; i<25; i++)
            {
                Vector v = new Vector(Application.random.nextInt((int) (self.getPosition().getX()-1), (int) (self.getPosition().getX()+1)),
                        Application.random.nextInt((int) (self.getPosition().getY()-1), (int) (self.getPosition().getY()+1)));
                System.out.println(v);
                Tile t = Map.getInstance().getTile(v);
                if(t instanceof ResourceTile rt)
                {
                    rt.placeResource(new FoodResource(v.add(Application.random.nextDouble(0.3, 0.7))));
                }
            }
        }
    }

    @Override
    public void execute(LivingEntity livingEntity) {
        if(livingEntity instanceof InsectEntity e)
        {
            if(Objects.equals(e.getState(), "set"))
            {
                setPath(e);
            }
            else if(Objects.equals(e.getState(), "move"))
            {
                moveOnPath(e);
            }
        }
    }
}
