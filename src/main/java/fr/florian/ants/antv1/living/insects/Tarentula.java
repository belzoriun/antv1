package fr.florian.ants.antv1.living.insects;

import fr.florian.ants.antv1.living.Living;
import fr.florian.ants.antv1.living.ant.WorkerAnt;
import fr.florian.ants.antv1.map.Map;
import fr.florian.ants.antv1.ui.WorldView;
import fr.florian.ants.antv1.util.Direction;
import fr.florian.ants.antv1.util.GameTimer;
import fr.florian.ants.antv1.util.ResourceLoader;
import fr.florian.ants.antv1.util.Vector;
import fr.florian.ants.antv1.util.fight.Attacker;
import fr.florian.ants.antv1.util.statemachine.StateMachine;
import javafx.scene.Node;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class Tarentula extends Living {

    public Tarentula(Vector pos) {
        super(pos, 50, 50, 5);
        initCore(new StateMachine.StateMachineBuilder()
                .addState("move", ()->{
                    Direction dir = Direction.random();
                    while(Map.getInstance().getTile(position.add(dir.getOffset())) == null)
                    {
                        dir = Direction.random();
                    }
                    headingDirection = dir;
                    position = getPosition().add(dir.getOffset());
                })
                .get("move"));
    }

    @Override
    protected String getNextAction() {
        if(GameTimer.getInstance().isDay())
        {
            Living.GOD.setDamage(2);
            Living.GOD.attack(this);
            Living.GOD.resetDamage();
        }
        return null;
    }

    @Override
    public void onKilled(Attacker killer) {

    }

    @Override
    protected void onAttackedBy(Living l) {

    }

    @Override
    public Node getDetailDisplay() {
        return new ImageView(getStateMachineDisplay());
    }

    @Override
    public void draw(GraphicsContext context, Vector position) {
        double rotation = 0;
        if(headingDirection != null) {
            rotation = switch (headingDirection) {
                case LEFT -> 90;
                case RIGHT -> -90;
                case DOWN -> 180;
                default -> 0;
            };
        }
        Image i = ResourceLoader.getInstance().loadResource(ResourceLoader.SPIDER);
        WorldView.drawRotatedImage(context, i, position.add(WorldView.TILE_SIZE/2), rotation, WorldView.TILE_SIZE*3);
        drawLifepoints(context, position, WorldView.TILE_SIZE*3);
    }
}
