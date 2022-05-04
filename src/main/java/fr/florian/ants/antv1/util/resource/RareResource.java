package fr.florian.ants.antv1.util.resource;

import fr.florian.ants.antv1.ui.Application;
import fr.florian.ants.antv1.ui.WorldView;
import fr.florian.ants.antv1.util.ResourceLoader;
import fr.florian.ants.antv1.util.Vector;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

import java.util.Random;

public class RareResource extends Resource{
    private double rotation;
    private int seedType;

    public RareResource(Vector position) {
        super(position,3, 0.8);
        rotation = Application.random.nextDouble(360);
        seedType = Application.random.nextInt(5)+1;
    }

    @Override
    public void draw(GraphicsContext context, Vector position) {
        double dotSize = WorldView.TILE_SIZE/4;
        String ressource = "";
        switch(seedType)
        {
            case 1: ressource = ResourceLoader.SEED_1;
                break;
            case 2: ressource = ResourceLoader.SEED_2;
                break;
            case 3: ressource = ResourceLoader.SEED_3;
                break;
            case 4: ressource = ResourceLoader.SEED_4;
                break;
            default:
            case 5: ressource = ResourceLoader.SEED_5;
                break;
        }
        Image i = ResourceLoader.getInstance().loadResource(ressource);
        WorldView.drawRotatedImage(context, i, position, rotation, dotSize);
    }

    @Override
    public Resource clone(Vector v) {
        return new RareResource(v);
    }
}
