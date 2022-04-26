package fr.florian.ants.antv1.living.ant;

import fr.florian.ants.antv1.map.ResourceTile;
import fr.florian.ants.antv1.util.HoldedResourceList;
import fr.florian.ants.antv1.util.resource.Resource;

import java.util.ArrayList;
import java.util.List;

public class WorkerAnt extends Ant{

    private HoldedResourceList holdedResources;

    public WorkerAnt()
    {
        holdedResources = new HoldedResourceList(5);
    }

    @Override
    protected void executeAction() {

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
