package fr.florian.ants.antv1.map;

import fr.florian.ants.antv1.living.Living;
import fr.florian.ants.antv1.living.ant.Ant;
import fr.florian.ants.antv1.util.Vector;
import fr.florian.ants.antv1.util.resource.IResourcePlacer;
import fr.florian.ants.antv1.util.resource.RandomResourcePlacer;
import javafx.scene.canvas.GraphicsContext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class Map {

    public static final int WIDTH = 500;
    public static final int HEIGHT = 500;
    public static final int ANTHILL_COUNT = 3;

    private java.util.Map<Vector, Tile> tiles;
    private List<Living> livings;

    private static Map instance = null;

    private Map()
    {
        tiles = new HashMap<>();
        livings = new ArrayList<>();
    }

    public <T extends Living> T spawn(T living)
    {
        new Thread(living).start();
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

    public void init(IResourcePlacer placer)
    {
        System.out.println("placing ant hills");
        for(int i = 0; i<ANTHILL_COUNT; i++)
        {
            Vector pos = new Vector(new Random().nextInt(0, WIDTH), new Random().nextInt(0, HEIGHT));
            while(tiles.containsKey(pos)){
                pos = new Vector(new Random().nextInt(0, WIDTH), new Random().nextInt(0, HEIGHT));
            }
            AntHillTile hill = new AntHillTile();
            this.tiles.put(pos, hill);
            hill.start();
            this.tiles.put(pos.up(), placer.placeTile(pos.up()).startSelf());
            this.tiles.put(pos.down(), placer.placeTile(pos.down()).startSelf());
            this.tiles.put(pos.left(), placer.placeTile(pos.left()).startSelf());
            this.tiles.put(pos.right(), placer.placeTile(pos.right()).startSelf());
            hill.makeInitialSpawns(pos);
        }

        System.out.println("placing resources");
        for(int x = 0; x< WIDTH; x++)
        {
            for(int y = 0; y<HEIGHT; y++)
            {
                Vector v = new Vector(x, y);
                if(!tiles.containsKey(v))
                {
                    tiles.put(v, placer.placeTile(v).startSelf());
                }
            }
        }

        System.out.println("map generation done");
    }

    public Tile getTile(Vector pos)
    {
        return tiles.get(pos);
    }

    public void drawTile(Vector vector, Vector displayPos, GraphicsContext graphicsContext2D) {
        if(tiles.get(vector) != null)
        {
            tiles.get(vector).draw(graphicsContext2D, displayPos);
        }
    }
}
