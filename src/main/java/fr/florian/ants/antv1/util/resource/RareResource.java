package fr.florian.ants.antv1.util.resource;

import fr.florian.ants.antv1.ui.Application;
import fr.florian.ants.antv1.ui.WorldView;
import fr.florian.ants.antv1.util.ResourceLoader;
import fr.florian.ants.antv1.util.Vector;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

/**
 * Class representing a rare resource
 */
public class RareResource extends Resource{
    private final int seedType;

    public RareResource(Vector position) {
        super(position,3, 0.8);
        seedType = Application.random.nextInt(5)+1;
    }

    @Override
    public void draw(GraphicsContext context, Vector position) {
        double dotSize = WorldView.TILE_SIZE/4;
        String resource = switch (seedType) {
            case 1 -> ResourceLoader.SEED_1;
            case 2 -> ResourceLoader.SEED_2;
            case 3 -> ResourceLoader.SEED_3;
            case 4 -> ResourceLoader.SEED_4;
            default -> ResourceLoader.SEED_5;
        };
        Image i = ResourceLoader.getInstance().loadResource(resource);
        WorldView.drawRotatedImage(context, i, position, rotation, dotSize);
    }

    @Override
    public Resource clone(Vector v) {
        return new RareResource(v);
    }
}
