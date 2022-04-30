package fr.florian.ants.antv1.util.resource;

import fr.florian.ants.antv1.ui.WorldView;
import fr.florian.ants.antv1.util.Vector;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class RareResource extends Resource{
    public RareResource(Vector position) {
        super(position,3, 0.8);
    }

    @Override
    public void draw(GraphicsContext context, Vector position) {
        double dotSize = WorldView.TILE_SIZE/4;
        context.setFill(Color.DEEPPINK);
        context.fillOval(position.getX(), position.getY(), dotSize, dotSize);
    }

    @Override
    public Resource clone(Vector v) {
        return new RareResource(v);
    }
}
