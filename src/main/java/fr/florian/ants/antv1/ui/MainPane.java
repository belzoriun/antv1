package fr.florian.ants.antv1.ui;

import fr.florian.ants.antv1.map.Map;
import fr.florian.ants.antv1.util.GameTimer;
import fr.florian.ants.antv1.util.TickWaiter;
import fr.florian.ants.antv1.util.pheromone.PheromoneManager;
import javafx.animation.AnimationTimer;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.stage.Screen;

/**
 * Main display of simulation
 */
public class MainPane extends BorderPane {

    private PauseMenu menu;
    private WorldView worldView;
    private AnimationTimer refreshHandler;
    private EventHandler<KeyEvent> keyHandler;
    private DataDisplay data;

    public MainPane()
    {
    }

    /**
     * Closes ant shutting down simulation
     */
    public void exit()
    {
        refreshHandler.stop();
        Application.stage.getScene().removeEventFilter(KeyEvent.KEY_RELEASED, keyHandler);
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

    /**
     * Set pause menu to be the "end menu"
     */
    public void showEnd()
    {
        menu.setEndMenu();
        menu.pauseGame();
    }

    /**
     * Init main display
     */
    public void init() {
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
                if(GameTimer.getInstance().getRemainingTime() <= 0)
                {
                    refreshHandler.stop();
                }
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
        this.worldView = new WorldView();
        data = new DataDisplay(worldView);
        data.setBackground(new Background(new BackgroundFill(Color.WHITE, null, null)));
        setRight(data);
        data.setPadding(new Insets(10));
        worldView.bindWidth(Application.stage.widthProperty().subtract(data.widthProperty()));
        worldView.init();
        setCenter(worldView);
        refreshHandler.start();
        this.menu = new PauseMenu(Screen.getPrimary().getBounds().getWidth()/2, Screen.getPrimary().getBounds().getHeight()/2);
        worldView.getChildren().add(menu);
        Application.stage.getScene().addEventFilter(KeyEvent.KEY_RELEASED, keyHandler);
    }
}
