package fr.florian.ants.antv1.living.ant;

import fr.florian.ants.antv1.util.registry.Registry;

public class Ants {
    public static final Ant QUEEN = Registry.register(Registry.ANT, "queen", new QueenAnt());
    public static final Ant SOLDIER = Registry.register(Registry.ANT, "soldier", new SoldierAnt());
    public static final Ant WORKER = Registry.register(Registry.ANT, "worker", new WorkerAnt());
}
