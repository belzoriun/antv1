package fr.florian.ants.antv1.util.resource;

import fr.florian.ants.antv1.util.Drawable;

public abstract class Resource implements Drawable {
    private int score;

    protected Resource(int score)
    {
        this.score = score;
    }

    public int getResourceScore()
    {
        return score;
    }
}
