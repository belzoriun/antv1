package fr.florian.ants.antv1.ui;

import fr.florian.ants.antv1.map.AntHillTile;
import fr.florian.ants.antv1.map.Map;
import fr.florian.ants.antv1.util.Vector;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;

/**
 * Class used to display colonies' scores
 */
public class HillScoreDisplay extends BorderPane {
    AntHillTile hill;

    public HillScoreDisplay(WorldView view, AntHillTile tile)
    {
        hill = tile;
        Text t = new Text("Colony "+hill.getUniqueId()+" : "+hill.getScore());
        t.setFill(hill.getColor());
        setLeft(t);
        Vector v = Map.getInstance().getTilePosition(hill);
        if (v == null) {
            Text e = new Text("Colony not found");
            e.setFill(hill.getColor());
            setRight(e);
        } else {
            Button b = new GoToColonyButton(view, v, "Go to colony");
            setRight(b);
        }
    }

    public void setWinning()
    {
        ((Text)getLeft()).setText("Colony "+hill.getUniqueId()+" : "+hill.getScore()+"  WIN");
    }

    public void setLoosing()
    {
        ((Text)getLeft()).setText("Colony "+hill.getUniqueId()+" : "+hill.getScore());
    }

    public int getScore()
    {
        return hill.getScore();
    }
}
