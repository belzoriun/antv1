package fr.florian.ants.antv1.util.option;

import java.util.HashMap;
import java.util.Map;

public class Options {

    private Map<String, String> options;

    public Options()
    {
        options = new HashMap<>();
        //set default options values
        set(OptionKey.MAP_WIDTH, 50);
        set(OptionKey.MAP_HEIGHT, 50);
        set(OptionKey.ANT_HILL_COUNT, 3);
        set(OptionKey.SOLDIER_PER_QUEEN, 5);
        set(OptionKey.WORKER_PER_SOLDIER, 10);
    }

    public void set(OptionKey name, int value)
    {
        options.put(name.getName(), value+"");
    }

    public int getInt(OptionKey name)
    {
        return Integer.parseInt(options.get(name.getName()));
    }
}
