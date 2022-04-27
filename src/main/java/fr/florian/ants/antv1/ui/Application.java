package fr.florian.ants.antv1.ui;

import fr.florian.ants.antv1.living.ant.WorkerAnt;
import fr.florian.ants.antv1.map.Map;
import fr.florian.ants.antv1.util.GameTimer;
import fr.florian.ants.antv1.util.PheromoneManager;
import fr.florian.ants.antv1.util.Vector;
import fr.florian.ants.antv1.util.resource.BasicResource;
import fr.florian.ants.antv1.util.resource.ExtremelyRareResource;
import fr.florian.ants.antv1.util.resource.RandomResourcePlacer;
import fr.florian.ants.antv1.util.resource.RareResource;
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

    public static boolean isExecuting()
    {
        return executing;
    }

    @Override
    public void start(Stage stage) throws IOException {
        executing = true;
        GameTimer.init(6000);//1 minute
        GameTimer.getInstance().start();
        Map.getInstance().init(new RandomResourcePlacer(List.of(new BasicResource(),
                new RareResource(),
                new ExtremelyRareResource())));
        Group root = new Group();
        Scene scene = new Scene(root);
        MainPane main = new MainPane();
        root.getChildren().add(main);
        stage.setTitle("Battle Ants!");
        stage.setScene(scene);
        main.displayAll();
        new AnimationTimer()
        {
            @Override
            public void handle(long currentNanoTime)
            {
                main.displayAll();
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) { e.printStackTrace();}
            }
        }.start();
        stage.show();
        menu = new PauseMenu(scene.getWindow());
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
        stage.setOnCloseRequest((WindowEvent e)->{
            endGame();
        });
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
    }
}