package fr.florian.ants.antv1.map;

import fr.florian.ants.antv1.util.resource.Resource;

import java.util.List;

public interface ResourceHoldTile {
    public List<Resource> getResources();
    public int resourceCount();
    public Resource take();
    public void placeResource(Resource remove);
}
