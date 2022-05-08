package fr.florian.ants.antv1.util.resource;

import fr.florian.ants.antv1.map.AntHillTile;
import fr.florian.ants.antv1.ui.Application;
import fr.florian.ants.antv1.util.Drawable;
import fr.florian.ants.antv1.util.Vector;

public abstract class Resource implements Drawable {
    private final int score;
    private double rarity;
    private final Vector position;
    protected final double rotation;

    protected Resource(Vector position, int score, double rarity)
    {
        this.rotation = Application.random.nextDouble(360);
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

    public void onDeposit(AntHillTile tile){}
}
