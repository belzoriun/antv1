package fr.florian.ants.antv1.ui;

import fr.florian.ants.antv1.living.Living;
import fr.florian.ants.antv1.living.ant.WorkerAnt;
import fr.florian.ants.antv1.map.AntHillTile;
import fr.florian.ants.antv1.map.Map;
import fr.florian.ants.antv1.util.GameTimer;
import fr.florian.ants.antv1.util.Vector;
import fr.florian.ants.antv1.util.option.OptionKey;
import fr.florian.ants.antv1.util.signals.AntSignal;
import fr.florian.ants.antv1.util.signals.AntSignalSender;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Screen;

import java.util.HashMap;
import java.util.List;

/**
 * Class used to display world
 */
public class WorldView extends Pane {

    public static double TILE_SIZE = 20;
    public static double MIN_TILE_SIZE = 15;
    private static double MAX_TILE_SIZE = 70;
    private static final float ZOOM_FACTOR = 1.05f;
    private static Living followed;

    private DisplayType displayType;
    private TileDetail detail;
    private LivingDetailDisplay livingDetail;

    private final Canvas canvas;
    private final MarkerManager manager = new MarkerManager();
    private final java.util.Map<Long, ArrowDisplay> hillArrows;

    private Vector clickPoint;

    public WorldView()
    {
        this.canvas = new Canvas(Screen.getPrimary().getBounds().getWidth(), Screen.getPrimary().getBounds().getHeight());
        hillArrows = new HashMap<>();
    }

    /**
     * Initialise the world display
     */
    public void init()
    {
        detail = new TileDetail();
        livingDetail = new LivingDetailDisplay();
        displayType = DisplayType.DEFAULT;
        double futureTileSize = TILE_SIZE;
        double heightTileSize = Screen.getPrimary().getBounds().getHeight()/Application.options.getInt(OptionKey.MAP_HEIGHT);
        if(heightTileSize > futureTileSize)
        {
            futureTileSize = heightTileSize;
            MAX_TILE_SIZE = futureTileSize*2;
        }
        if(heightTileSize > MIN_TILE_SIZE)
            MIN_TILE_SIZE = heightTileSize;
        canvas.widthProperty().addListener(new ChangeListener<>() {
            double futureTileSize = TILE_SIZE;

            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
                double widthTileSize = t1.doubleValue() / Application.options.getInt(OptionKey.MAP_WIDTH);
                if (widthTileSize > futureTileSize) {
                    futureTileSize = widthTileSize;
                    MAX_TILE_SIZE = futureTileSize * 2;
                }
                if (widthTileSize > MIN_TILE_SIZE)
                    MIN_TILE_SIZE = widthTileSize;
                if (MIN_TILE_SIZE > TILE_SIZE) {
                    TILE_SIZE = MIN_TILE_SIZE;
                }
            }
        });
        canvas.heightProperty().addListener(new ChangeListener<>() {
            double futureTileSize = TILE_SIZE;

            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
                double heightTileSize = t1.doubleValue() / Application.options.getInt(OptionKey.MAP_HEIGHT);
                if (heightTileSize > futureTileSize) {
                    futureTileSize = heightTileSize;
                    MAX_TILE_SIZE = futureTileSize * 2;
                }
                if (heightTileSize > MIN_TILE_SIZE)
                    MIN_TILE_SIZE = heightTileSize;

                if (MIN_TILE_SIZE > TILE_SIZE) {
                    TILE_SIZE = MIN_TILE_SIZE;
                }
            }
        });
        this.getChildren().add(canvas);
        canvas.setOnMousePressed((MouseEvent e)->{
            if(e.isPrimaryButtonDown()) {
                clickPoint = new Vector(e.getSceneX(), e.getSceneY());
                for(AntHillTile hill : Map.getInstance().getAntHills())
                {
                    if(hillArrows.get(hill.getUniqueId()).inBounds(new Vector(e.getX(), e.getY())))
                    {
                        goTo(Map.getInstance().getTilePosition(hill));
                        return;
                    }
                }
            }else if(e.isMiddleButtonDown() && e.isControlDown())
            {
                GameTimer.getInstance().setTickTimeDefault();
            }
            else if(e.isSecondaryButtonDown()) {
                followed = null;
                Vector pos = new Vector(e.getSceneX(), e.getSceneY());
                pos = new Vector(pos.getX()-manager.getOriginX()*TILE_SIZE, pos.getY()- manager.getOriginY()*TILE_SIZE).multi(1/TILE_SIZE);
                pos = new Vector((int)(pos.getX()), (int)(pos.getY()));
                detail.displayForTile(Map.getInstance().getTile(pos), pos);
            }
        });
        canvas.setOnMouseDragged((MouseEvent e)->{
            if(e.isPrimaryButtonDown()) {
                Vector point = manager.toWorldPoint(clickPoint);
                Vector trans = manager.toWorldPoint(new Vector(e.getSceneX(), e.getSceneY()));
                manager.translateOrigin(new Vector(trans.getX() - point.getX(), trans.getY() - point.getY()).multi(1 / TILE_SIZE));
                clickPoint = new Vector(e.getSceneX(), e.getSceneY());
                followed = null;
            }
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
        detail.setTranslateX(10);
        detail.setTranslateY(10);
        getChildren().add(detail);
        getChildren().add(livingDetail);
        livingDetail.setVisible(false);
        livingDetail.setTranslateX(250);
        livingDetail.setTranslateY(10);

        for(AntHillTile hill : Map.getInstance().getAntHills())
        {
            hillArrows.put(hill.getUniqueId(), new ArrowDisplay(hill, manager, canvas));
        }
    }

    /**
     * Switch display type to next one
     */
    public void toNextDisplay()
    {
        displayType = displayType.next();
    }

    /**
     * Apply day/night and other shaders on top of world display
     */
    private void applyShaders()
    {
        double transition = Math.exp(6*Math.pow(GameTimer.getInstance().getDayNightTime()-1.2, 7));
        Color day = new Color(109/255.0, 223/255.0, 1, 0);
        Color night = new Color(0/255.0, 4/255.0, 103/255.0, 0.4);
        Color dayNightShader = night.interpolate(day, transition);
        canvas.getGraphicsContext2D().setFill(dayNightShader);
        canvas.getGraphicsContext2D().fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
    }

    /**
     * Update world display
     */
    public void displayAll()
    {
        if(followed != null && !followed.isAlive())
        {
            followed = null;
        }
        detail.update();
        if(followed != null)
        {
            livingDetail.displayFor(followed);
            livingDetail.setVisible(true);
            livingDetail.update();
        }
        else
        {
            livingDetail.setVisible(false);
        }
        if(followed != null && followed.getPosition() != null)
        {
            goTo(followed.getPosition());
        }
        if(manager.getOriginX() > 0)
        {
            manager.translateOrigin(new Vector(-manager.getOriginX(), 0));
        }
        if(manager.getOriginY() > 0)
        {
            manager.translateOrigin(new Vector(0, -manager.getOriginY()));
        }
        double diffX = canvas.getWidth() - (manager.getOriginX()*TILE_SIZE+Application.options.getInt(OptionKey.MAP_WIDTH)*TILE_SIZE);
        double diffY = canvas.getHeight() - (manager.getOriginY()*TILE_SIZE+Application.options.getInt(OptionKey.MAP_HEIGHT)*TILE_SIZE);
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
        for (int x = 0; x < Application.options.getInt(OptionKey.MAP_WIDTH); x++) {
            for (int y = 0; y < Application.options.getInt(OptionKey.MAP_HEIGHT); y++) {
                Vector pos = new Vector(x, y);
                Vector displayPoint = manager.toWorldPoint(pos).multi(TILE_SIZE);
                if (displayPoint.getX()  + TILE_SIZE >= 0
                        && displayPoint.getX()  - TILE_SIZE <= canvas.getWidth()
                        && displayPoint.getY()  + TILE_SIZE >= 0
                        && displayPoint.getY()  - TILE_SIZE <= canvas.getHeight())
                    drawTile(pos, displayPoint, context);
            }
        }
        for (int x = 0; x < Application.options.getInt(OptionKey.MAP_WIDTH); x++) {
            for (int y = 0; y < Application.options.getInt(OptionKey.MAP_HEIGHT); y++) {
                Vector pos = new Vector(x, y);
                Vector displayPoint = manager.toWorldPoint(pos).multi(TILE_SIZE);
                if (displayPoint.getX()  + TILE_SIZE >= 0
                        && displayPoint.getX()  - TILE_SIZE <= canvas.getWidth()
                        && displayPoint.getY()  + TILE_SIZE >= 0
                        && displayPoint.getY()  - TILE_SIZE <= canvas.getHeight())
                    Map.getInstance().displayResources(context, pos, displayPoint);
            }
        }

        for (Living l : Map.getInstance().getLivings()) {
            Vector pos = l.getPosition();
            if(pos != null)
            {
                Vector displayPoint = manager.toWorldPoint(pos).multi(TILE_SIZE);
                if (displayPoint.getX() + TILE_SIZE >= 0
                        && displayPoint.getX() - TILE_SIZE <= canvas.getWidth()
                        && displayPoint.getY() + TILE_SIZE >= 0
                        && displayPoint.getY() - TILE_SIZE <= canvas.getHeight()) {
                    l.draw(context, displayPoint);
                    if(l.equals(followed))
                    {
                        context.setFill(Color.YELLOW);
                        context.setStroke(Color.YELLOW);
                        context.setLineDashes(0);
                        context.strokeOval(displayPoint.getX()+TILE_SIZE/4, displayPoint.getY()+TILE_SIZE/4, TILE_SIZE/2, TILE_SIZE/2);
                        if(l instanceof WorkerAnt w) {
                            drawPath(context, w.getPath());
                        }
                    }
                }
                if (l instanceof AntSignalSender sender && (displayType == DisplayType.SIGNALS || displayType == DisplayType.SIGNALS_AND_PHEROMONES)) {
                    List<AntSignal> signals = sender.getSignalList();
                    for (AntSignal s : signals) {
                        if (!s.mayDissipate())
                            s.draw(context, manager.toWorldPoint(s.getSourcePosition()));
                    }
                }
            }
        }
        Vector pos = detail.positionForDisplay();
        context.setStroke(Color.YELLOW);
        context.setLineWidth(2);
        context.setLineDashes(0);
        context.strokeRect(pos.getX()*TILE_SIZE+manager.getOriginX()*TILE_SIZE, pos.getY()*TILE_SIZE+manager.getOriginY()*TILE_SIZE,
                TILE_SIZE, TILE_SIZE);
        applyShaders();
        drawArrows(context);
    }

    private void drawPath(GraphicsContext context, List<Vector> path) {
        for(int i = 1; i<path.size(); i++)
        {
            Vector start = manager.toWorldPoint(path.get(i-1)).multi(TILE_SIZE).add(TILE_SIZE/2);
            Vector end = manager.toWorldPoint(path.get(i)).multi(TILE_SIZE).add(TILE_SIZE/2);
            context.fillOval(start.getX()-TILE_SIZE/16, start.getY()-TILE_SIZE/16, TILE_SIZE/8, TILE_SIZE/8);
            context.fillOval(end.getX()-TILE_SIZE/16, end.getY()-TILE_SIZE/16, TILE_SIZE/8, TILE_SIZE/8);
            context.strokeLine(start.getX(), start.getY(), end.getX(), end.getY());
        }
    }

    public void drawTile(Vector pos, Vector displayPos, GraphicsContext context)
    {
        Map.getInstance().drawTile(pos, displayPos, context);
        if(displayType == DisplayType.PHEROMONES || displayType == DisplayType.SIGNALS_AND_PHEROMONES)
            Map.getInstance().drawPheromones(pos, displayPos, context);
    }

    public static void drawRotatedImage(GraphicsContext context, Image i, Vector position, double angle, double size)
    {
        context.save();
        context.translate(position.getX(), position.getY());
        context.rotate(-angle);
        context.drawImage(i, -size/2, -size/2, size, size);
        context.restore();
    }

    /**
     * Place the given tile position on center of the screen
     * @param position The tile position
     */
    public void goTo(Vector position)
    {
        Vector anchor = new Vector(widthProperty().get()/TILE_SIZE/2, heightProperty().get()/TILE_SIZE/2);
        Vector pos = manager.toWorldPoint(position);
        manager.translateOrigin(anchor.add(pos.multi(-1)));
    }

    public static void follow(Living l)
    {
        followed = l;
    }

    public void drawArrows(GraphicsContext context)
    {
        for(AntHillTile hill : Map.getInstance().getAntHills())
        {
            hillArrows.get(hill.getUniqueId()).update(context);
        }
    }

    public void bindWidth(DoubleBinding widthProperty) {
        canvas.widthProperty().bind(widthProperty);
    }
}
