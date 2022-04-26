package fr.florian.ants.antv1.map;

import fr.florian.ants.antv1.living.Living;
import fr.florian.ants.antv1.living.ant.Ant;
import fr.florian.ants.antv1.util.Drawable;

public interface Tile extends Drawable {
    void onWalkOn(Living l);
    void onInteract(Ant a);
}
