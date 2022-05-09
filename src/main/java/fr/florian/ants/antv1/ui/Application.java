package fr.florian.ants.antv1.ui;

import fr.florian.ants.antv1.map.Map;
import fr.florian.ants.antv1.util.GameTimer;
import fr.florian.ants.antv1.util.TickWaiter;
import fr.florian.ants.antv1.util.option.OptionKey;
import fr.florian.ants.antv1.util.option.Options;
import fr.florian.ants.antv1.util.pheromone.PheromoneManager;
import fr.florian.ants.antv1.util.resource.*;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.input.KeyCombination;
import javafx.stage.Stage;

import java.util.List;
import java.util.Random;

/**
 * Main class of simulation
 */
public class Application extends javafx.application.Application {

    public static Stage stage;
    private static MainPane main;
    private static StartMenu menu;
    private static SimulationOptionsMenu optionMenu;
    public static Options options;

    public static Random random;

    /**
     * Restarts the whole application
     */
    public static void restart() {
        System.out.println("restarting ...");
        main.exit();
        switchToOptionScreen();
    }

    /**
     * Init the application's core
     */
    public static void initGame()
    {
        long seed = 0L;
        random = new Random(seed);
        TickWaiter.lock();
        PheromoneManager.forceInit();
        Map.getInstance().init(new NoiseResourcePlacer(seed, List.of(new BasicResource(null),
                new RareResource(null),
                new ExtremelyRareResource(null),
                new FoodResource(null))));
        System.out.println("initialized map");
        GameTimer.init(options.getInt(OptionKey.SIMULATION_TIME)* 60000L);//2 minute
        GameTimer.getInstance().start();
        System.out.println("initialized timer");
        main.init();
    }

    @Override
    public void start(Stage stage) {
        options = new Options();
        main = new MainPane();
        menu = new StartMenu();
        optionMenu = new SimulationOptionsMenu();
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
        if(stage.getScene().getRoot() == main)
            main.exit();
        stage.getScene().setRoot(menu);
    }

    public static void switchToOptionScreen()
    {
        stage.getScene().setRoot(optionMenu);
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