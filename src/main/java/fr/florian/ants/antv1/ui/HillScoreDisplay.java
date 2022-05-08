package fr.florian.ants.antv1.ui;

import fr.florian.ants.antv1.map.AntHillTile;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;

/**
 * Class used to display colonies' scores
 */
public class HillScoreDisplay extends BorderPane {
    private final AntHillTile hill;

    public HillScoreDisplay(WorldView view, AntHillTile tile)
    {
        hill = tile;
        Text t = new Text("Colony "+hill.getUniqueId()+" : "+hill.getScore());
        t.setFill(hill.getColor());
        setLeft(t);
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
