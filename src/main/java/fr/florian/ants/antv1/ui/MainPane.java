package fr.florian.ants.antv1.ui;

import fr.florian.ants.antv1.living.Living;
import fr.florian.ants.antv1.map.Map;
import fr.florian.ants.antv1.util.Vector;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.*;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.stage.Screen;

public class MainPane extends Pane {

    public static double TILE_SIZE = 20;
    public static final float MIN_TILE_SIZE = 6;
    private static final float DRAG_SPEED = 0.2f;
    private static final float ZOOM_FACTOR = 1.05f;

    private Canvas canvas;
    private MarkerManager manager = new MarkerManager();

    private Vector clickPoint;

    public MainPane()
    {
        this.canvas = new Canvas(Screen.getPrimary().getBounds().getWidth(), Screen.getPrimary().getBounds().getHeight());
        this.getChildren().add(canvas);
        canvas.setOnMousePressed((MouseEvent e)->{
            clickPoint = new Vector(e.getSceneX(), e.getSceneY());
        });
        canvas.setOnMouseDragged((MouseEvent e)->{
            Vector newPos = new Vector(e.getSceneX()-clickPoint.getX(), e.getSceneY() - clickPoint.getY()).mult((float) (MIN_TILE_SIZE/TILE_SIZE)*DRAG_SPEED);
            manager.translateOrigin(newPos);
            clickPoint = new Vector(e.getSceneX(), e.getSceneY());
        });
        canvas.setOnScroll((ScrollEvent e)->{
            Vector old = manager.toWorldPoint(new Vector(e.getSceneX()/TILE_SIZE, e.getSceneY()/TILE_SIZE));
            if (e.getDeltaY() > 0) {
                TILE_SIZE *= ZOOM_FACTOR;
            } else {
                TILE_SIZE /= ZOOM_FACTOR;
            }
            if(TILE_SIZE < MIN_TILE_SIZE) TILE_SIZE = MIN_TILE_SIZE;
            Vector newPos = manager.toWorldPoint(new Vector(e.getSceneX()/TILE_SIZE, e.getSceneY()/TILE_SIZE));
            manager.translateOrigin(new Vector(newPos.getX()-old.getX(), newPos.getY()-old.getY()));
        });
    }

    public void displayAll()
    {
        if(manager.getOriginX() > 0)
        {
            manager.translateOrigin(new Vector(-manager.getOriginX(), 0));
        }
        if(manager.getOriginY() > 0)
        {
            manager.translateOrigin(new Vector(0, -manager.getOriginY()));
        }
        double diffX = canvas.getWidth() - (manager.getOriginX()*TILE_SIZE+Map.WIDTH*TILE_SIZE);
        double diffY = canvas.getHeight() - (manager.getOriginY()*TILE_SIZE+Map.HEIGHT*TILE_SIZE);
        if(diffX > 0)
        {
            manager.translateOrigin(new Vector(diffX/TILE_SIZE,0));
        }
        if(diffY > 0)
        {
            manager.translateOrigin(new Vector(0,diffY/TILE_SIZE));
        }
        GraphicsContext context = canvas.getGraphicsContext2D();
        context.clearRect(0, 0, getWidth(), getHeight());
        for(int x = 0; x< Map.WIDTH; x++)
        {
            for(int y = 0; y<Map.HEIGHT; y++)
            {
                Vector pos = new Vector(x, y);
                Vector displayPoint = manager.toWorldPoint(pos);
                if(displayPoint.getX()*TILE_SIZE+TILE_SIZE >= 0
                        && displayPoint.getX()*TILE_SIZE-TILE_SIZE <= canvas.getWidth()
                        && displayPoint.getY()*TILE_SIZE+TILE_SIZE >= 0
                        && displayPoint.getY()*TILE_SIZE-TILE_SIZE <= canvas.getHeight())
                    drawTile(pos, displayPoint, context);
            }
        }
        for(Living l : Map.getInstance().getLivings())
        {
            Vector pos = l.getPosition();
            Vector displayPoint = manager.toWorldPoint(pos);
            if(displayPoint.getX()*TILE_SIZE+TILE_SIZE >= 0
                    && displayPoint.getX()*TILE_SIZE-TILE_SIZE <= canvas.getWidth()
                    && displayPoint.getY()*TILE_SIZE+TILE_SIZE >= 0
                    && displayPoint.getY()*TILE_SIZE-TILE_SIZE <= canvas.getHeight())
                l.draw(context, displayPoint);
        }
    }

    public void drawTile(Vector pos, Vector displayPos, GraphicsContext context)
    {
        Map.getInstance().drawTile(pos, displayPos, context);
    }


}
