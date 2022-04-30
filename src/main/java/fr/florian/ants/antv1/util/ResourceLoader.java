package fr.florian.ants.antv1.util;

import javafx.scene.image.Image;

import java.util.HashMap;
import java.util.Map;

public class ResourceLoader {

    private static ResourceLoader instance = null;

    public static final String GRASS_RES_1 = "grass_1.png";
    public static final String GRASS_RES_2 = "grass_2.png";
    public static final String ANT_UP = "ant_up.png";
    public static final String ANT_LEFT = "ant_left.png";
    public static final String ANT_RIGHT = "ant_right.png";
    public static final String ANT_DOWN = "ant_down.png";
    public static final String SUGAR = "sugar.png";

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
            instance.loadResource(ANT_UP);
            instance.loadResource(ANT_LEFT);
            instance.loadResource(ANT_RIGHT);
            instance.loadResource(ANT_DOWN);
            instance.loadResource(SUGAR);
        }
        return instance;
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
