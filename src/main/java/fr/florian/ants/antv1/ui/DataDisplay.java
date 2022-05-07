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
        ScrollPane scrollc = new ScrollPane();
        scrollc.setContent(score);
        score.setPrefWidth(Application.stage.getWidth()/5.5);
        getChildren().add(scrollc);

        VBox pane = new VBox();
        ScrollPane scrollg = new ScrollPane();

        ScoreGraphDisplay graph = new ScoreGraphDisplay();
        pane.getChildren().add(graph);
        new Thread(graph).start();
        graph.setPrefWidth(Application.stage.getWidth()/5.5);

        AntNumberGraphDisplay ants = new AntNumberGraphDisplay();
        pane.getChildren().add(ants);
        new Thread(ants).start();
        ants.setPrefWidth(Application.stage.getWidth()/5.5);

        scrollg.setContent(pane);
        getChildren().add(scrollg);
        scrollc.heightProperty().addListener(e->{
            double bonusHeight = heightProperty().get()/2-scrollc.heightProperty().get();
            if(bonusHeight < 0) bonusHeight = 0;
            scrollg.setMaxHeight(heightProperty().get()/2+bonusHeight);
        });
        setPrefWidth(Application.stage.getWidth()/4.5);
        scrollg.setPrefWidth(Application.stage.getWidth()/5);
        scrollc.setPrefWidth(Application.stage.getWidth()/5);
    }

    public void update()
    {
        score.update();
        time.update();
    }
}
