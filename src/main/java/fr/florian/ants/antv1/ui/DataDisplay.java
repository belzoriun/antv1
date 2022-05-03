package fr.florian.ants.antv1.ui;

import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

public class DataDisplay extends VBox {

    private ScoreDisplay score;

    public DataDisplay(WorldView view)
    {
        setSpacing(20);
        score = new ScoreDisplay(view);
        getChildren().add(score);

        ScoreGraphDisplay graph = new ScoreGraphDisplay();
        getChildren().add(graph);
        new Thread(graph).start();
    }

    public void update()
    {
        score.update();
    }
}
