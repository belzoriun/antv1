package fr.florian.ants.antv1.living.ant;

import fr.florian.ants.antv1.ui.Application;
import fr.florian.ants.antv1.ui.WorldView;
import fr.florian.ants.antv1.util.Drawable;
import fr.florian.ants.antv1.util.ImageColorer;
import fr.florian.ants.antv1.util.ResourceLoader;
import fr.florian.ants.antv1.util.Vector;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

import java.util.Random;

public class DeadAnt implements Drawable {
    private Color color;
    private Vector position;
    private double rotation;
    private double size;

    public DeadAnt(Vector position, Color color, double size)
    {
        this.position = position;

        this.position.setX(this.position.getX()+(Application.random.nextDouble(-0.3, 0.3)*WorldView.TILE_SIZE));
        this.position.setY(this.position.getY()+(Application.random.nextDouble(-0.3, 0.3)*WorldView.TILE_SIZE));
        this.color=color;
        this.size = size;
        this.rotation = Application.random.nextDouble(360);
        ResourceLoader.getInstance().saveResource("dead_ant"+color.getRed()+":"+color.getGreen()+":"+color.getBlue(),
                ImageColorer.fade(ImageColorer.colorAntImage(ResourceLoader.getInstance().loadResource(ResourceLoader.DEAD_ANT), color)
                , 0.6));
    }

    @Override
    public void draw(GraphicsContext context, Vector position) {
        double dotSize = WorldView.TILE_SIZE / (Ant.MAX_SIZE + 1 - size);
        Image i = ResourceLoader.getInstance().loadResource("dead_ant"+color.getRed()+":"+color.getGreen()+":"+color.getBlue());
        Vector center = position.add(WorldView.TILE_SIZE / 2-dotSize/2);
        WorldView.drawRotatedImage(context, i, center, rotation, dotSize);
    }

    public Vector getPosition() {
        return position;
    }
}
