package fr.florian.ants.antv1.ui;

/**
 * Enum representing the type of display of the world
 */
public enum DisplayType {
    DEFAULT,
    PHEROMONES,
    SIGNALS,
    SIGNALS_AND_PHEROMONES;

    public DisplayType next() {
        return switch (this) {
            case DEFAULT -> PHEROMONES;
            case PHEROMONES -> SIGNALS;
            case SIGNALS -> SIGNALS_AND_PHEROMONES;
            case SIGNALS_AND_PHEROMONES -> DEFAULT;
        };
    }
}
