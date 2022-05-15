package fr.florian.ants.antv1.living.insects.entity;

import fr.florian.ants.antv1.living.Living;
import fr.florian.ants.antv1.living.LivingEntity;
import fr.florian.ants.antv1.living.insects.Insect;
import fr.florian.ants.antv1.ui.WorldView;
import fr.florian.ants.antv1.util.ResourceLoader;
import fr.florian.ants.antv1.util.Vector;
import fr.florian.ants.antv1.util.fight.Attacker;
import javafx.scene.Node;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

public class InsectEntity extends LivingEntity {

    private Vector target;

    public InsectEntity(String initialState, Vector pos, Insect l) {
        super(initialState, pos, l);
    }


    @Override
    public void draw(GraphicsContext context, Vector position) {
        double rotation = 0;
        if(headingDirection != null) {
            rotation = switch (headingDirection) {
                case LEFT -> 90;
                case RIGHT -> -90;
                case DOWN -> 180;
                default -> 0;
            };
        }
        Image i = ResourceLoader.getInstance().loadResource(ResourceLoader.SPIDER);
        WorldView.drawRotatedImage(context, i, position.add(WorldView.TILE_SIZE/2), rotation, WorldView.TILE_SIZE*3);
        drawLifepoints(context, position, WorldView.TILE_SIZE*3);
    }

    @Override
    public void onVictory(LivingEntity l) {

    }

    public void resetTarget() {
        target = null;
    }

    public Vector getTarget() {
        return target;
    }

    public void setTarget(Vector enemy) {
    }

    @Override
    public void onUpdate() {
        getLiving().onUpdate(this);
    }

    @Override
    protected void onKilled(Attacker killer) {
        getLiving().onKilled(killer, this);
    }

    @Override
    public Node getDetailDisplay() {
        VBox box = new VBox();
        box.getChildren().add(new Label("Target at "+target));
        return box;
    }
}
