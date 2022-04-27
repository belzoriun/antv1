package fr.florian.ants.antv1.living.ant;

import fr.florian.ants.antv1.living.Living;
import fr.florian.ants.antv1.util.Drawable;
import fr.florian.ants.antv1.util.Vector;

public abstract class Ant extends Living{

    protected Ant(Vector ipos) {
        super(ipos,50);
    }
}
