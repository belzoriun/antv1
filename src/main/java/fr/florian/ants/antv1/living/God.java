package fr.florian.ants.antv1.living;

import fr.florian.ants.antv1.util.Vector;
import fr.florian.ants.antv1.util.fight.Attacker;
import javafx.scene.Node;
import javafx.scene.canvas.GraphicsContext;

public class God extends Living{

    private static God instance = null;
    private static final int DEFAULT_DAMAGE = Integer.MAX_VALUE;

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

    public void setDamage(double damage)
    {
        this.strength = damage;
    }

    public void resetDamage()
    {
        this.strength = DEFAULT_DAMAGE;
    }

    @Override
    protected String getNextAction() {
        return "";
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
