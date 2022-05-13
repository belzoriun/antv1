package fr.florian.ants.antv1.ui;

import fr.florian.ants.antv1.map.ChunkUpdater;
import fr.florian.ants.antv1.map.Map;
import fr.florian.ants.antv1.util.GameTimer;
import fr.florian.ants.antv1.util.TickWaiter;
import fr.florian.ants.antv1.util.mod.ModLoader;
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
    public static MainPane main;
    public static StartMenu menu;
    private static SimulationOptionsMenu optionMenu;
    public static Options options;
    public static long seed = 0L;

    private static DisplayStartController controller;

    public static Random random;

    /**
     * Restarts the whole application
     */
    public static void restart() {
        controller.emitAsync(DisplayStartController.StartEvent.RESTART);
    }

    /**
     * Init the application's core
     */
    public static void initGame()
    {
        controller.emitAsync(DisplayStartController.StartEvent.INIT_GAME);
    }

    public static void showLoadingScreen(String text)
    {
        if(stage.getScene().getRoot() != LoadMenu.getInstance())
            stage.getScene().setRoot(LoadMenu.getInstance());
        LoadMenu.getInstance().show(text);
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
        LoadMenu.getInstance().hide();
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
        options.load();
        optionMenu.init();
        stage.getScene().setRoot(optionMenu);
    }

    public static void endGame()
    {
        if(stage.getScene().getRoot() == main)
            main.exit();
        controller.emit(DisplayStartController.StartEvent.END);
    }

    public static void showEndMenu()
    {
        main.showEnd();
    }

    public static void main(String[] args) {
        controller = new DisplayStartController();
        controller.start();
        launch();
        endGame();
        controller.terminate();
        Platform.exit();
        System.exit(0);
    }
}