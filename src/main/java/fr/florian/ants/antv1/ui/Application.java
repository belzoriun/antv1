package fr.florian.ants.antv1.ui;

import fr.florian.ants.antv1.map.Map;
import fr.florian.ants.antv1.util.GameTimer;
import fr.florian.ants.antv1.util.pheromone.PheromoneManager;
import fr.florian.ants.antv1.util.resource.*;
import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

public class Application extends javafx.application.Application {

    public static Stage stage;
    private static MainPane main;

    private static final IResourcePlacer placer = new RandomResourcePlacer(List.of(new BasicResource(null),
            new RareResource(null),
            new ExtremelyRareResource(null)));

    public static void restart() {
        System.out.println("restarting ...");
        main.exit();
        initGame();
    }

    private static void initGame()
    {
        Map.getInstance().init(placer);
        System.out.println("initialized map");
        GameTimer.init(120*1000);//2 minute
        GameTimer.getInstance().start();
        System.out.println("initialized timer");
        main.init();
    }

    @Override
    public void start(Stage stage) throws IOException {
        main = new MainPane();
        Scene scene = new Scene(main);
        stage.setScene(scene);
        Application.stage = stage;
        initGame();
        scene.getRoot().requestFocus();
        stage.setTitle("Battle Ants!");
        stage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);
        stage.setFullScreen(true);
        stage.setResizable(false);
        stage.show();
    }

    public static void endGame()
    {
        main.exit();
        Platform.exit();
        System.exit(0);
    }

    public static void showEndMenu()
    {
        main.showEnd();
    }

    public static void main(String[] args) {
        launch();
        endGame();
    }
}