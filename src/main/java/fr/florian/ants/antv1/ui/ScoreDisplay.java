package fr.florian.ants.antv1.ui;

import fr.florian.ants.antv1.map.AntHillTile;
import fr.florian.ants.antv1.map.Map;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;

import java.util.List;

public class ScoreDisplay extends GridPane {

    public void update()
    {
        getChildren().clear();
        List<AntHillTile> hills = Map.getInstance().getAntHills();
        hills.sort((a, b)->{
            if(a.getScore() == b.getScore()) return 0;
            return a.getScore() > b.getScore() ? -1 : 1;
        });
        for(AntHillTile hill : hills)
        {
            int row = hills.indexOf(hill);
            this.add(new Text("Colony "+hill.getUniqueId()+" : "+hill.getScore()+"  "+(row == 0 ? "WIN":"")), 0, row);
        }
    }
}
