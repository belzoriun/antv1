package fr.florian.ants.antv1.ui;

import fr.florian.ants.antv1.map.Map;
import fr.florian.ants.antv1.util.GameTimer;
import fr.florian.ants.antv1.util.TickAwaiter;
import fr.florian.ants.antv1.util.pheromone.Pheromone;
import fr.florian.ants.antv1.util.pheromone.PheromoneManager;
import javafx.animation.AnimationTimer;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.transform.Translate;

public class MainPane extends BorderPane {

    private PauseMenu menu;
    private WorldView worldView;
    private AnimationTimer refreshHandler;
    private EventHandler<KeyEvent> keyHandler;
    private DataDisplay data;

    public MainPane()
    {
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
                update();
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) { e.printStackTrace();}
            }
        };
        keyHandler = evt -> {
            if (evt.getCode().equals(KeyCode.ESCAPE)) {
                if(GameTimer.getInstance().isPaused())
                {
                    menu.playGame();
                }
                else {
                    menu.pauseGame();
                }
            }
            if (evt.getCode().equals(KeyCode.TAB)) {
                worldView.toNextDisplay();
            }
        };
    }

    public void exit()
    {
        TickAwaiter.free();
        System.out.print("killing ants ...");
        long time = System.currentTimeMillis();
        Map.getInstance().killAll();
        System.out.println(" "+(System.currentTimeMillis()-time)+"ms");
        System.out.print("Stopping screen refresh ...");
        time = System.currentTimeMillis();
        refreshHandler.stop();
        System.out.println(" "+(System.currentTimeMillis()-time)+"ms");
        Application.stage.getScene().removeEventFilter(KeyEvent.KEY_RELEASED, keyHandler);
        try {
            System.out.print("Stopping pheromone manager ...");
            time = System.currentTimeMillis();
            PheromoneManager.getInstance().stopExecution();
            PheromoneManager.getInstance().join();
            System.out.println(" "+(System.currentTimeMillis()-time)+"ms");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        this.getChildren().remove(time);
        System.out.print("Stopping timer ...");
        time = System.currentTimeMillis();
        GameTimer.getInstance().stopTime();
        try {
            GameTimer.getInstance().join();
            System.out.println(" "+(System.currentTimeMillis()-time)+"ms");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Map.anihilate();
    }

    private void update()
    {
        worldView.displayAll();
        data.update();
        if(GameTimer.getInstance().getRemainingTime() <= 0)
        {
            Application.showEndMenu();
        }
    }

    public void showEnd()
    {
        menu.setEndMenu();
        menu.pauseGame();
    }

    public void init() {
        this.worldView = new WorldView();
        data = new DataDisplay(worldView);
        data.setBackground(new Background(new BackgroundFill(Color.WHITE, null, null)));
        setRight(data);
        data.setPadding(new Insets(10));
        worldView.bindWidth(Application.stage.widthProperty().subtract(data.widthProperty()));
        setCenter(worldView);
        refreshHandler.start();
        this.menu = new PauseMenu(Application.stage);
        Application.stage.getScene().addEventFilter(KeyEvent.KEY_RELEASED, keyHandler);
    }
}
