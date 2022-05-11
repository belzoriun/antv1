package fr.florian.ants.antv1.map;

import fr.florian.ants.antv1.util.Vector;

public interface ChunkUpdateFeature {
    public void call(Vector v, Tile t);
}
