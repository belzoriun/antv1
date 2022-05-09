package fr.florian.ants.antv1.ui;

import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

/**
 * Class displaying the start menu
 */
public class StartMenu extends BorderPane {
    public StartMenu()
    {
        VBox buttons = new VBox();
        Button start = new Button("Start simulation");
        start.setOnAction((ActionEvent e)->{
            Application.switchToOptionScreen();
        });
        Button exit = new Button("Exit");
        exit.setOnAction((ActionEvent e)-> Application.stage.close());
        buttons.getChildren().add(start);
        buttons.getChildren().add(exit);
        setCenter(buttons);
        buttons.setAlignment(Pos.CENTER);
        buttons.setSpacing(50);
    }
}
