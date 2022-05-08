package fr.florian.ants.antv1.ui;

import fr.florian.ants.antv1.map.AntHillTile;
import fr.florian.ants.antv1.map.Map;
import fr.florian.ants.antv1.util.ImageColorMaker;
import fr.florian.ants.antv1.util.ResourceLoader;
import fr.florian.ants.antv1.util.Vector;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

public class ArrowDisplay {

    private final AntHillTile hill;
    private final MarkerManager manager;
    private final Canvas canvas;
    private final double arrowSize;
    private Vector position;
    private boolean displayed;

    public ArrowDisplay(AntHillTile tile, MarkerManager manager, Canvas canvas)
    {
        arrowSize = 30;
        this.hill = tile;
        this.manager = manager;
        this.canvas = canvas;
        displayed = true;
        position = new Vector(0, 0);
        ResourceLoader.getInstance().saveResource(
            "arrow"+hill.getColor().getRed()+":"+hill.getColor().getGreen()+":"+hill.getColor().getBlue(),
            ImageColorMaker.colorAntImage(ResourceLoader.getInstance().loadResource(ResourceLoader.ARROW), hill.getColor()));
    }

    public boolean inBounds(Vector pos)
    {
        return pos.getX()>=position.getX()-arrowSize/2 && pos.getX() <= position.getX()+arrowSize/2
                && pos.getY()>=position.getY()-arrowSize/2 && pos.getY() <= position.getY()+arrowSize/2 && displayed;
    }

    public void update(GraphicsContext context)
    {
        Vector tilePos = Map.getInstance().getTilePosition(hill);
        Vector position = manager.toWorldPoint(tilePos).multi(WorldView.TILE_SIZE).add(WorldView.TILE_SIZE/2);
        if(position.getX() < 0 || position.getX() > canvas.widthProperty().get() - arrowSize / 2
                || position.getY() < 0 || position.getY() > canvas.heightProperty().get() - arrowSize / 2) {
            Image i = ResourceLoader.getInstance().loadResource("arrow" + hill.getColor().getRed() + ":" + hill.getColor().getGreen() + ":" + hill.getColor().getBlue());

            if (position.getX() > canvas.widthProperty().get() - arrowSize / 2) {
                double dist = position.getX() - canvas.widthProperty().get();
                position = position.add(new Vector(-dist - arrowSize / 1.5, 0));
            }
            if (position.getY() > canvas.heightProperty().get() - arrowSize / 2) {
                double dist = position.getY() - canvas.heightProperty().get();
                position = position.add(new Vector(0, -dist - arrowSize / 1.5));
            }
            if (position.getX() < arrowSize/2 ) {
                position = new Vector(arrowSize/2, position.getY());
            }
            if (position.getY() < arrowSize/2) {
                position = new Vector(position.getX(), arrowSize/2);
            }
            this.position = position;
            Vector center = new Vector(
                    (int) ((canvas.widthProperty().get() / 2 - manager.getOriginX() * WorldView.TILE_SIZE) / WorldView.TILE_SIZE),
                    (int) ((canvas.heightProperty().get() / 2 - manager.getOriginY() * WorldView.TILE_SIZE) / WorldView.TILE_SIZE));
            Vector vector = new Vector(tilePos.getX() - center.getX(), tilePos.getY() - center.getY());
            Vector unit = new Vector(1, 0);
            double add = 0;

            if (center.getY() < tilePos.getY()) {
                vector = new Vector(center.getX() - tilePos.getX(), center.getY() - tilePos.getY());
                add = 180;
            }

            double distance = position.multi(-1/WorldView.TILE_SIZE).add(manager.toWorldPoint(tilePos)).magnitude();

            double angle = unit.angle(vector) + add;
            WorldView.drawRotatedImage(context, i, position, angle, arrowSize);
            context.setFill(Color.BLACK);
            Vector textPosition = position.add(new Vector(-arrowSize/2, arrowSize));
            if(position.getX() <= arrowSize)
            {
                textPosition = textPosition.add(new Vector(arrowSize/2, 0));
            }
            else
            {
                textPosition = textPosition.add(new Vector(-arrowSize/2, 0));
            }
            if(position.getY() > arrowSize/2)
            {
                textPosition = textPosition.add(new Vector(0, -arrowSize*1.5));
            }
            context.fillText((Math.round(distance*100)/100)+" tiles", textPosition.getX(), textPosition.getY());
            displayed = true;
        }
        else
        {
            displayed = false;
        }
    }
}
