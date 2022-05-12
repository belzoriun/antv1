package fr.florian.ants.antv1.util.option;

import fr.florian.ants.antv1.map.Chunk;

/**
 * Enum listing all simulation options
 */
@SuppressWarnings("SpellCheckingInspection")
public enum OptionKey {
    MAP_WIDTH("Map width (by "+ Chunk.CHUNK_SIZE+"x"+Chunk.CHUNK_SIZE+" chunks)", "mapwidth"),
    ANT_HILL_COUNT("Number of ant hills", "anthillsnb"),
    SOLDIER_PER_QUEEN("Number of soldier per queen", "soldierperqueen"),
    WORKER_PER_SOLDIER("Number of worker per soldier", "workerpersoldier"),
    SIMULATION_TIME("Simulation duration", "duration"),
    INFINITE_SIMULATION("Simulation is infinite", "infinitesimulation"),
    MAP_HEIGHT("Map height (by "+ Chunk.CHUNK_SIZE+"x"+Chunk.CHUNK_SIZE+" chunks)", "mapheight");

    private final String name;
    private final String label;

    OptionKey(String label, String name)
    {
        this.label=label;
        this.name=name;
    }

    public String getLabel()
    {
        return label;
    }

    public String getName()
    {
        return name;
    }
}
