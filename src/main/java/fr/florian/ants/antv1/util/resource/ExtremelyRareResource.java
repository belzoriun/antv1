package fr.florian.ants.antv1.util.resource;

import fr.florian.ants.antv1.ui.MainPane;
import fr.florian.ants.antv1.util.Vector;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class ExtremelyRareResource extends Resource{
    public ExtremelyRareResource() {
        super(6, 0.99);
    }

    @Override
    public void draw(GraphicsContext context, Vector position) {
        double dotSize = MainPane.TILE_SIZE/4;
        context.setFill(Color.BLUEVIOLET);
        context.fillOval(position.getX(), position.getY(), dotSize, dotSize);
    }

    @Override
    public Resource clone() {
        return new ExtremelyRareResource();
    }
}
