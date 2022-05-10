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

    private VBox ants;
    private Label scoreLbl;
    private Label id;
    private Label food;

    private final long uniqueId;
    private final Color color;

    private java.util.Map<Ant, HBox> followBottons;

    private int score;

    public AntHillTile()
    {
        followBottons = new HashMap<>();
        detailNode = new VBox();

        foodHeld = 0;
        this.uniqueId = currentId;
        currentId+= 1L;
        score = 0;
        color = Color.rgb(Application.random.nextInt(0, 160), Application.random.nextInt(0, 160), Application.random.nextInt(0, 160));
        ResourceLoader.getInstance().saveResource("ant"+color.getRed()+":"+color.getGreen()+":"+color.getBlue()
                , ImageColorMaker.colorAntImage(ResourceLoader.getInstance().loadResource(ResourceLoader.ANT), color));
        ResourceLoader.getInstance().saveResource("anthill"+color.getRed()+":"+color.getGreen()+":"+color.getBlue()
                , ImageColorMaker.colorAntImage(ResourceLoader.getInstance().loadResource(ResourceLoader.ANTHILL), color));

        scoreLbl = new Label("Score : " + getScore());
        id = new Label("Id : " + uniqueId);
        food = new Label("Food : " + foodHeld);
        ants = new VBox();

        ScrollPane scroll = new ScrollPane();
        scroll.setContent(ants);
        scroll.setMaxHeight(200);
        scroll.setPrefWidth(200);
        detailNode.getChildren().add(scoreLbl);
        detailNode.getChildren().add(id);
        detailNode.getChildren().add(food);
        detailNode.getChildren().add(scroll);
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

        scoreLbl.setText("Score : " + getScore());
        id.setText("Id : " + uniqueId);
        food.setText("Food : " + foodHeld);

        List<Ant> antsList = Map.getInstance().getAntsOf(uniqueId);


        List<Node> trash = new ArrayList<>();
        List<Ant> trashAnt = new ArrayList<>();
        for(java.util.Map.Entry<Ant, HBox> entry : followBottons.entrySet()) {
            if (!entry.getKey().isAlive() && !antsList.contains(entry.getKey())) {
                trash.add(entry.getValue());
                trashAnt.add(entry.getKey());
                break;
            }
        }

        for (Ant ant : antsList) {
            for(java.util.Map.Entry<Ant, HBox> entry : followBottons.entrySet()) {
                if (entry.getKey().equals(ant)) {
                    if (!ant.isAlive()) {
                        trash.add(entry.getValue());
                        trashAnt.add(entry.getKey());
                    }
                    else {
                        ((Label) entry.getValue().getChildren().get(0)).setText(ant.getClass().getSimpleName() + " " + ant.getPosition());
                    }
                    break;
                }
            }
            if (!followBottons.containsKey(ant)) {
                HBox box = new HBox();
                Label l = new Label(ant.getClass().getSimpleName() + " " + ant.getPosition());
                box.getChildren().add(l);
                Button b = new Button("Follow");
                b.setOnAction(e -> {
                    WorldView.follow(ant);
                });
                box.getChildren().add(b);
                ants.getChildren().add(box);
                followBottons.put(ant, box);
            }
        }
        for(Node n:trash) {
            ants.getChildren().remove(n);
        }
        for(Ant n:trashAnt) {
            followBottons.remove(n);
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
