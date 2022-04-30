package fr.florian.ants.antv1.util.resource;

import fr.florian.ants.antv1.util.Drawable;
import fr.florian.ants.antv1.util.Vector;

public abstract class Resource implements Drawable {
    private int score;
    private double rarity;
    private Vector position;

    protected Resource(Vector position, int score, double rarity)
    {
        this.score = score;
        this.rarity = rarity;
        this.position = position;
        if(this.rarity > 1) this.rarity = 1;
        if(this.rarity < 0) this.rarity = 0;
    }

    public int getResourceScore()
    {
        return score;
    }

    public double getRarity(){return rarity;}

    public abstract Resource clone(Vector v);

    public Vector getPosition()
    {
        return position;
    }
}
