package fr.florian.ants.antv1.ui;

import fr.florian.ants.antv1.living.Living;
import fr.florian.ants.antv1.living.LivingEntity;
import fr.florian.ants.antv1.living.ant.Ant;
import fr.florian.ants.antv1.living.ant.entity.AntEntity;
import fr.florian.ants.antv1.map.AntHillTile;
import fr.florian.ants.antv1.map.Map;
import fr.florian.ants.antv1.map.Tile;
import fr.florian.ants.antv1.util.Vector;
import javafx.geometry.Insets;
import javafx.scene.DepthTest;
import javafx.scene.control.Label;
import javafx.scene.control.TitledPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

/**
 * Class displaying tile's details
 */
public class TileDetail extends VBox {

    private final Label title;
    private final HBox pheromones;
    private final Label position;
    private final HBox ants;

    private final TitledPane detail;

    private Tile tile;
    private Vector tilePos;

    public TileDetail()
    {
        this.title = new Label();
        this.pheromones = new HBox();
        pheromones.setSpacing(10);
        this.position = new Label();
        this.ants = new HBox();
        ants.setSpacing(10);
        detail = new TitledPane();
        detail.setCollapsible(false);

        detail.setText("Detail");

        getChildren().add(title);
        getChildren().add(ants);
        getChildren().add(pheromones);
        getChildren().add(position);
        getChildren().add(detail);
        setVisible(false);
        setPadding(new Insets(10));
        setBorder(new Border(new BorderStroke(Color.BLACK,
                new BorderStrokeStyle(null, null, null, 1, 1, null),
                new CornerRadii(5), new BorderWidths(1))));
        setBackground(new Background(new BackgroundFill(Color.WHITE, new CornerRadii(5), null)));
        setOnMouseClicked((MouseEvent e)-> this.setVisible(false));
    }

    public Vector positionForDisplay()
    {
        if(!isVisible())
        {
            return new Vector(-1, -1);
        }
        return tilePos;
    }

    /**
     * Set this display to show info for the given tile, fount at a given position
     * @param t The tile
     * @param pos The position
     */
    public void displayForTile(Tile t, Vector pos)
    {
        if(t != null) {
            tile = t;
            tilePos = pos;
            setVisible(true);
        }
    }

    public void update()
    {
        if(isVisible()) {
            pheromones.getChildren().clear();
            ants.getChildren().clear();
            ants.getChildren().add(new Label("Ants : "));

            title.setText(tile.getClass().getSimpleName());
            boolean displayP = false;
            for (AntHillTile hill : Map.getInstance().getAntHills()) {
                int p = tile.getPheromoneLevel(hill.getUniqueId());
                if (p > 0) {
                    displayP = true;
                    Label l = new Label();
                    l.setText(p + "");
                    l.setTextFill(hill.getColor());
                    pheromones.getChildren().add(l);
                }
                int a = 0;
                for(LivingEntity liv : Map.getInstance().getLivingsAt(tilePos))
                {
                    if(liv instanceof AntEntity ant && ant.getAntHillId() == hill.getUniqueId())
                    {
                        a++;
                    }
                }
                Label l = new Label(a+"");
                l.setTextFill(hill.getColor());
                ants.getChildren().add(l);
            }
            if(displayP)
            {
                pheromones.getChildren().add(0, new Label("Pheromones : "));
            }
            position.setText(tilePos.toString());
            detail.setContent(tile.getInfoDisplay());
        }
    }

}
