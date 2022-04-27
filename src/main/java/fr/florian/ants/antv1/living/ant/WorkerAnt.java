package fr.florian.ants.antv1.living.ant;

import fr.florian.ants.antv1.map.Map;
import fr.florian.ants.antv1.map.ResourceTile;
import fr.florian.ants.antv1.ui.MainPane;
import fr.florian.ants.antv1.util.Direction;
import fr.florian.ants.antv1.util.HoldedResourceList;
import fr.florian.ants.antv1.util.Vector;
import fr.florian.ants.antv1.util.resource.Resource;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;

public class WorkerAnt extends Ant{

    private HoldedResourceList holdedResources;

    public WorkerAnt(long anthillId,Color color, Vector ipos)
    {
        super(anthillId, color, ipos, 7);
        holdedResources = new HoldedResourceList(5);
    }

    @Override
    protected void executeAction() {
        Direction dir = Direction.random();
        Vector newPos = position.add(dir.getOffset());
        if(Map.getInstance().getTile(newPos) != null)
        {
            this.position = newPos;
        }
    }

    private void takeResource(ResourceTile tile)
    {
        if(!holdedResources.isFull()) {
            Resource r = tile.take();
            if (r != null) {
                try {
                    holdedResources.add(r);
                }catch(Exception e)
                {
                    System.err.println(e);
                }
            }
        }
    }
}
