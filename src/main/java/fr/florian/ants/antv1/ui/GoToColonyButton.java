package fr.florian.ants.antv1.ui;

import fr.florian.ants.antv1.util.Vector;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;

public class GoToColonyButton extends Button {
    private final Vector pos;
    WorldView view;

    public GoToColonyButton(WorldView view, Vector pos, String text)
    {
        super(text);
        this.pos = pos;
        this.view = view;
        this.setOnMouseClicked((MouseEvent e)->{
            this.view.goTo(this.pos);
        });
    }
}
