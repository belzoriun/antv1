package fr.florian.ants.antv1.living.ant;

import fr.florian.ants.antv1.living.Living;
import fr.florian.ants.antv1.living.LivingEntity;
import fr.florian.ants.antv1.living.ant.entity.AntEntity;
import fr.florian.ants.antv1.map.AntHillTile;
import fr.florian.ants.antv1.map.Map;
import fr.florian.ants.antv1.util.AntOrder;
import fr.florian.ants.antv1.util.Direction;
import fr.florian.ants.antv1.util.Vector;
import fr.florian.ants.antv1.util.fight.Attacker;
import fr.florian.ants.antv1.util.signals.AntSignal;
import fr.florian.ants.antv1.util.signals.AntSignalSender;
import fr.florian.ants.antv1.util.signals.AntSubscription;
import fr.florian.ants.antv1.util.statemachine.StateMachine;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Flow;
import java.util.concurrent.ForkJoinPool;

/**
 * Class representing a commander ant
 */
public class SoldierAnt extends Ant{

    private static final double MAX_ANTHILL_DISTANCE = 20;

    public SoldierAnt() {
        super(9.2, 10, 10, 3);
    }


    @Override
    public LivingEntity createEntity(Vector initialPosition) {
        return new AntEntity(null, initialPosition, this);
    }

    @Override
    public void onOrderReceived(AntEntity self, AntOrder order) {
        AntSignal newSig = new AntSignal(self, self.getPosition(), order, 15, 0.3);
        self.sendSignal(newSig);
    }

    @Override
    public void onKilled(Attacker killer, LivingEntity self) {

    }

    @Override
    public void execute(LivingEntity livingEntity) {
        if(livingEntity instanceof AntEntity a) {
            a.setHeadingDirection(Direction.random());
            while (a.getPosition().add(a.getHeadingDirection().getOffset()).delta(a.getPosition()) > MAX_ANTHILL_DISTANCE
                    || Map.getInstance().getTile(a.getPosition().add(a.getHeadingDirection().getOffset())) == null
                    || (Map.getInstance().getTile(a.getPosition().add(a.getHeadingDirection().getOffset())) instanceof AntHillTile ht && ht.getUniqueId() != a.getAntHillId())) {
                a.setHeadingDirection(Direction.random());
            }
            a.setPosition(a.getPosition().add(a.getHeadingDirection().getOffset()));
        }
    }
}
