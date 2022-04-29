package fr.florian.ants.antv1.ui;

public enum DisplayType {
    DEFAULT,
    PHEROMONES,
    SIGNALS,
    SIGNALSANDPHEROMONES;

    public DisplayType next() {
        switch(this)
        {
            case DEFAULT : return PHEROMONES;
            case PHEROMONES : return SIGNALS;
            case SIGNALS : return SIGNALSANDPHEROMONES;
            default:
            case SIGNALSANDPHEROMONES : return DEFAULT;
        }
    }
}
