package fr.florian.ants.antv1.ui;

import fr.florian.ants.antv1.living.Living;
import fr.florian.ants.antv1.map.Map;
import fr.florian.ants.antv1.util.GameTimer;
import fr.florian.ants.antv1.util.Vector;
import fr.florian.ants.antv1.util.signals.AntSignal;
import fr.florian.ants.antv1.util.signals.AntSignalSender;
import javafx.event.EventHandler;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.*;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Screen;

import java.util.List;

public class MainPane extends Pane {

    public static double TILE_SIZE = 20;
    public static double MIN_TILE_SIZE = 5;
    private static double MAX_TILE_SIZE = 70;
    private static final float DRAG_SPEED = 0.2f;
    private static final float ZOOM_FACTOR = 1.05f;

    private DisplayType displayType;

    private Canvas canvas;
    private MarkerManager manager = new MarkerManager();

    private Vector clickPoint;

    public MainPane()
    {
        displayType = DisplayType.DEFAULT;
        this.canvas = new Canvas(Screen.getPrimary().getBounds().getWidth(), Screen.getPrimary().getBounds().getHeight());
        double futureTileSize = TILE_SIZE;
        if(Map.WIDTH*TILE_SIZE < canvas.getWidth())
        {
            double wtileSize = canvas.getWidth()/Map.WIDTH;
            if(wtileSize > futureTileSize) {
                futureTileSize = wtileSize;
                MAX_TILE_SIZE = futureTileSize * 2;
                MIN_TILE_SIZE = futureTileSize;
            }
        }
        if(Map.HEIGHT*TILE_SIZE < canvas.getHeight())
        {
            double htileSize = canvas.getHeight()/Map.HEIGHT;
            if(htileSize > futureTileSize)
            {
                futureTileSize = htileSize;
                MAX_TILE_SIZE = futureTileSize*2;
                MIN_TILE_SIZE = futureTileSize;
            }
        }
        TILE_SIZE = futureTileSize*1.5;
        this.getChildren().add(canvas);
        canvas.setOnMousePressed((MouseEvent e)->{
            if(e.isPrimaryButtonDown()) {
                clickPoint = new Vector(e.getSceneX(), e.getSceneY());
            }else if(e.isMiddleButtonDown() && e.isControlDown())
            {
                GameTimer.getInstance().setTickTimeDefault();
            }
        });
        canvas.setOnMouseDragged((MouseEvent e)->{
            Vector newPos = new Vector(e.getSceneX() - clickPoint.getX(), e.getSceneY() - clickPoint.getY()).mult((float) (MIN_TILE_SIZE / TILE_SIZE) * DRAG_SPEED);
            manager.translateOrigin(newPos);
            clickPoint = new Vector(e.getSceneX(), e.getSceneY());
        });
        canvas.setOnScroll((ScrollEvent e)->{
            if(e.isControlDown()) {
                long delta = 1L;
                delta = GameTimer.getInstance().getTickTime()+(long)(e.getDeltaY())*delta;
                GameTimer.getInstance().setTickTime(delta);
            }
            else {
                Vector old = manager.toWorldPoint(new Vector(e.getSceneX() / TILE_SIZE, e.getSceneY() / TILE_SIZE));
                if (e.getDeltaY() > 0) {
                    TILE_SIZE *= ZOOM_FACTOR;
                } else {
                    TILE_SIZE /= ZOOM_FACTOR;
                }
                if (TILE_SIZE < MIN_TILE_SIZE) TILE_SIZE = MIN_TILE_SIZE;
                if (TILE_SIZE > MAX_TILE_SIZE) TILE_SIZE = MAX_TILE_SIZE;
                Vector newPos = manager.toWorldPoint(new Vector(e.getSceneX() / TILE_SIZE, e.getSceneY() / TILE_SIZE));
                manager.translateOrigin(new Vector(newPos.getX() - old.getX(), newPos.getY() - old.getY()));
            }
        });
    }

    public void toNextDisplay()
    {
        displayType = displayType.next();
    }

    private void applyShaders()
    {
        double transition = Math.exp(6*Math.pow(GameTimer.getInstance().getDayNightTime()-1.2, 7));
        Color day = new Color(109/255.0, 223/255.0, 1, 0);
        Color night = new Color(0/255.0, 4/255.0, 103/255.0, 0.4);
        Color dayNightShader = night.interpolate(day, transition);
        canvas.getGraphicsContext2D().setFill(dayNightShader);
        canvas.getGraphicsContext2D().fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
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
            if(l instanceof AntSignalSender sender && (displayType == DisplayType.SIGNALS || displayType == DisplayType.SIGNALSANDPHEROMONES))
            {
                List<AntSignal> sigs = sender.getSignalList();
                synchronized (sigs) {
                    for (AntSignal s : sigs) {
                        if (!s.mayDissipate())
                            s.draw(context, manager.toWorldPoint(s.getSourcePosition()));
                    }
                }
            }
        }
        applyShaders();
    }

    public void drawTile(Vector pos, Vector displayPos, GraphicsContext context)
    {
        Map.getInstance().drawTile(pos, displayPos, context);
        if(displayType == DisplayType.PHEROMONES || displayType == DisplayType.SIGNALSANDPHEROMONES)
            Map.getInstance().drawPheromones(pos, displayPos, context);
    }


}
