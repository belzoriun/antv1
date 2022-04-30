package fr.florian.ants.antv1.util.pheromone;

import fr.florian.ants.antv1.living.ant.Ant;
import fr.florian.ants.antv1.ui.WorldView;
import fr.florian.ants.antv1.util.Vector;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public abstract class Pheromone {
    private Color color;

    protected Pheromone(Color color)
    {
        this.color = color;
    }

    public abstract void onDetect(Ant a);

    public void draw(GraphicsContext context, Vector position)
    {
        Color base = new Color(color.getRed(), color.getGreen(), color.getBlue(), 0.1);
        context.setFill(base);
        context.fillRect(position.getX() * WorldView.TILE_SIZE, position.getY() * WorldView.TILE_SIZE, WorldView.TILE_SIZE, WorldView.TILE_SIZE);
    }
}
