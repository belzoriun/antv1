package fr.florian.ants.antv1.ui;

import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;

/**
 * Class used to display simulation information
 */
public class DataDisplay extends VBox {

    private final ScoreDisplay score;
    private final TimeDisplay time;

    public DataDisplay(WorldView view)
    {
        setSpacing(20);

        time = new TimeDisplay();
        getChildren().add(time);

        score = new ScoreDisplay(view);
        ScrollPane scrollScore = new ScrollPane();
        scrollScore.setContent(score);
        score.setPrefWidth(Application.stage.getWidth()/5.5);
        getChildren().add(scrollScore);

        VBox pane = new VBox();
        ScrollPane scrollGraph = new ScrollPane();

        ScoreGraphDisplay graph = new ScoreGraphDisplay();
        pane.getChildren().add(graph);
        new Thread(graph).start();
        graph.setPrefWidth(Application.stage.getWidth()/5.5);

        AntNumberGraphDisplay ants = new AntNumberGraphDisplay();
        pane.getChildren().add(ants);
        new Thread(ants).start();
        ants.setPrefWidth(Application.stage.getWidth()/5.5);

        scrollGraph.setContent(pane);
        getChildren().add(scrollGraph);
        scrollScore.heightProperty().addListener(e-> scrollGraph.setMaxHeight(heightProperty().get()/2));
        setPrefWidth(Application.stage.getWidth()/4.5);
        scrollGraph.setPrefWidth(Application.stage.getWidth()/5);
        scrollScore.setPrefWidth(Application.stage.getWidth()/5);

        scrollScore.heightProperty().add(Application.stage.getWidth()/5);
    }

    public void update()
    {
        score.update();
        time.update();
    }
}
