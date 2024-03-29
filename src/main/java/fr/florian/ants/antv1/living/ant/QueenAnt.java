package fr.florian.ants.antv1.living.ant;

import fr.florian.ants.antv1.living.LivingEntity;
import fr.florian.ants.antv1.living.ant.entity.AntEntity;
import fr.florian.ants.antv1.living.ant.entity.ResourceHolderAntEntity;
import fr.florian.ants.antv1.map.AntHillTile;
import fr.florian.ants.antv1.map.Map;
import fr.florian.ants.antv1.ui.Application;
import fr.florian.ants.antv1.util.AntOrder;
import fr.florian.ants.antv1.util.GameTimer;
import fr.florian.ants.antv1.util.Vector;
import fr.florian.ants.antv1.util.fight.Attacker;
import fr.florian.ants.antv1.util.option.OptionKey;
import fr.florian.ants.antv1.util.registry.Registry;
import fr.florian.ants.antv1.util.signals.AntSignal;
import fr.florian.ants.antv1.util.signals.AntSubscription;
import fr.florian.ants.antv1.util.statemachine.StateMachine;
import javafx.scene.paint.Color;

import java.util.*;

/**
 * Class representing a queen ant
 */
public class QueenAnt extends Ant {

    public QueenAnt() {
        super(new HashSet<>(), Integer.MAX_VALUE, Integer.MAX_VALUE, 10, 50, 200, 10);
    }

    public void execute(LivingEntity self)
    {
        if(self instanceof AntEntity a)
        {
            AntOrder order = AntOrder.SEARCH_FOR_FOOD;
            if (GameTimer.getInstance().getRemainingTime() <= 30000 && !Application.options.getBoolean(OptionKey.INFINITE_SIMULATION)) {
                order = AntOrder.BACK_TO_COLONY;
            }
            AntSignal newSig = new AntSignal(a, a.getPosition(), order, 30, 0.3);
            a.sendSignal(newSig);
            new Thread(newSig).start();
            if (Application.random.nextDouble() < 0.25) {
                ((AntHillTile)Map.getInstance().getTile(self.getPosition())).makeSpawnNewAnt(a.getPosition(), 5, 15, true);
            }
        }
    }

    @Override
    public LivingEntity createEntity(Vector initialPosition) {
        return new AntEntity(null, initialPosition, this);
    }

    @Override
    public void onKilled(Attacker killer, LivingEntity self) {
        //TODO : make something when queen dies
    }

    @Override
    public void onOrderReceived(AntEntity self, AntOrder order) {
        //does nothing
    }
}
