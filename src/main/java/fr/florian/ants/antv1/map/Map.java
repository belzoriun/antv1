package fr.florian.ants.antv1.map;

import fr.florian.ants.antv1.living.Living;
import fr.florian.ants.antv1.living.ant.Ant;
import fr.florian.ants.antv1.living.ant.DeadAnt;
import fr.florian.ants.antv1.ui.Application;
import fr.florian.ants.antv1.ui.WorldView;
import fr.florian.ants.antv1.util.option.OptionKey;
import fr.florian.ants.antv1.util.pheromone.Pheromone;
import fr.florian.ants.antv1.util.pheromone.PheromoneManager;
import fr.florian.ants.antv1.util.TickAwaiter;
import fr.florian.ants.antv1.util.Vector;
import fr.florian.ants.antv1.util.resource.IResourcePlacer;
import fr.florian.ants.antv1.util.resource.Resource;
import javafx.scene.canvas.GraphicsContext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class Map {

    private java.util.Map<Vector, Tile> tiles;
    private List<AntHillTile> antHills;
    private List<Living> livings;
    private List<Thread> lives;
    private List<DeadAnt> deadAnts;
    private static Map instance = null;

    private Map()
    {
        lives = new ArrayList<>();
        tiles = new HashMap<>();
        livings = new ArrayList<>();
        antHills = new ArrayList<>();
        deadAnts = new ArrayList<>();
    }

    public List<Ant> getAntsOf(long uniqueId) {
        List<Ant> res = new ArrayList<>();
        for(Living l : livings)
        {
            if(l instanceof Ant a && a.getAntHillId() == uniqueId)
            {
                res.add(a);
            }
        }
        return res;
    }

    public <T extends Living> T spawn(T living)
    {
        Thread t = new Thread(living);
        t.start();
        lives.add(t);
        livings.add(living);
        return living;
    }

    public List<Living> getLivings()
    {
        return livings;
    }

    public static Map getInstance()
    {
        if(instance == null)
        {
            instance = new Map();
        }
        return instance;
    }

    public static void anihilate()
    {
        instance = null;
    }

    public void init(IResourcePlacer placer)
    {
        PheromoneManager.getInstance().start();
        System.out.println("placing ant hills");
        for(int i = 0; i< Application.options.getInt(OptionKey.ANT_HILL_COUNT); i++)
        {
            Vector pos = new Vector(Application.random.nextInt(0, Application.options.getInt(OptionKey.MAP_WIDTH)), Application.random.nextInt(0, Application.options.getInt(OptionKey.MAP_HEIGHT)));
            while(tiles.containsKey(pos)){
                pos = new Vector(Application.random.nextInt(0, Application.options.getInt(OptionKey.MAP_WIDTH)), Application.random.nextInt(0, Application.options.getInt(OptionKey.MAP_HEIGHT)));
            }
            AntHillTile hill = new AntHillTile();
            addTile(pos, hill);
            addTile(pos.up(), placer.placeTile(pos.up()));
            addTile(pos.down(), placer.placeTile(pos.down()));
            addTile(pos.left(), placer.placeTile(pos.left()));
            addTile(pos.right(), placer.placeTile(pos.right()));
            hill.makeInitialSpawns(pos);
            antHills.add(hill);
        }

        System.out.println("placing resources");
        for(int x = 0; x< Application.options.getInt(OptionKey.MAP_WIDTH); x++)
        {
            for(int y = 0; y<Application.options.getInt(OptionKey.MAP_HEIGHT); y++)
            {
                Vector v = new Vector(x, y);
                if(!tiles.containsKey(v))
                {
                    addTile(v, placer.placeTile(v));
                }
            }
        }

        System.out.println("map generation done");
    }

    private void addTile(Vector v, Tile t)
    {
        tiles.put(v, t);
    }

    public Tile getTile(Vector pos)
    {
        if(pos.getX()<0|| pos.getX()>Application.options.getInt(OptionKey.MAP_WIDTH)||pos.getY()<0||pos.getY()>Application.options.getInt(OptionKey.MAP_HEIGHT))
            return  null;
        return tiles.get(pos);
    }

    public void drawTile(Vector vector, Vector displayPos, GraphicsContext graphicsContext2D) {
        if(tiles.get(vector) != null)
        {
            tiles.get(vector).draw(graphicsContext2D, displayPos);
        }
    }

    public List<AntHillTile> getAntHills()
    {
        return new ArrayList<>(antHills);
    }

    public void killAll()
    {
        for(Living l : livings)
        {
            new Thread(l::kill).start();
        }
    }

    public int updateLivings()
    {
        List<Living> trash = new ArrayList<>();
        for(Living l : livings)
        {
            if(!l.isAlive())
            {
                trash.add(l);
            }
        }
        for(Living l : trash)
        {
            synchronized (l)
            {
                livings.remove(l);
            }
        }
        return livings.size();
    }

    public List<Living> getLivingsAt(Vector position) {
        List<Living> res = new ArrayList<>();
        for (Living l : livings) {
            if (l.getPosition().equals(position)) {
                res.add(l);
            }
        }
        return res;
    }

    public void drawPheromones(Vector pos, Vector displayPos, GraphicsContext context) {
        Tile t = getTile(pos);
        if(t != null) {
            for(Pheromone p : t.getAllPheromones())
            {
                if(p != null)
                    p.draw(context, displayPos);
            }
        }
    }

    public void displayResources(GraphicsContext context, Vector pos, Vector displayPos) {
        Tile t = getTile(pos);
        if(t != null) {
            if(t instanceof ResourceTile rt) {
                for (Resource r : rt.getResources()) {
                    r.draw(context, r.getPosition().mult(WorldView.TILE_SIZE).add(displayPos));
                }
            }
        }
    }

    public void addDeadAnt(DeadAnt d) {
        deadAnts.add(d);
    }

    public List<DeadAnt> getDeadAnts()
    {
        return deadAnts;
    }

    public Vector getTilePosition(Tile tile) {
        for(java.util.Map.Entry<Vector, Tile> entry : tiles.entrySet())
        {
            if(entry.getValue() == tile)
            {
                return entry.getKey();
            }
        }
        return null;
    }
}
