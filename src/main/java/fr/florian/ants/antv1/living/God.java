package fr.florian.ants.antv1.living;

import fr.florian.ants.antv1.util.Vector;
import fr.florian.ants.antv1.util.fight.Attacker;
import javafx.scene.Node;
import javafx.scene.canvas.GraphicsContext;

public class God extends Living{

    private static God instance = null;

    private God()
    {
        super(new Vector(0, 0), 0, Integer.MAX_VALUE, Integer.MAX_VALUE);
    }

    public static God getInstance()
    {
        if(instance == null)
            instance = new God();
        return instance;
    }

    public void attack(Living l, double damage)
    {
        double base = strength;
        strength = damage;
        this.attack(l);
        strength = base;
    }

    @Override
    protected String getNextAction() {
        return null;
    }

    @Override
    public void onKilled(Attacker killer) {
        //nothing can kill god
    }

    @Override
    protected void onAttackedBy(Living l) {
        l.kill(this);
    }

    @Override
    public Node getDetailDisplay() {
        return null;
    }

    @Override
    public void draw(GraphicsContext context, Vector position) {
        //god cannot be drawn
    }
}
