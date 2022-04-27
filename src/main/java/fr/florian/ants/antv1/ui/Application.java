package fr.florian.ants.antv1.ui;

import fr.florian.ants.antv1.living.ant.WorkerAnt;
import fr.florian.ants.antv1.map.Map;
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
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

public class Application extends javafx.application.Application {

    @Override
    public void start(Stage stage) throws IOException {
        Map.getInstance().init(new RandomResourcePlacer(List.of(new BasicResource(), new RareResource(), new ExtremelyRareResource())));
        Map.getInstance().spawn(new WorkerAnt(new Vector(0, 0)));
        Group root = new Group();
        Scene scene = new Scene(root);
        MainPane main = new MainPane();
        root.getChildren().add(main);
        stage.setTitle("Hello!");
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
        scene.addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent evt) {
                if (evt.getCode().equals(KeyCode.ESCAPE)) {
                    stage.close();
                }
            }
        });
        stage.show();
        stage.setFullScreen(true);
        stage.setResizable(false);
    }

    public static void main(String[] args) {
        launch();
    }
}