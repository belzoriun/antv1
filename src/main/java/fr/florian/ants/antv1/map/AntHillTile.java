package fr.florian.ants.antv1.map;

import fr.florian.ants.antv1.living.Living;
import fr.florian.ants.antv1.living.ant.Ant;
import fr.florian.ants.antv1.living.ant.QueenAnt;
import fr.florian.ants.antv1.living.ant.SoldierAnt;
import fr.florian.ants.antv1.living.ant.WorkerAnt;
import fr.florian.ants.antv1.ui.Application;
import fr.florian.ants.antv1.ui.WorldView;
import fr.florian.ants.antv1.util.ImageColorMaker;
import fr.florian.ants.antv1.util.ResourceLoader;
import fr.florian.ants.antv1.util.Vector;
import fr.florian.ants.antv1.util.option.OptionKey;
import fr.florian.ants.antv1.util.resource.Resource;
import javafx.scene.Node;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Class representing an ant hill
 */
public class AntHillTile extends Tile{

    private static long currentId = 0L;

    private int foodHeld;
    private VBox detailNode;

    private final long uniqueId;
    private final Color color;

    private java.util.Map<Ant, Button> followBottons;

    private int score;

    public AntHillTile()
    {
        followBottons = new HashMap<>();
        foodHeld = 0;
        this.uniqueId = currentId;
        currentId+= 1L;
        score = 0;
        color = Color.rgb(Application.random.nextInt(0, 160), Application.random.nextInt(0, 160), Application.random.nextInt(0, 160));
        ResourceLoader.getInstance().saveResource("ant"+color.getRed()+":"+color.getGreen()+":"+color.getBlue()
                , ImageColorMaker.colorAntImage(ResourceLoader.getInstance().loadResource(ResourceLoader.ANT), color));
        ResourceLoader.getInstance().saveResource("anthill"+color.getRed()+":"+color.getGreen()+":"+color.getBlue()
                , ImageColorMaker.colorAntImage(ResourceLoader.getInstance().loadResource(ResourceLoader.ANTHILL), color));
    }

    public void addFood()
    {
        foodHeld++;
    }

    public boolean consumeFood(int amount)
    {
        if(amount > foodHeld) return false;
        foodHeld -= amount;
        return true;
    }

    /**
     * Spawn initial ants (1 queen, 5 soldiers ant 50 workers)
     * @param pos Position where to initially spawn ants
     */
    public void makeInitialSpawns(Vector pos)
    {
        QueenAnt queen = new QueenAnt(uniqueId, color, pos);
        synchronized (Map.getInstance()) {
            Map.getInstance().spawn(queen);
        }
        System.out.println("spawned queen");
        for (int i = 0; i < Application.options.getInt(OptionKey.SOLDIER_PER_QUEEN); i++) {
            SoldierAnt s = new SoldierAnt(uniqueId, queen, color, pos);
            synchronized (Map.getInstance()) {
                Map.getInstance().spawn(s);
            }
            for (int j = 0; j < Application.options.getInt(OptionKey.WORKER_PER_SOLDIER); j++) {
                synchronized (Map.getInstance()) {
                    WorkerAnt w = new WorkerAnt(uniqueId, s, color, pos);
                    Map.getInstance().spawn(w);
                }
            }
        }
        System.out.println("spawned "+Application.options.getInt(OptionKey.WORKER_PER_SOLDIER)*Application.options.getInt(OptionKey.SOLDIER_PER_QUEEN)+" workers");
    }

    public final long getUniqueId()
    {
        return uniqueId;
    }

    public final Color getColor()
    {
        return color;
    }

    @Override
    public void onWalkOn(Living l) {
        if(l instanceof Ant a && a.getAntHillId() == this.uniqueId)
        {
            a.heal();
        }
    }

    @Override
    public void onInteract(Ant a) {
        if(a instanceof WorkerAnt want)
        {
            Resource r = want.getResources().remove();
            this.score += r.getResourceScore();
            r.onDeposit(this);
        }
    }

    @Override
    public void onAntDieOn(Ant a) {
        if(a instanceof WorkerAnt want)
        {
            while(!want.getResources().isEmpty()) {
                this.score += want.getResources().remove().getResourceScore();
            }
        }
    }

    @Override
    public Node getInfoDisplay() {
        Label score = new Label("Score : " + getScore());
        Label id = new Label("Id : " + uniqueId);
        Label food = new Label("Food : " + foodHeld);
        if(detailNode == null) {

            VBox antsPane = new VBox();
            List<Ant> ants = Map.getInstance().getAntsOf(uniqueId);
            for (Ant ant : ants) {
                HBox box = new HBox();
                Label l = new Label(ant.getClass().getSimpleName() + " " + ant.getPosition());
                box.getChildren().add(l);
                if (!followBottons.containsKey(ant)) {
                    Button b = new Button("Follow");
                    b.setOnAction(e -> {
                        WorldView.follow(ant);
                    });
                    followBottons.put(ant, b);
                }
                box.getChildren().add(followBottons.get(ant));
                antsPane.getChildren().add(box);
            }

            detailNode = new VBox();
            detailNode.getChildren().add(id);
            detailNode.getChildren().add(score);
            detailNode.getChildren().add(food);
            ScrollPane antScroll = new ScrollPane();
            antScroll.setContent(antsPane);
            antScroll.setMaxHeight(200);
            detailNode.getChildren().add(antScroll);
        }
        else
        {
            detailNode.getChildren().set(0, id);
            detailNode.getChildren().set(1, score);
            detailNode.getChildren().set(2, food);
            VBox pane = (VBox) ((ScrollPane)detailNode.getChildren().get(3)).getContent();

            List<Ant> ants = Map.getInstance().getAntsOf(uniqueId);
            for (Ant ant : ants) {
                boolean found = false;
                List<Node> trash = new ArrayList<>();
                for(Node node : pane.getChildren())
                {
                    HBox box = (HBox)node;
                    for(java.util.Map.Entry<Ant, Button> entry : followBottons.entrySet()) {
                        if (entry.getValue().equals(box.getChildren().get(1)) && entry.getKey().equals(ant)) {
                            found = true;
                            if (!ant.isAlive()) {
                                trash.add(node);
                            } else {
                                ((Label) box.getChildren().get(0)).setText(ant.getClass().getSimpleName() + " " + ant.getPosition());
                            }
                            break;
                        }
                    }
                }
                for(Node n:trash)
                {
                    pane.getChildren().remove(n);
                }
                if(!found)
                {
                    HBox box = new HBox();
                    Label l = new Label(ant.getClass().getSimpleName() + " " + ant.getPosition());
                    box.getChildren().add(l);
                    if (!followBottons.containsKey(ant)) {
                        Button b = new Button("Follow");
                        b.setOnAction(e -> {
                            WorldView.follow(ant);
                        });
                        followBottons.put(ant, b);
                    }
                    box.getChildren().add(followBottons.get(ant));
                    pane.getChildren().add(box);
                }
            }

        }

        return detailNode;
    }

    public int getScore()
    {
        return score;
    }

    @Override
    public void draw(GraphicsContext context, Vector position) {
        context.drawImage(ResourceLoader.getInstance().loadResource(ResourceLoader.GRASS_RES_1)
                , position.getX()
                , position.getY()
                , WorldView.TILE_SIZE
                , WorldView.TILE_SIZE);
        context.drawImage(
                ResourceLoader.getInstance().loadResource("anthill"+color.getRed()+":"+color.getGreen()+":"+color.getBlue()),
                position.getX(),
                position.getY()-WorldView.TILE_SIZE/2
                , WorldView.TILE_SIZE
                , WorldView.TILE_SIZE);
    }

    public void makeSpawn(Ant ant, boolean revive) {
        if(ant.getAntHillId() != uniqueId)
        {
            score += 10;
            return;
        }
        if(revive || ant instanceof SoldierAnt)
        {
            Map.getInstance().spawn(ant);
            return;
        }
        if(ant instanceof WorkerAnt) {
            java.util.Map<SoldierAnt, Integer> antsPerSoldier = new HashMap<>();
            for (Ant a : Map.getInstance().getAntsOf(uniqueId)) {
                if (a instanceof WorkerAnt wa) {
                    if (antsPerSoldier.containsKey(wa.getSoldier())) {
                        antsPerSoldier.put(wa.getSoldier(), antsPerSoldier.get(wa.getSoldier()) + 1);
                    } else {
                        antsPerSoldier.put(wa.getSoldier(), 1);
                    }
                }
            }
            for (java.util.Map.Entry<SoldierAnt, Integer> entry : antsPerSoldier.entrySet()) {
                if (entry.getValue() < Application.options.getInt(OptionKey.WORKER_PER_SOLDIER)) {
                    entry.getKey().subscribe(ant);
                }
            }
            Map.getInstance().spawn(ant);
        }
    }
}
