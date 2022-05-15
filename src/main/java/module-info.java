module fr.florian.ants.antv {
    requires javafx.controls;
    requires guava;
    requires it.unimi.dsi.fastutil;


    exports fr.florian.ants.antv1.ui;
    exports fr.florian.ants.antv1.living;
    exports fr.florian.ants.antv1.living.ant;
    exports fr.florian.ants.antv1.map;
    exports fr.florian.ants.antv1.util;
    exports fr.florian.ants.antv1.util.resource;
    exports fr.florian.ants.antv1.util.option;
    exports fr.florian.ants.antv1.util.exception;
    exports fr.florian.ants.antv1.util.signals;
    exports fr.florian.ants.antv1.util.pheromone;
    exports fr.florian.ants.antv1.map.tileplacer;
    exports fr.florian.ants.antv1.util.fight;
    exports fr.florian.ants.antv1.living.ant.entity;
}