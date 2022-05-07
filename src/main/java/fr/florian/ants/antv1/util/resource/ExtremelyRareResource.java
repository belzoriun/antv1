package fr.florian.ants.antv1.util.resource;

import fr.florian.ants.antv1.ui.WorldView;
import fr.florian.ants.antv1.util.ResourceLoader;
import fr.florian.ants.antv1.util.Vector;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

/**
 * Class representing an extremely rare resource
 */
public class ExtremelyRareResource extends Resource{

    public ExtremelyRareResource(Vector position) {
        super(position, 6, 0.96);
    }

    @Override
    public void draw(GraphicsContext context, Vector position) {
        double dotSize = WorldView.TILE_SIZE/3;
        Image i = ResourceLoader.getInstance().loadResource(ResourceLoader.SUGAR);
        WorldView.drawRotatedImage(context, i, position, rotation, dotSize);
    }

    @Override
    public Resource clone(Vector v) {
        return new ExtremelyRareResource(v);
    }
}
