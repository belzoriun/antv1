package fr.florian.ants.antv1.ui;

import javafx.scene.layout.GridPane;

public class DataDisplay extends GridPane {

    private ScoreDisplay score;

    public DataDisplay()
    {
        score = new ScoreDisplay();
        add(score, 0, 0);
    }

    public void update()
    {
        score.update();
    }
}
