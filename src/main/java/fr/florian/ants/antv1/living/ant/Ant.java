package fr.florian.ants.antv1.living.ant;

import fr.florian.ants.antv1.living.Living;
import fr.florian.ants.antv1.living.LivingEntity;
import fr.florian.ants.antv1.living.ant.entity.AntEntity;
import fr.florian.ants.antv1.map.AntHillTile;
import fr.florian.ants.antv1.map.Map;
import fr.florian.ants.antv1.map.Tile;
import fr.florian.ants.antv1.ui.Application;
import fr.florian.ants.antv1.ui.WorldView;
import fr.florian.ants.antv1.util.*;
import fr.florian.ants.antv1.util.fight.Attacker;
import fr.florian.ants.antv1.util.signals.AntSignal;
import fr.florian.ants.antv1.util.signals.AntSignalReceiver;
import fr.florian.ants.antv1.util.signals.AntSubscription;
import fr.florian.ants.antv1.util.statemachine.StateMachine;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

import java.util.concurrent.Flow;

/**
 * Ant class
 * describing the general behavior of an ant
 */
public abstract class Ant extends Living {

    public static final int MAX_SIZE = 10;

    private final double size;

    protected Ant(double size, int ticksPerOperation, double lifePoints, int strength) {
        super(ticksPerOperation, lifePoints, strength);
        if(size <= 0) size = 1;
        if(size > MAX_SIZE) size = MAX_SIZE;
        this.size = size;
    }

    public double getSize()
    {
        return size;
    }

    /**
     * Called when an order is received
     * @param order the received order
     */
    public abstract void onOrderReceived(AntEntity self, AntOrder order);
}
