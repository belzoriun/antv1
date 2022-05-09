package fr.florian.ants.antv1.ui;

import fr.florian.ants.antv1.util.option.OptionKey;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.Optional;

public class SimulationOptionsMenu extends BorderPane {

    private static final int WARN_NB_ANTS = 1000;

    public SimulationOptionsMenu()
    {
        VBox box = new VBox();
        box.setAlignment(Pos.CENTER);
        box.setSpacing(20);
        setCenter(box);

        addIntInput(box, OptionKey.MAP_WIDTH);
        addIntInput(box, OptionKey.MAP_HEIGHT);
        addIntInput(box, OptionKey.ANT_HILL_COUNT);
        addIntInput(box, OptionKey.SOLDIER_PER_QUEEN);
        addIntInput(box, OptionKey.WORKER_PER_SOLDIER);
        addBoolInput(box, OptionKey.INFINITE_SIMULATION);

        Button start = new Button("Start");
        start.setOnAction(e->{
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.initOwner(Application.stage);
            if(Application.options.getInt(OptionKey.ANT_HILL_COUNT) > (Application.options.getInt(OptionKey.MAP_HEIGHT)*Application.options.getInt(OptionKey.MAP_WIDTH))/2) {
                alert.setContentText("Warning : the number of ant hills may be too high for a map of this size, generate simulation anyway ?");
                Optional<ButtonType> button = alert.showAndWait();
                if (button.get() == ButtonType.OK) {
                    Application.switchToGameScreen();
                    Application.initGame();
                }
            }
            else if(Application.options.getInt(OptionKey.MAP_HEIGHT)*Application.options.getInt(OptionKey.MAP_WIDTH) > 1000000) {
                alert.setContentText("Warning : the size of the map may be too high. This could cause generation issues, generate simulation anyway ?");
                Optional<ButtonType> button = alert.showAndWait();
                if (button.get() == ButtonType.OK) {
                    Application.switchToGameScreen();
                    Application.initGame();
                }
            }
            else if(Application.options.getInt(OptionKey.ANT_HILL_COUNT) * Application.options.getInt(OptionKey.SOLDIER_PER_QUEEN) * Application.options.getInt(OptionKey.WORKER_PER_SOLDIER) > WARN_NB_ANTS) {
                alert.setContentText("Warning : the number of ants may be too high, this may result in latency or crash in the simulation, continue anyway ?");
                Optional<ButtonType> button = alert.showAndWait();
                if (button.get() == ButtonType.OK) {
                    Application.switchToGameScreen();
                    Application.initGame();
                }
            }
            else
            {
                Application.switchToGameScreen();
                Application.initGame();
            }
        });
        Button back = new Button("Back");
        back.setOnAction(e->{
            Application.switchToMenuScreen();
        });
        HBox buttonPane = new HBox();
        buttonPane.setAlignment(Pos.CENTER);
        buttonPane.setSpacing(50);
        buttonPane.getChildren().add(start);
        buttonPane.getChildren().add(back);
        box.getChildren().add(buttonPane);
    }

    private void addIntInput(VBox box, OptionKey key)
    {
        HBox field = new HBox();
        field.setSpacing(10);
        field.setAlignment(Pos.CENTER);
        field.getChildren().add(new Label(key.getLabel()+" : "));
        TextField input = new TextField();
        input.setText(Application.options.getInt(key)+"");
        input.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                input.setText(newValue.replaceAll("[^\\d]", ""));
            }
            Application.options.set(key, Integer.parseInt(input.getText()));
        });
        field.getChildren().add(input);
        box.getChildren().add(field);
    }

    private void addBoolInput(VBox box, OptionKey key)
    {
        HBox field = new HBox();
        field.setSpacing(10);
        field.setAlignment(Pos.CENTER);
        field.getChildren().add(new Label(key.getLabel()+" : "));
        CheckBox input = new CheckBox();
        input.setSelected(Application.options.getBoolean(key));
        input.selectedProperty().addListener((observable, oldValue, newValue) -> {
            Application.options.set(key, newValue);
            System.out.println(key.getLabel()+" : "+Application.options.getBoolean(key));
        });
        field.getChildren().add(input);
        box.getChildren().add(field);
    }

}
