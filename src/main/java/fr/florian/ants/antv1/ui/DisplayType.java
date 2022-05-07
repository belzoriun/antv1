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
        switch(this)
        {
            case DEFAULT : return PHEROMONES;
            case PHEROMONES : return SIGNALS;
            case SIGNALS : return SIGNALS_AND_PHEROMONES;
            default:
            case SIGNALS_AND_PHEROMONES: return DEFAULT;
        }
    }
}
