package fr.florian.ants.antv1.util.resource;

public abstract class Resource {
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
