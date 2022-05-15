package fr.florian.ants.antv1.util.resource;

import fr.florian.ants.antv1.living.ant.Ant;
import fr.florian.ants.antv1.living.ant.QueenAnt;
import fr.florian.ants.antv1.living.ant.WorkerAnt;
import fr.florian.ants.antv1.living.ant.entity.AntEntity;
import fr.florian.ants.antv1.living.ant.entity.ResourceHolderAntEntity;
import fr.florian.ants.antv1.map.AntHillTile;
import fr.florian.ants.antv1.map.Map;
import fr.florian.ants.antv1.ui.Application;
import fr.florian.ants.antv1.ui.WorldView;
import fr.florian.ants.antv1.util.Drawable;
import fr.florian.ants.antv1.util.ImageColorMaker;
import fr.florian.ants.antv1.util.ResourceLoader;
import fr.florian.ants.antv1.util.Vector;
import fr.florian.ants.antv1.util.resource.Resource;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

/**
 * Class representing a dead ant (for display only)
 */
public class DeadAnt extends Resource implements Drawable {
    private final Color color;
    private final double size;
    private final AntEntity dead;

    public DeadAnt(AntEntity ant)
    {
        super(new Vector(Application.random.nextDouble(1), Application.random.nextDouble(1)), 0, 0);

        this.color=ant.getColor();
        this.size = ((Ant)ant.getLiving()).getSize();
        dead=ant;
        ResourceLoader.getInstance().saveResource("dead_ant"+color.getRed()+":"+color.getGreen()+":"+color.getBlue(),
                ImageColorMaker.fade(
                        ImageColorMaker.colorAntImage(ResourceLoader.getInstance().loadResource(ResourceLoader.DEAD_ANT), color), 0.6));
    }

    public boolean isOf(long id)
    {
        return dead.getAntHillId() == id;
    }

    @Override
    public void onDeposit(AntHillTile tile){
        Vector pos = Map.getInstance().getTilePosition(tile);
        if(pos != null) {
            if(dead.getAntHillId() == tile.getUniqueId()) {
                dead.revive();
                dead.setPosition(pos);
                if(dead instanceof ResourceHolderAntEntity wa)
                {
                    wa.resetPath();
                }
                tile.makeRespawn(dead);
            }
            else
            {
                tile.makeSpawnNewAnt(pos, 1, 1, false);
            }
        }
    }

    @Override
    public void draw(GraphicsContext context, Vector position) {
        double dotSize = WorldView.TILE_SIZE / (Ant.MAX_SIZE + 1 - size);
        Image i = ResourceLoader.getInstance().loadResource("dead_ant"+color.getRed()+":"+color.getGreen()+":"+color.getBlue());//ResourceLoader.getInstance().loadResource("dead_ant"+color.getRed()+":"+color.getGreen()+":"+color.getBlue());
        WorldView.drawRotatedImage(context, i, position, rotation, dotSize);
    }

    @Override
    public Resource clone(Vector v) {
        return null;
    }
}
