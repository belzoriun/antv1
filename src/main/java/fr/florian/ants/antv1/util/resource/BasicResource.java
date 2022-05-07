package fr.florian.ants.antv1.util.resource;

import fr.florian.ants.antv1.ui.Application;
import fr.florian.ants.antv1.ui.WorldView;
import fr.florian.ants.antv1.util.ResourceLoader;
import fr.florian.ants.antv1.util.Vector;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

/**
 * Class representing a basic resource
 */
public class BasicResource extends Resource{

    public BasicResource(Vector position) {
        super(position,1, 0);
    }

    @Override
    public void draw(GraphicsContext context, Vector position) {
        double dotSize = WorldView.TILE_SIZE/5;
        Image i = ResourceLoader.getInstance().loadResource(ResourceLoader.PEA);
        WorldView.drawRotatedImage(context, i, position, rotation, dotSize);
    }

    @Override
    public Resource clone(Vector v) {
        return new BasicResource(v);
    }
}
