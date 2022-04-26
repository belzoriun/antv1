package fr.florian.ants.antv1.map;

import fr.florian.ants.antv1.living.Living;
import fr.florian.ants.antv1.living.ant.Ant;
import fr.florian.ants.antv1.ui.MainPane;
import fr.florian.ants.antv1.util.Vector;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class AntHillTile implements Tile{

    public AntHillTile()
    {

    }

    @Override
    public void onWalkOn(Living l) {

    }

    @Override
    public void onInteract(Ant a) {

    }

    @Override
    public void draw(GraphicsContext context, Vector position) {
        context.setFill(Color.BROWN);
        context.fillRect(position.getX()* MainPane.TILE_SIZE, position.getY()*MainPane.TILE_SIZE, MainPane.TILE_SIZE, MainPane.TILE_SIZE);
    }
}
