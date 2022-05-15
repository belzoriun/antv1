package fr.florian.ants.antv1.util.option;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Class used to manage options (parsing / saving / access)
 */
public class Options {

    private final Map<String, String> options;
    private final InputStream file;

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
        file = Objects.requireNonNull(this.getClass().getClassLoader().getResourceAsStream("options.txt"));
    }

    public void load()
    {
        try(BufferedReader reader = new BufferedReader(new InputStreamReader(file)))
        {
            String line = reader.readLine();
            while(line != null) {
                options.put(line.split(":")[0].trim(), line.split(":")[1].trim());
                line = reader.readLine();
            }
        } catch (IOException ignore) {
        }
    }

    public void save()
    {
        try(BufferedWriter writer = new BufferedWriter(new FileWriter(Objects.requireNonNull(this.getClass().getClassLoader().getResource("options.txt")).getFile(), true)))
        {
            for(Map.Entry<String, String> entry : options.entrySet())
            {
                writer.append(entry.getKey()).append(" : ").append(entry.getValue()).append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
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
