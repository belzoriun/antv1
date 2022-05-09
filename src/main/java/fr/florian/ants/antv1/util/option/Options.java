package fr.florian.ants.antv1.util.option;

import java.util.HashMap;
import java.util.Map;

/**
 * Class used to manage options (parsing / saving / access)
 */
public class Options {

    private final Map<String, String> options;

    public Options()
    {
        options = new HashMap<>();
        //set default options values
        set(OptionKey.MAP_WIDTH, 50);
        set(OptionKey.MAP_HEIGHT, 50);
        set(OptionKey.ANT_HILL_COUNT, 3);
        set(OptionKey.SOLDIER_PER_QUEEN, 5);
        set(OptionKey.WORKER_PER_SOLDIER, 10);
        set(OptionKey.SIMULATION_TIME, 2);
        set(OptionKey.INFINITE_SIMULATION, false);
    }

    public void set(OptionKey name, int value)
    {
        options.put(name.getName(), value+"");
    }

    public void set(OptionKey name, boolean value)
    {
        options.put(name.getName(), String.valueOf(value));
    }

    public int getInt(OptionKey name)
    {
        return Integer.parseInt(options.get(name.getName()));
    }

    public boolean getBoolean(OptionKey name)
    {
        return options.get(name.getName()).equals("true");
    }
}
