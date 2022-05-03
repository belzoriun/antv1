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
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.stage.WindowEvent;

import java.util.ArrayList;
import java.util.List;

public class PauseMenu {

    private VBox main;
    private Stage stage;
    private Button continu;
    private Button restart;
    private boolean isEndMenu;

    public PauseMenu(Stage owner)
    {
        restart = new Button("restart");
        main = new VBox();
        main.setPrefWidth(120);
        main.setSpacing(5);
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
        main.getChildren().add(continu);
        main.getChildren().add(restart);
        main.getChildren().add(exit);
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
            continu.setDisable(false);
            stage.hide();
        });
        continu.setMinWidth(main.getPrefWidth());
        restart.setMinWidth(main.getPrefWidth());
        exit.setMinWidth(main.getPrefWidth());
    }

    public void pauseGame()
    {
        stage.show();
        GameTimer.getInstance().pause();
    }

    public void playGame()
    {
        stage.hide();
        GameTimer.getInstance().play();
    }

    public void setEndMenu() {
        continu.setDisable(true);
        isEndMenu = true;
    }
}
