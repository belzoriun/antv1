package fr.florian.ants.antv1.living.ant;

import fr.florian.ants.antv1.living.Living;
import fr.florian.ants.antv1.ui.MainPane;
import fr.florian.ants.antv1.util.Vector;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public abstract class Ant extends Living{

    private double size;

    protected Ant(Vector ipos, double size) {
        super(ipos,50);
        if(size <= 0) size = 1;
        if(size > 10) size = 10;
        this.size = size;
    }

    @Override
    public void draw(GraphicsContext context, Vector position)
    {
        double dotSize = MainPane.TILE_SIZE/(11-size);
        Vector center = position.mult(MainPane.TILE_SIZE).add(MainPane.TILE_SIZE/2-dotSize/2);
        context.setFill(Color.BLACK);
        context.fillOval(center.getX(), center.getY(), dotSize, dotSize);
    }
}
