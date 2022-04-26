package fr.florian.ants.antv1.ui;

import fr.florian.ants.antv1.map.Map;
import fr.florian.ants.antv1.util.resource.RandomResourcePlacer;
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

public class Application extends javafx.application.Application {

    @Override
    public void start(Stage stage) throws IOException {
        Map.getInstance().init(new RandomResourcePlacer());
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