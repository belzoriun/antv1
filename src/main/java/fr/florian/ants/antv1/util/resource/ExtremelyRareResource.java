package fr.florian.ants.antv1.util.resource;

import fr.florian.ants.antv1.ui.MainPane;
import fr.florian.ants.antv1.ui.WorldView;
import fr.florian.ants.antv1.util.ResourceLoader;
import fr.florian.ants.antv1.util.Vector;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.transform.Rotate;

import java.util.Random;

public class ExtremelyRareResource extends Resource{

    private int rotation;

    public ExtremelyRareResource(Vector position) {
        super(position, 6, 0.96);
        rotation = new Random().nextInt(360);
    }

    @Override
    public void draw(GraphicsContext context, Vector position) {
        double dotSize = WorldView.TILE_SIZE/4;
        Image i = ResourceLoader.getInstance().loadResource(ResourceLoader.SUGAR);
        context.save();
        context.translate(position.getX()+dotSize/2, position.getY()+dotSize/2);
        context.rotate(rotation);
        context.drawImage(i, 0, 0, dotSize, dotSize);
        context.restore();
    }

    @Override
    public Resource clone(Vector v) {
        return new ExtremelyRareResource(v);
    }
}
