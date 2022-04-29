package fr.florian.ants.antv1.map;

import fr.florian.ants.antv1.living.Living;
import fr.florian.ants.antv1.living.ant.Ant;
import fr.florian.ants.antv1.util.PheromoneManager;
import fr.florian.ants.antv1.util.TickAwaiter;
import fr.florian.ants.antv1.util.Vector;
import fr.florian.ants.antv1.util.resource.IResourcePlacer;
import fr.florian.ants.antv1.util.resource.RandomResourcePlacer;
import javafx.scene.canvas.GraphicsContext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class Map {

    public static final int WIDTH = 50;
    public static final int HEIGHT = 50;
    public static final int ANTHILL_COUNT = 5;

    private java.util.Map<Vector, Tile> tiles;
    private List<AntHillTile> antHills;
    private List<Living> livings;
    private List<Thread> lives;

    private static Map instance = null;

    private Map()
    {
        lives = new ArrayList<>();
        tiles = new HashMap<>();
        livings = new ArrayList<>();
        antHills = new ArrayList<>();
    }

    public <T extends Living> T spawn(T living)
    {
        Thread t = new Thread(living);
        t.start();
        lives.add(t);
        livings.add(living);
        return living;
    }

    public Ant spawnAnt(Ant a)
    {
        return spawn(a);
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
        for(int i = 0; i<ANTHILL_COUNT; i++)
        {
            Vector pos = new Vector(new Random().nextInt(0, WIDTH), new Random().nextInt(0, HEIGHT));
            while(tiles.containsKey(pos)){
                pos = new Vector(new Random().nextInt(0, WIDTH), new Random().nextInt(0, HEIGHT));
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
        for(int x = 0; x< WIDTH; x++)
        {
            for(int y = 0; y<HEIGHT; y++)
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
        if(pos.getX()<0|| pos.getX()>WIDTH||pos.getY()<0||pos.getY()>HEIGHT)
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
        return antHills;
    }

    public void killAll()
    {
        for(Living l : livings)
        {
            synchronized (l) {
                l.kill();
            }
        }
        TickAwaiter.emitTick();
        for(Thread t : lives)
        {
            try {
                t.join();
            } catch (InterruptedException e) {
            }
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
}
