package fr.florian.ants.antv1.map;

import fr.florian.ants.antv1.living.Living;
import fr.florian.ants.antv1.living.ant.Ant;
import fr.florian.ants.antv1.ui.WorldView;
import fr.florian.ants.antv1.util.ResourceLoader;
import fr.florian.ants.antv1.util.Vector;
import javafx.animation.AnimationTimer;
import javafx.scene.Node;
import javafx.scene.canvas.GraphicsContext;

import java.time.Period;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class WaterTile extends Tile{

    private String waterImage = ResourceLoader.WATER_1;

    public WaterTile()
    {
        Executors.newScheduledThreadPool(1).scheduleAtFixedRate(()->{
            if(waterImage == ResourceLoader.WATER_1)
                waterImage = ResourceLoader.WATER_2;
            else
                waterImage = ResourceLoader.WATER_1;
        }, 0, 500, TimeUnit.MILLISECONDS);
    }

    @Override
    public void onWalkOn(Living l) {
        l.setTicksPerExecution(l.getTicksPerExecution()+5);
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
    public void draw(GraphicsContext context, Vector position) {
        context.drawImage(ResourceLoader.getInstance().loadResource(waterImage)
                , position.getX()
                , position.getY()
                , WorldView.TILE_SIZE
                , WorldView.TILE_SIZE);
    }
}
