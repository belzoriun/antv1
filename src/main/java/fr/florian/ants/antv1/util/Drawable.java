package fr.florian.ants.antv1.util;

import javafx.scene.canvas.GraphicsContext;

/**
 * Interface used to specify an element can be drawn on screen
 */
public interface Drawable {
    void draw(GraphicsContext context, Vector position);
}
