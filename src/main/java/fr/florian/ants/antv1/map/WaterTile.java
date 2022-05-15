package fr.florian.ants.antv1.map;

import fr.florian.ants.antv1.living.Living;
import fr.florian.ants.antv1.living.LivingEntity;
import fr.florian.ants.antv1.living.ant.Ant;
import fr.florian.ants.antv1.living.ant.entity.AntEntity;
import fr.florian.ants.antv1.ui.Application;
import fr.florian.ants.antv1.ui.WorldView;
import fr.florian.ants.antv1.util.ResourceLoader;
import fr.florian.ants.antv1.util.Vector;
import fr.florian.ants.antv1.util.effect.Wet;
import fr.florian.ants.antv1.util.pheromone.Pheromone;
import fr.florian.ants.antv1.util.resource.Resource;
import fr.florian.ants.antv1.util.statemachine.StateMachine;
import javafx.animation.AnimationTimer;
import javafx.scene.Node;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.time.Period;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class WaterTile extends Tile{

    private String[] waterImages = new String[]{
            ResourceLoader.WATER_1,
            ResourceLoader.WATER_2
    };
    private int waterImage = 0;

    private final java.util.Map<LivingEntity, HBox> followBottons;
    private final VBox livings;
    private final VBox detailNode;

    public WaterTile()
    {
        livings = new VBox();
        detailNode = new VBox();
        ScrollPane scroll = new ScrollPane();
        scroll.setContent(livings);
        scroll.setMaxHeight(200);
        scroll.setPrefWidth(200);
        detailNode.getChildren().add(scroll);
        followBottons = new HashMap<>();
    }

    @Override
    public void placePheromone(long antHillId, Pheromone p)
    {
        //prevents placing pheromones on this tile
    }

    @Override
    public void onWalkOn(LivingEntity l) {
        l.applyEffect(new Wet());
    }

    @Override
    public void onInteract(AntEntity a) {

    }

    @Override
    public void onAntDieOn(AntEntity a) {

    }

    @Override
    public Node getInfoDisplay() {

        List<LivingEntity> livingList = Map.getInstance().getLivingsAt(Map.getInstance().getTilePosition(this));

        List<Node> trash = new ArrayList<>();
        List<LivingEntity> trashLiving = new ArrayList<>();
        for(java.util.Map.Entry<LivingEntity, HBox> entry : followBottons.entrySet()) {
            if (!livingList.contains(entry.getKey())) {
                trash.add(entry.getValue());
                trashLiving.add(entry.getKey());
                break;
            }
        }

        for (LivingEntity liv : livingList) {
            for(java.util.Map.Entry<LivingEntity, HBox> entry : followBottons.entrySet()) {
                if (entry.getKey().equals(liv)) {
                    if (!liv.isAlive()) {
                        trash.add(entry.getValue());
                        trashLiving.add(entry.getKey());
                    }
                    else {
                        ((Label) entry.getValue().getChildren().get(0)).setText(liv.getClass().getSimpleName() + " " + liv.getPosition());
                    }
                    break;
                }
            }
            if (!followBottons.containsKey(liv)) {
                HBox box = new HBox();
                Label l = new Label(liv.getClass().getSimpleName() + " " + liv.getPosition());
                box.getChildren().add(l);
                Button b = new Button("Follow");
                b.setOnAction(e -> {
                    WorldView.follow(liv);
                });
                box.getChildren().add(b);
                livings.getChildren().add(box);
                followBottons.put(liv, box);
            }
        }
        for(Node n:trash) {
            livings.getChildren().remove(n);
        }
        for(LivingEntity n:trashLiving) {
            followBottons.remove(n);
        }

        return detailNode;
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
