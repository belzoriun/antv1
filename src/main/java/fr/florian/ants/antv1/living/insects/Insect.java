package fr.florian.ants.antv1.living.insects;

import fr.florian.ants.antv1.living.Living;
import fr.florian.ants.antv1.living.LivingEntity;
import fr.florian.ants.antv1.living.ant.entity.AntEntity;
import fr.florian.ants.antv1.living.insects.entity.InsectEntity;
import fr.florian.ants.antv1.util.Vector;
import fr.florian.ants.antv1.util.fight.Attacker;
import javafx.scene.paint.Color;

public abstract class Insect extends Living {
    protected Insect(int ticksPerExecution, double maxLifePoints, double strength) {
        super(ticksPerExecution, maxLifePoints, strength);
    }
}
