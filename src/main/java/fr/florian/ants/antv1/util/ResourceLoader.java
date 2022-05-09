package fr.florian.ants.antv1.util;

import javafx.scene.image.Image;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Class used to manage loaded resources
 */
public class ResourceLoader {

    private static ResourceLoader instance = null;

    public static final String GRASS_RES_1 = "grass_1.png";
    public static final String GRASS_RES_2 = "grass_2.png";
    public static final String ANT = "ant.png";
    public static final String DEAD_ANT = "ant_dead.png";
    public static final String SUGAR = "sugar.png";
    public static final String ANTHILL = "anthill.png";
    public static final String PEA = "pea.png";
    public static final String SEED_1 = "seed_1.png";
    public static final String SEED_2 = "seed_2.png";
    public static final String SEED_3 = "seed_3.png";
    public static final String SEED_4 = "seed_4.png";
    public static final String SEED_5 = "seed_5.png";
    public static final String ARROW = "arrow.png";
    public static final String APPLE = "apple.png";
    public static final String BURGER = "burger.png";
    public static final String CARROT = "carrot.png";
    public static final String CHERRY = "cherry.png";
    public static final String EGG = "egg.png";
    public static final String FRIES = "fries.png";
    public static final String HAM = "ham.png";
    public static final String PIZZA = "pizza.png";
    public static final String STRAWBERRY = "strawberry.png";
    public static final String SUSHI = "sushi.png";
    public static final String WATERMELON = "watermelon.png";

    private final Map<String, Image> streams;
    private final ClassLoader loader;

    private ResourceLoader()
    {
        streams = new HashMap<>();
        loader = this.getClass().getClassLoader();
    }

    public static ResourceLoader getInstance()
    {
        if(instance == null)
        {
            instance = new ResourceLoader();
            instance.loadResource(GRASS_RES_1);
            instance.loadResource(GRASS_RES_2);
            instance.loadResource(ANT);
            instance.loadResource(DEAD_ANT);
            instance.loadResource(SUGAR);
            instance.loadResource(ANTHILL);
            instance.loadResource(PEA);
            instance.loadResource(SEED_1);
            instance.loadResource(SEED_2);
            instance.loadResource(SEED_3);
            instance.loadResource(SEED_4);
            instance.loadResource(SEED_5);
            instance.loadResource(ARROW);
            instance.loadResource(APPLE);
            instance.loadResource(BURGER);
            instance.loadResource(CARROT);
            instance.loadResource(CHERRY);
            instance.loadResource(EGG);
            instance.loadResource(FRIES);
            instance.loadResource(HAM);
            instance.loadResource(PIZZA);
            instance.loadResource(STRAWBERRY);
            instance.loadResource(SUSHI);
            instance.loadResource(WATERMELON);
        }
        return instance;
    }

    public void saveResource(String name, Image i)
    {
        streams.put(name, i);
    }

    public Image loadResource(String res)
    {
        if(!streams.containsKey(res))
        {
            streams.put(res, new Image(Objects.requireNonNull(loader.getResourceAsStream(res))));
        }
        return streams.get(res);
    }
}
