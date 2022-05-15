package fr.florian.ants.antv1.living;

import fr.florian.ants.antv1.util.Vector;
import fr.florian.ants.antv1.util.fight.Attacker;
import javafx.scene.Node;
import javafx.scene.canvas.GraphicsContext;

public class God extends Living implements Attacker{

    private static God instance = null;

    private God()
    {
        super(0, Integer.MAX_VALUE, Integer.MAX_VALUE);
    }

    public static God getInstance()
    {
        if(instance == null)
            instance = new God();
        return instance;
    }

    public void attack(LivingEntity l, double damage)
    {
        l.hit(this, damage);
    }

    @Override
    public LivingEntity createEntity(Vector initialPosition) {
        return null;
    }

    @Override
    public void onKilled(Attacker killer, LivingEntity self) {
        //nothing can kill god
    }

    @Override
    public void execute(LivingEntity livingEntity) {

    }

    @Override
    public void attack(LivingEntity l) {

    }

    @Override
    public void onVictory(LivingEntity l) {

    }
}
