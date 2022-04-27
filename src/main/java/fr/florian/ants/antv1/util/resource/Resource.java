package fr.florian.ants.antv1.util.resource;

import fr.florian.ants.antv1.util.Drawable;

public abstract class Resource implements Drawable {
    private int score;
    private double rarity;

    protected Resource(int score, double rarity)
    {
        this.score = score;
        this.rarity = rarity;
        if(this.rarity > 1) this.rarity = 1;
        if(this.rarity < 0) this.rarity = 0;
    }

    public int getResourceScore()
    {
        return score;
    }

    public double getRarity(){return rarity;}

    public abstract Resource clone();
}
