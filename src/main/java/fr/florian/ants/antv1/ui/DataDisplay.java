package fr.florian.ants.antv1.ui;

import javafx.scene.layout.GridPane;

public class DataDisplay extends GridPane {

    private ScoreDisplay score;

    public DataDisplay(WorldView view)
    {
        setVgap(20);
        score = new ScoreDisplay(view);
        add(score, 0, 0);
    }

    public void update()
    {
        score.update();
    }
}
