package fr.florian.ants.antv1.living.ant;

import fr.florian.ants.antv1.living.Living;
import fr.florian.ants.antv1.map.Map;
import fr.florian.ants.antv1.util.Direction;
import fr.florian.ants.antv1.util.Vector;
import javafx.scene.paint.Color;

public class SoldierAnt extends Ant{

    protected SoldierAnt(long anthillId, Color color, Vector ipos) {
        super(anthillId, color, ipos, 0.8, 3);
    }

    @Override
    protected void executeAction() {
        while(Map.getInstance().getTile(position.add(headingDirection.getOffset())) == null) {
            headingDirection = Direction.random();
        }
        position = position.add(headingDirection.getOffset());
    }
}
