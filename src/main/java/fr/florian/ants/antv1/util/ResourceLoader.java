package fr.florian.ants.antv1.util;

import javafx.scene.image.Image;

import java.util.HashMap;
import java.util.Map;

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

    private Map<String, Image> streams;
    private ClassLoader loader;

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
            streams.put(res, new Image(loader.getResourceAsStream(res)));
        }
        return streams.get(res);
    }
}
