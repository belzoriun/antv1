package fr.florian.ants.antv1.util.resource;

import fr.florian.ants.antv1.map.AntHillTile;
import fr.florian.ants.antv1.ui.Application;
import fr.florian.ants.antv1.ui.WorldView;
import fr.florian.ants.antv1.util.ResourceLoader;
import fr.florian.ants.antv1.util.Vector;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public class FoodResource extends Resource{

    private int foodType;

    public FoodResource(Vector position) {
        super(position, 0, 0.97);
        foodType = Application.random.nextInt(12);
    }

    @Override
    public void onDeposit(AntHillTile tile)
    {
        tile.addFood();
    }

    @Override
    public void draw(GraphicsContext context, Vector position) {
        double dotSize = WorldView.TILE_SIZE/3;
        String resource = switch (foodType) {
            case 2 -> ResourceLoader.BURGER;
            case 3 -> ResourceLoader.CARROT;
            case 4 -> ResourceLoader.CHERRY;
            case 5 -> ResourceLoader.EGG;
            case 6 -> ResourceLoader.FRIES;
            case 7 -> ResourceLoader.HAM;
            case 8 -> ResourceLoader.PIZZA;
            case 9 -> ResourceLoader.STRAWBERRY;
            case 10 -> ResourceLoader.SUSHI;
            case 11 -> ResourceLoader.WATERMELON;
            default -> ResourceLoader.APPLE;
        };
        Image i = ResourceLoader.getInstance().loadResource(resource);
        WorldView.drawRotatedImage(context, i, position, rotation, dotSize);
    }

    @Override
    public Resource clone(Vector v) {
        return new FoodResource(v);
    }
}
