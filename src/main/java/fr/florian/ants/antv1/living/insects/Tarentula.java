package fr.florian.ants.antv1.living.insects;

import fr.florian.ants.antv1.living.Living;
import fr.florian.ants.antv1.living.ant.Ant;
import fr.florian.ants.antv1.living.ant.WorkerAnt;
import fr.florian.ants.antv1.map.AntHillTile;
import fr.florian.ants.antv1.map.Map;
import fr.florian.ants.antv1.map.Tile;
import fr.florian.ants.antv1.ui.WorldView;
import fr.florian.ants.antv1.util.Direction;
import fr.florian.ants.antv1.util.GameTimer;
import fr.florian.ants.antv1.util.ResourceLoader;
import fr.florian.ants.antv1.util.Vector;
import fr.florian.ants.antv1.util.fight.Attacker;
import fr.florian.ants.antv1.util.statemachine.StateMachine;
import javafx.scene.Node;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

public class Tarentula extends Living {

    private static final int VIEW_FIELD = 3;
    private Vector target;

    public Tarentula(Vector pos) {
        super(pos, 30, 50, 5);
        initCore(new StateMachine.StateMachineBuilder()
                .addState("setpath", ()->{
                    target = null;
                    for(double x = position.getX()-VIEW_FIELD; x <= position.getX()+VIEW_FIELD; x++)
                    {
                        for(double y = position.getY()-VIEW_FIELD; y <= position.getY()+VIEW_FIELD; y++)
                        {
                            Vector enemy = new Vector(x, y);
                            if(!enemy.equals(position) && !Map.getInstance().getLivingsAt(enemy).isEmpty())
                            {
                                if(target == null || target.delta(position) >
                                        enemy.delta(position))
                                {
                                    target = enemy;
                                }
                            }
                        }
                    }
                    if(target == null)
                    {
                        headingDirection = Direction.random();
                        while(Map.getInstance().getTile(position.add(headingDirection.getOffset())) == null)
                        {
                            headingDirection = Direction.random();
                        }
                        position = position.add(headingDirection.getOffset());
                    }
                    else
                    {
                        executeDirectly("moveonpath");
                    }
                })
                .addState("moveonpath", ()->{
                    if(position.getX() < target.getX())
                    {
                        headingDirection = Direction.RIGHT;
                    }
                    else if(position.getX() > target.getX())
                    {
                        headingDirection = Direction.LEFT;
                    }
                    else
                    {
                        if(position.getY() < target.getY())
                        {
                            headingDirection = Direction.DOWN;
                        }
                        else if(position.getY() > target.getY())
                        {
                            headingDirection = Direction.UP;
                        }
                        else
                        {
                            return;
                        }
                    }
                    position = position.add(headingDirection.getOffset());
                })
                .addTransition("moveonpath")
                .addTransition("setpath")
                .addStateLink("setpath", "moveonpath", "moveonpath")
                .addStateLink("moveonpath", "setpath", "setpath")
                .get("setpath"));
    }

    public void onUpdate(){
        if(GameTimer.getInstance().isDay())
        {
            Living.GOD.attack(this, 2);
        }
    }

    @Override
    protected String getNextAction() {
        if(position.equals(target))
        {
            return "setpath";
        }
        return null;
    }

    @Override
    public void onKilled(Attacker killer) {
        if(killer instanceof Ant a)
        {
            AntHillTile t = Map.getInstance().getAntHills().stream().filter(h->h.getUniqueId() == a.getAntHillId()).toList().get(0);
            t.addScore(20);
            for(int i = 0; i<25; i++) {
                t.addFood();
            }
        }
    }

    @Override
    protected void onAttackedBy(Living l) {

    }

    @Override
    public Node getDetailDisplay() {
        VBox box = new VBox();
        box.getChildren().add(new Label("Target at "+target));
        box.getChildren().add(new ImageView(getStateMachineDisplay()));
        return box;
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
