package fr.florian.ants.antv1.util;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.shape.Shape;

public interface Drawable {
    void draw(GraphicsContext context, Vector position);
}
