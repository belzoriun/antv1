package fr.florian.ants.antv1.ui;

import fr.florian.ants.antv1.util.GameTimer;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

/**
 * Class used to display timer
 */
public class TimeDisplay extends Label {

    public TimeDisplay()
    {
        setFont(new Font(30));
        setTextFill(new Color(59/255.0, 59/255.0, 59/255.0, 0.9));
    }

    public void update()
    {
        setText(getTime());
    }

    private String getTime()
    {
        long milliseconds = (long) (GameTimer.getInstance().getRemainingTime());
        long s = milliseconds/1000 % 60;
        long m = (milliseconds / 60000) % 60;
        long h = (milliseconds / (60000 * 60)) % 24;
        return String.format("%02d:%02d:%02d.%03d", h,m,s,milliseconds%1000);
    }
}
