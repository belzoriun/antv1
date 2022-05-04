package fr.florian.ants.antv1.ui;

import fr.florian.ants.antv1.map.Map;
import fr.florian.ants.antv1.util.GameTimer;
import fr.florian.ants.antv1.util.TickAwaiter;
import fr.florian.ants.antv1.util.option.Options;
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
import java.util.Random;

public class Application extends javafx.application.Application {

    public static Stage stage;
    private static MainPane main;
    private static StartMenu menu;
    public static Options options;

    public static Random random;

    public static void restart() {
        System.out.println("restarting ...");
        main.exit();
        initGame();
    }

    public static void initGame()
    {
        long seed = 0L;
        random = new Random(seed);
        options = new Options();
        TickAwaiter.lock();
        PheromoneManager.forceInit();
        Map.getInstance().init(new NoiseRessourcePlacer(seed, List.of(new BasicResource(null),
                new RareResource(null),
                new ExtremelyRareResource(null))));
        System.out.println("initialized map");
        GameTimer.init(2*60000);//2 minute
        GameTimer.getInstance().start();
        System.out.println("initialized timer");
        main.init();
    }

    @Override
    public void start(Stage stage) throws IOException {
        main = new MainPane();
        menu = new StartMenu();
        Scene scene = new Scene(menu);
        stage.setScene(scene);
        Application.stage = stage;
        scene.getRoot().requestFocus();
        stage.setTitle("Battle Ants!");
        stage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);
        stage.setFullScreen(true);
        stage.setResizable(false);
        stage.show();
    }

    public static void switchToGameScreen()
    {
        stage.getScene().setRoot(main);
    }

    public static void switchToMenuScreen()
    {
        main.exit();
        stage.getScene().setRoot(menu);
    }

    public static void endGame()
    {
        if(stage.getScene().getRoot() == main)
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