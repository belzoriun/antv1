package fr.florian.ants.antv1.ui;

import fr.florian.ants.antv1.util.GameTimer;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * Class representing the pause menu
 */
public class PauseMenu extends VBox{

    private final Stage stage;
    private final Button continu;
    private boolean isEndMenu;

    public PauseMenu(Stage owner)
    {
        Button restart = new Button("restart");
        setPrefWidth(120);
        setSpacing(5);
        stage = new Stage();
        Scene scene = new Scene(this);
        stage.setScene(scene);
        stage.setTitle("Pause menu");
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(owner);
        scene.addEventFilter(KeyEvent.KEY_PRESSED, evt -> {
            if(evt.getCode() == KeyCode.ESCAPE)
            {
                if(GameTimer.getInstance().isPaused())
                {
                    playGame();
                }
            }
        });
        stage.setResizable(false);
        Button exit = new Button("exit");
        exit.setOnAction((ActionEvent e)->{
            Application.switchToMenuScreen();
            stage.close();
        });
        continu = new Button("continue");
        continu.setOnAction((ActionEvent e)-> playGame());
        getChildren().add(continu);
        getChildren().add(restart);
        getChildren().add(exit);
        setPadding(new Insets(10));
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
        continu.setMinWidth(getPrefWidth());
        restart.setMinWidth(getPrefWidth());
        exit.setMinWidth(getPrefWidth());
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

    /**
     * Set this menu to "game end" mode
     */
    public void setEndMenu() {
        continu.setDisable(true);
        stage.setTitle("Game end");
        isEndMenu = true;
    }
}
