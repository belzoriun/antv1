package fr.florian.ants.antv1.living.ant;

import fr.florian.ants.antv1.living.Living;
import fr.florian.ants.antv1.ui.MainPane;
import fr.florian.ants.antv1.util.Vector;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public abstract class Ant extends Living{

    private static final int MAX_SIZE = 10;

    private double size;
    protected long uniqueAnthillId;

    private Color color;

    protected Ant(long anthillId, Color color, Vector ipos, double size) {
        super(ipos,50);
        if(size <= 0) size = 1;
        if(size > MAX_SIZE) size = MAX_SIZE;
        this.size = size;
        this.uniqueAnthillId = anthillId;
        this.color = color;
    }

    @Override
    public void draw(GraphicsContext context, Vector position)
    {
        double dotSize = MainPane.TILE_SIZE/(MAX_SIZE+1-size);
        Vector center = position.mult(MainPane.TILE_SIZE).add(MainPane.TILE_SIZE/2-dotSize/2);
        context.setFill(color);
        context.fillOval(center.getX(), center.getY(), dotSize, dotSize);
    }
}
