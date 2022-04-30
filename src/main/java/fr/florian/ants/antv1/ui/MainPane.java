package fr.florian.ants.antv1.ui;

import fr.florian.ants.antv1.map.Map;
import fr.florian.ants.antv1.util.GameTimer;
import fr.florian.ants.antv1.util.TickAwaiter;
import fr.florian.ants.antv1.util.pheromone.Pheromone;
import fr.florian.ants.antv1.util.pheromone.PheromoneManager;
import javafx.animation.AnimationTimer;
import javafx.event.EventHandler;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;

public class MainPane extends Pane {

    private PauseMenu menu;
    private WorldView worldView;
    private TimeDisplay time;
    private AnimationTimer refreshHandler;
    private EventHandler<KeyEvent> keyHandler;

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
        Map.getInstance().killAll();
        refreshHandler.stop();
        Application.stage.getScene().removeEventFilter(KeyEvent.KEY_PRESSED, keyHandler);
        try {
            PheromoneManager.getInstance().stopExecution();
            TickAwaiter.emitTick();
            PheromoneManager.getInstance().join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        this.getChildren().remove(time);
        PheromoneManager.forceInit();
        GameTimer.getInstance().stopTime();
        try {
            GameTimer.getInstance().join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Map.anihilate();
    }

    private void update()
    {
        worldView.displayAll();
        time.update();
    }

    public void showEnd()
    {
        menu.setEndMenu();
        menu.pauseGame();
    }

    public void init() {
        this.worldView = new WorldView();
        this.getChildren().add(worldView);
        refreshHandler.start();
        this.menu = new PauseMenu(Application.stage);
        Application.stage.getScene().addEventFilter(KeyEvent.KEY_PRESSED, keyHandler);
        time = new TimeDisplay();
        this.getChildren().add(time);
    }
}
