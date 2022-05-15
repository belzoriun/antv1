package fr.florian.ants.antv1.living.insects;

import fr.florian.ants.antv1.util.registry.Registry;

public class Insects {
    public static final Insect SPIDER = Registry.register(Registry.INSECT, "spider", new Tarantula());
}
