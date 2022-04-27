package fr.florian.ants.antv1.util.resource;

import fr.florian.ants.antv1.ui.MainPane;
import fr.florian.ants.antv1.util.Vector;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class BasicResource extends Resource{
    protected BasicResource() {
        super(1);
    }

    @Override
    public void draw(GraphicsContext context, Vector position) {
        double dotSize = MainPane.TILE_SIZE/4;
        context.setFill(Color.GREENYELLOW);
        context.fillOval(position.getX(), position.getY(), dotSize, dotSize);
    }
}
