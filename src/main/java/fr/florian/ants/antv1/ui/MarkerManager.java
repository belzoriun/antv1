package fr.florian.ants.antv1.ui;

import fr.florian.ants.antv1.util.Vector;

public class MarkerManager {
    private Vector origin;

    public MarkerManager()
    {
        origin = new Vector(-1.0, 2.0);
    }

    public void translateOrigin(Vector v)
    {
        origin.add(v);
    }

    public double getOriginX()
    {
        return origin.getX();
    }

    public double getOriginY()
    {
        return origin.getY();
    }

    public Vector toWorldPoint(Vector v)
    {
        return new Vector(origin.getX()+v.getX(), origin.getY()+v.getY());
    }
}
