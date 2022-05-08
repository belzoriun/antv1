package fr.florian.ants.antv1.ui;

import fr.florian.ants.antv1.util.GameTimer;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.layout.*;

/**
 * Class representing the pause menu
 */
public class PauseMenu extends Pane {

    private final Button continueBtn;

    public PauseMenu(double initialTranslateX, double initialTranslateY)
    {
        VBox main = new VBox();
        getChildren().add(main);
        Button restart = new Button("restart");
        main.setPrefWidth(120);
        main.setSpacing(5);
        Button exit = new Button("exit");
        exit.setOnAction((ActionEvent e)-> Application.switchToMenuScreen());
        continueBtn = new Button("continue");
        continueBtn.setOnAction((ActionEvent e)-> playGame());
        main.getChildren().add(continueBtn);
        main.getChildren().add(restart);
        main.getChildren().add(exit);
        main.setPadding(new Insets(10));
        restart.setOnAction((ActionEvent e) -> {
            Application.restart();
            continueBtn.setDisable(false);
        });
        continueBtn.setMinWidth(main.getPrefWidth());
        restart.setMinWidth(main.getPrefWidth());
        exit.setMinWidth(main.getPrefWidth());

        setTranslateX(initialTranslateX);
        setTranslateY(initialTranslateY);

        this.setVisible(false);
    }

    public void pauseGame()
    {
        this.setVisible(true);
        GameTimer.getInstance().pause();
    }

    public void playGame()
    {
        this.setVisible(false);
        GameTimer.getInstance().play();
    }

    /**
     * Set this menu to "game end" mode
     */
    public void setEndMenu() {
        continueBtn.setDisable(true);
    }
}
