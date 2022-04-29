package fr.florian.ants.antv1.ui;

import fr.florian.ants.antv1.living.ant.WorkerAnt;
import fr.florian.ants.antv1.map.Map;
import fr.florian.ants.antv1.util.GameTimer;
import fr.florian.ants.antv1.util.PheromoneManager;
import fr.florian.ants.antv1.util.Vector;
import fr.florian.ants.antv1.util.resource.*;
import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;
import java.util.List;

public class Application extends javafx.application.Application {

    private static boolean executing;
    private static PauseMenu menu;
    private static Stage stage;
    private static MainPane main;

    private static AnimationTimer refreshHandler;

    private static final IResourcePlacer placer = new RandomResourcePlacer(List.of(new BasicResource(),
            new RareResource(),
            new ExtremelyRareResource()));

    public static boolean isExecuting()
    {
        return executing;
    }

    public static void restart() {
        System.out.println("restarting ...");
        refreshHandler.stop();
        executing = false;
        Map.getInstance().killAll();
        try {
            PheromoneManager.getInstance().join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        PheromoneManager.forceInit();
        GameTimer.getInstance().stopTime();
        try {
            GameTimer.getInstance().join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Map.anihilate();
        initGame();
    }

    private static void initGame()
    {
        executing = true;
        Map.getInstance().init(placer);
        System.out.println("initialized map");
        GameTimer.init(600000);//100 minute
        GameTimer.getInstance().start();
        System.out.println("initialized timer");
        refreshHandler = new AnimationTimer()
        {
            @Override
            public void handle(long currentNanoTime)
            {
                synchronized (Map.getInstance()) {
                    if(Map.getInstance().updateLivings() <= 0)
                    {
                        System.out.println("all ants got killed ...");
                        menu.setEndMenu();
                        menu.pauseGame();
                    }
                }
                main.displayAll();
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) { e.printStackTrace();}
            }
        };
        refreshHandler.start();
    }

    @Override
    public void start(Stage stage) throws IOException {
        Application.stage = stage;
        Group root = new Group();
        Scene scene = new Scene(root);
        main = new MainPane();
        initGame();
        root.getChildren().add(main);
        stage.setTitle("Battle Ants!");
        stage.setScene(scene);
        stage.show();
        menu = new PauseMenu(stage);
        scene.addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent evt) {
                if (evt.getCode().equals(KeyCode.ESCAPE)) {
                    if(GameTimer.getInstance().isPaused())
                    {
                        menu.playGame();
                    }
                    else {
                        menu.pauseGame();
                    }
                }
            }
        });
        stage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);
        stage.setFullScreen(true);
        stage.setResizable(false);
    }

    public static void endGame()
    {
        executing = false;
        Map.getInstance().killAll();
        try {
            PheromoneManager.getInstance().join();
        } catch (InterruptedException e) {
        }
        Platform.exit();
        System.exit(0);
    }

    public static void showEndMenu()
    {
        menu.setEndMenu();
        menu.pauseGame();
    }

    public static void main(String[] args) {
        launch();
        endGame();
    }
}