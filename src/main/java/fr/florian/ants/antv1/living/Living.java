package fr.florian.ants.antv1.living;

import fr.florian.ants.antv1.living.ant.entity.AntEntity;
import fr.florian.ants.antv1.map.Map;
import fr.florian.ants.antv1.map.Tile;
import fr.florian.ants.antv1.ui.WorldView;
import fr.florian.ants.antv1.util.*;
import fr.florian.ants.antv1.util.effect.Effect;
import fr.florian.ants.antv1.util.fight.Attacker;
import fr.florian.ants.antv1.util.fight.FightManager;
import fr.florian.ants.antv1.util.statemachine.StateMachine;
import javafx.scene.Node;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;

/**
 * The class representing a living entity
 */
public abstract class Living {

    public static final God GOD = God.getInstance();
    private final double maxLifePoints;
    private double strength;
    private final int initialTicksPerOperation;

    protected Living(int ticksPerExecution, double maxLifePoints, double strength)
    {
        initialTicksPerOperation = ticksPerExecution;
        this.strength = strength;
        this.maxLifePoints = maxLifePoints;
    }

    public void onVictory(Living l)
    {
        //does nothing (may apply a special effect)
    }

    public abstract LivingEntity createEntity(Vector initialPosition);

    public double getStrength()
    {
        return strength;
    }

    public void onUpdate(LivingEntity self){ }

    public int getTicksPerExecution() {
        return initialTicksPerOperation;
    }

    public double getMaxLifePoints() {
        return maxLifePoints;
    }

    public abstract void onKilled(Attacker killer, LivingEntity self);

    public abstract void execute(LivingEntity livingEntity);
}
