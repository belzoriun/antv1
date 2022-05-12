package fr.florian.ants.antv1.util.resource;

import fr.florian.ants.antv1.map.ResourceHoldTile;
import fr.florian.ants.antv1.map.ResourceTile;
import fr.florian.ants.antv1.map.Tile;
import fr.florian.ants.antv1.util.Vector;

/**
 * Interface for resource placement
 */
public interface IResourcePlacer {

    void placeResources(Vector v, ResourceHoldTile t);

}
