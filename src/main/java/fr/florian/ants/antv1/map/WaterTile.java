package fr.florian.ants.antv1.map;

import fr.florian.ants.antv1.living.Living;
import fr.florian.ants.antv1.living.ant.Ant;
import fr.florian.ants.antv1.ui.Application;
import fr.florian.ants.antv1.ui.WorldView;
import fr.florian.ants.antv1.util.ResourceLoader;
import fr.florian.ants.antv1.util.Vector;
import fr.florian.ants.antv1.util.effect.Wet;
import fr.florian.ants.antv1.util.pheromone.Pheromone;
import fr.florian.ants.antv1.util.statemachine.StateMachine;
import javafx.animation.AnimationTimer;
import javafx.scene.Node;
import javafx.scene.canvas.GraphicsContext;

import java.time.Period;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class WaterTile extends Tile{

    private String[] waterImages = new String[]{
            ResourceLoader.WATER_1,
            ResourceLoader.WATER_2
    };
    private int waterImage = 0;

    public WaterTile()
    {

    }

    @Override
    public void placePheromone(long antHillId, Pheromone p)
    {
        //prevents placing pheromones on this tile
    }

    @Override
    public void onWalkOn(Living l) {
        l.applyEffect(new Wet());
    }

    @Override
    public void onInteract(Ant a) {

    }

    @Override
    public void onAntDieOn(Ant a) {

    }

    @Override
    public Node getInfoDisplay() {
        return null;
    }

    @Override
    public void tickUpdate() {
        if(waterImage == waterImages.length-1)
        {
            waterImage = 0;
        }
        else
            waterImage++;
    }

    @Override
    public void draw(GraphicsContext context, Vector position) {
        context.drawImage(ResourceLoader.getInstance().loadResource(waterImages[waterImage])
                , position.getX()
                , position.getY()
                , WorldView.TILE_SIZE
                , WorldView.TILE_SIZE);
    }
}
