package fr.florian.ants.antv1.ui;

import fr.florian.ants.antv1.map.AntHillTile;
import fr.florian.ants.antv1.map.Map;
import fr.florian.ants.antv1.util.Vector;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ScoreDisplay extends VBox {

    private java.util.Map<Long, BorderPane> boxes;

    public ScoreDisplay(WorldView view)
    {
        boxes = new HashMap<>();
        List<AntHillTile> hills = Map.getInstance().getAntHills();
        hills.sort((a, b)->{
            if(a.getScore() == b.getScore()) return 0;
            return a.getScore() > b.getScore() ? -1 : 1;
        });
        for(AntHillTile hill : hills) {
            HillScoreDisplay box = new HillScoreDisplay(view, hill);
            this.getChildren().add(box);
        }
    }

    public void update()
    {
        List<Node> childs = new ArrayList<>(getChildren());
        childs.sort((Node a, Node b)->{
            if(a instanceof HillScoreDisplay ha && b instanceof HillScoreDisplay hb) {
                if (ha.getScore() == hb.getScore()) return 0;
                return ha.getScore() > hb.getScore() ? -1 : 1;
            }
            return 0;
        });
        for(Node n : childs)
        {
            if(childs.indexOf(n) == 0)
                ((HillScoreDisplay)n).setWinning();
            else
                ((HillScoreDisplay)n).setLoosing();
        }
    }
}
