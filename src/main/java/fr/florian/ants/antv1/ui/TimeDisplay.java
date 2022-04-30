package fr.florian.ants.antv1.ui;

import fr.florian.ants.antv1.util.GameTimer;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class TimeDisplay extends Pane {

    private Label text;

    public TimeDisplay()
    {
        text = new Label(getTime());
        text.setFont(new Font(30));
        text.setPadding(new Insets(10));
        this.getChildren().add(text);
    }

    public void update()
    {
        text.setText(getTime());
    }

    private String getTime()
    {
        long milis = (long) (GameTimer.getInstance().getRemainingTime());
        long s = milis/1000 % 60;
        long m = (milis / 60000) % 60;
        long h = (milis / (60000 * 60)) % 24;
        return String.format("%02d:%02d:%02d.%03d", h,m,s,milis%1000);
    }
}
