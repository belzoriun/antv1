package fr.florian.ants.antv1.living.ant;

import fr.florian.ants.antv1.ui.MainPane;
import fr.florian.ants.antv1.util.Vector;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class QueenAnt extends Ant{

    protected QueenAnt(long anthillId, Color color, Vector ipos) {
        super(anthillId, color, ipos, 9);
    }

    @Override
    protected void executeAction() {

    }
}
