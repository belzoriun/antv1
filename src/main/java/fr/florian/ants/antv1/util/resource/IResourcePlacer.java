package fr.florian.ants.antv1.util.resource;

import fr.florian.ants.antv1.map.ResourceTile;
import fr.florian.ants.antv1.util.Vector;

/**
 * Interface for resource placement
 */
public interface IResourcePlacer {

    ResourceTile placeTile(Vector v);

}
