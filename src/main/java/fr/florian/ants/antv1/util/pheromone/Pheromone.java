package fr.florian.ants.antv1.util.pheromone;

import fr.florian.ants.antv1.ui.WorldView;
import fr.florian.ants.antv1.util.Vector;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.Objects;

/**
 * Class representing basic pheromone behavior
 */
public abstract class Pheromone {
    private final Color color;
    private int weight;
    private final int lifetime;

    protected Pheromone(int lifetime, Color color)
    {
        this.color = color;
        weight = 0;
        this.lifetime = lifetime;
    }

    public int getLifetime()
    {
        return lifetime;
    }

    public void draw(GraphicsContext context, Vector position)
    {
        double a = weight/10.0;
        if(a > 0.8) a = 0.8;
        Color base = new Color(color.getRed(), color.getGreen(), color.getBlue(), a);
        context.setFill(base);
        context.fillRect(position.getX(), position.getY(), WorldView.TILE_SIZE, WorldView.TILE_SIZE);
    }

    public int getWeight() {
        return weight;
    }
    public void remove()
    {
        weight--;
        if(weight <0) weight = 0;
    }

    public void add()
    {
        this.weight ++;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Pheromone pheromone = (Pheromone) o;
        return Objects.equals(color, pheromone.color);
    }
}
