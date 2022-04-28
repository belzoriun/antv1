package fr.florian.ants.antv1.ui;

import fr.florian.ants.antv1.map.AntHillTile;
import fr.florian.ants.antv1.map.Map;
import fr.florian.ants.antv1.util.GameTimer;
import fr.florian.ants.antv1.util.ResourceLoader;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.stage.WindowEvent;

import java.util.ArrayList;
import java.util.List;

public class PauseMenu {

    private GridPane main;
    private Stage stage;
    private Button continu;
    private Button restart;
    private boolean isEndMenu;

    private List<Text> scores;

    private Stage owner;

    public PauseMenu(Stage owner)
    {
        restart = new Button("restart");
        this.owner=owner;
        scores = new ArrayList<>();
        main = new GridPane();
        main.setHgap(5);
        main.setVgap(5);
        stage = new Stage();
        Scene scene = new Scene(main);
        stage.setScene(scene);
        stage.setTitle("Pause menu");
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(owner);
        scene.addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent evt) {
                if(evt.getCode() == KeyCode.ESCAPE)
                {
                    if(GameTimer.getInstance().isPaused())
                    {
                        playGame();
                    }
                }
            }
        });
        stage.setResizable(false);
        Button exit = new Button("exit");
        exit.setOnAction((ActionEvent e)->{
            Application.endGame();
        });
        continu = new Button("continue");
        continu.setOnAction((ActionEvent e)->{
            playGame();
        });
        List<AntHillTile> hills = Map.getInstance().getAntHills();
        for(int i = 0; i<hills.size(); i++) {
            Text text = new Text("Colony "+hills.get(i).getUniqueId()+ " : "+hills.get(i).getScore()+" points");
            text.setFill(hills.get(i).getColor());
            main.add(text, 0, i);
            scores.add(text);
        }
        main.add(exit, 0, hills.size());
        main.add(continu, 1, hills.size());
        main.setPadding(new Insets(10));
        stage.setOnCloseRequest((WindowEvent)->{
            if(isEndMenu)
            {
                owner.close();
            }
            else
                playGame();
        });
        restart.setOnAction((ActionEvent e) -> {
            Application.restart();
            System.out.println("restart");
            main.getChildren().remove(restart);
            main.add(continu, 1, Map.getInstance().getAntHills().size());
        });
    }

    private void displayScores()
    {
        List<AntHillTile> hills = Map.getInstance().getAntHills();
        int maxScore = 0;
        int maxScoreIndex = 0;
        for(int i = 0; i<hills.size(); i++) {
            if(hills.get(i).getScore()>maxScore)
            {
                maxScore = hills.get(i).getScore();
                maxScoreIndex = i;
            }
            scores.get(i).setText("Colony "+hills.get(i).getUniqueId()+ " : "+hills.get(i).getScore()+" points");
        }
        scores.get(maxScoreIndex).setText(scores.get(maxScoreIndex).getText()+"    WIN!");
    }

    public void pauseGame()
    {
        displayScores();
        stage.show();
        GameTimer.getInstance().pause();
    }

    public void playGame()
    {
        stage.hide();
        GameTimer.getInstance().play();
    }

    public void setEndMenu() {
        main.getChildren().remove(continu);
        displayScores();
        if(!main.getChildren().contains(restart))
            main.add(restart, 1, Map.getInstance().getAntHills().size());
        isEndMenu = true;
    }
}
