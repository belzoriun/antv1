package fr.florian.ants.antv1.ui;

import javafx.animation.AnimationTimer;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;

public class LoadMenu extends BorderPane {

    private AnimationTimer updater;
    private long lastUpdate;
    private int dotNb;
    private Label text;
    private String display;
    private boolean active;

    private static LoadMenu instance = null;

    private LoadMenu()
    {
        lastUpdate = -1;
        dotNb = 0;
        display = "";
        active = false;
        updater = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if(lastUpdate == -1)
                {
                    lastUpdate = now;
                    text.setText(display);
                }
                else if(now-lastUpdate > 300000000){
                    String dots = "";
                    for (int i = 0; i < dotNb; i++) {
                        dots += " .";
                    }
                    dotNb++;
                    if (dotNb > 3) dotNb = 0;
                    text.setText(display + dots);
                    lastUpdate = now;
                }
            }
        };
        text = new Label();
        text.setTextFill(Color.WHITE);
        setCenter(text);
        text.setAlignment(Pos.CENTER);
        setBackground(new Background(new BackgroundFill(Color.BLACK, null, null)));
    }

    public static LoadMenu getInstance()
    {
        if(instance == null)
        {
            instance = new LoadMenu();
        }
        return instance;
    }

    public void hide()
    {
        lastUpdate = -1;
        if(active) {
            active = false;
            updater.stop();
        }
    }

    public void show(String text)
    {
        display = text;
        if(!active) {
            active = true;
            updater.start();
        }
    }

}
