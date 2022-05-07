package fr.florian.ants.antv1.ui;

import fr.florian.ants.antv1.map.AntHillTile;
import fr.florian.ants.antv1.map.Map;
import javafx.scene.Node;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;

/**
 * Class used to display colonies' scores
 */
public class ScoreDisplay extends VBox {

    public ScoreDisplay(WorldView view)
    {
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
        List<Node> children = new ArrayList<>(getChildren());
        children.sort((Node a, Node b)->{
            if(a instanceof HillScoreDisplay ha && b instanceof HillScoreDisplay hb) {
                if (ha.getScore() == hb.getScore()) return 0;
                return ha.getScore() > hb.getScore() ? -1 : 1;
            }
            return 0;
        });
        for(Node n : children)
        {
            if(children.indexOf(n) == 0)
                ((HillScoreDisplay)n).setWinning();
            else
                ((HillScoreDisplay)n).setLoosing();
        }
    }
}
