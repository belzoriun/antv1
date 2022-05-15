package fr.florian.ants.antv1.map;

import fr.florian.ants.antv1.living.LivingEntity;
import fr.florian.ants.antv1.living.ant.entity.AntEntity;
import fr.florian.ants.antv1.map.tileplacer.TilePlacer;
import fr.florian.ants.antv1.ui.Application;
import fr.florian.ants.antv1.ui.WorldView;
import fr.florian.ants.antv1.util.Vector;
import fr.florian.ants.antv1.util.option.OptionKey;
import fr.florian.ants.antv1.util.pheromone.Pheromone;
import fr.florian.ants.antv1.util.pheromone.PheromoneManager;
import fr.florian.ants.antv1.util.resource.IResourcePlacer;
import fr.florian.ants.antv1.util.resource.Resource;
import javafx.application.Platform;
import javafx.scene.canvas.GraphicsContext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Singleton representing the world map
 */
public class Map {

    private final java.util.Map<Vector, Chunk> chunks;
    private final List<AntHillTile> antHills;
    private final List<LivingEntity> livings;
    private final List<Thread> cores;
    private static Map instance = null;

    private Map()
    {
        chunks = new HashMap<>();
        cores = new ArrayList<>();
        livings = new ArrayList<>();
        antHills = new ArrayList<>();
    }

    /**
     * Get all ants owned by an ant hill
     * @param uniqueId The ant hill id
     * @return The ants owned by the hill
     */
    public List<AntEntity> getAntsOf(long uniqueId) {
        List<AntEntity> res = new ArrayList<>();
        synchronized (livings) {
            for (LivingEntity l : livings) {
                if (l instanceof AntEntity a && a.getAntHillId() == uniqueId) {
                    res.add(a);
                }
            }
        }
        return res;
    }

    /**
     * Spawn a living entity
     * @param living The entity to be spawned
     * @param <T> The entity type
     */
    public <T extends LivingEntity> void spawn(T living, boolean revive)
    {
        synchronized (livings) {
            if(!revive) {
                synchronized (cores) {
                    Thread t = new Thread(living);
                    t.start();
                    cores.add(t);
                }
            }
            if (!livings.contains(living)) {
                livings.add(living);
            }
        }
    }

    public List<LivingEntity> getLivings()
    {
        return new ArrayList<>(livings);
    }

    public static Map getInstance()
    {
        if(instance == null)
        {
            instance = new Map();
        }
        return instance;
    }

    /**
     * Initialize the map
     * create all tiles ant spawning ants
     * @param placer The tile placement specifier
     */
    public void init(TilePlacer tilePlacer, IResourcePlacer placer)
    {
        chunks.clear();
        antHills.clear();
        livings.clear();
        cores.clear();
        PheromoneManager.getInstance().start();

        Platform.runLater(() -> {
            Application.showLoadingScreen("Generating map (0%)");
        });
        System.out.println("placing resources");
        for(int x = 0; x< Application.options.getInt(OptionKey.MAP_WIDTH); x++)
        {
            for(int y = 0; y<Application.options.getInt(OptionKey.MAP_HEIGHT); y++)
            {
                Vector v = new Vector(x, y);
                if(!chunks.containsKey(v))
                {
                    addChunk(v, new Chunk(v, tilePlacer, placer));
                    Platform.runLater(() -> {
                        Application.showLoadingScreen("Generating map ("+Math.round((double)chunks.size()/
                                (Application.options.getInt(OptionKey.MAP_HEIGHT)*Application.options.getInt(OptionKey.MAP_WIDTH))*100.0)+"%)");
                    });
                }
            }
        }

        Platform.runLater(() -> {
            Application.showLoadingScreen("Placing ant hills");
        });
        System.out.println("placing ant hills");
        for(int i = 0; i< Application.options.getInt(OptionKey.ANT_HILL_COUNT); i++)
        {
            Vector pos = new Vector(Application.random.nextInt(0, Application.options.getInt(OptionKey.MAP_WIDTH)*Chunk.CHUNK_SIZE),
                    Application.random.nextInt(0, Application.options.getInt(OptionKey.MAP_HEIGHT)*Chunk.CHUNK_SIZE));
            Vector chunkPos = pos.multi(1.0/Chunk.CHUNK_SIZE);
            AntHillTile hill = new AntHillTile();
            chunks.get(new Vector((int)chunkPos.getX(), (int)chunkPos.getY())).setTile(Chunk.reduceToChunkPos(pos), hill);
            hill.makeInitialSpawns(pos);
            antHills.add(hill);
        }

        new Thread(new ChunkUpdater(chunks.values().stream().toList())).start();
        System.out.println("map generation done");
    }

    /**
     * Place a tile at a position
     * @param v The position where to place the tile
     * @param t The tile to place
     */
    private void addChunk(Vector v, Chunk t)
    {
        chunks.put(v, t);
    }

    public Tile getTile(Vector pos)
    {
        if(pos == null)
        {
            return null;
        }
        if(pos.getX()<0|| pos.getX()>=Application.options.getInt(OptionKey.MAP_WIDTH)* Chunk.CHUNK_SIZE||pos.getY()<0||pos.getY()>=Application.options.getInt(OptionKey.MAP_HEIGHT)* Chunk.CHUNK_SIZE)
            return null;
        Vector chunkPos = pos.multi(1.0/Chunk.CHUNK_SIZE);
        if(chunks.get(new Vector((int)chunkPos.getX(), (int)chunkPos.getY())) == null)
        {
            return null;
        }
        return chunks.get(new Vector((int)chunkPos.getX(), (int)chunkPos.getY())).getTile(Chunk.reduceToChunkPos(pos));
    }

    public void drawTile(Vector vector, Vector displayPos, GraphicsContext graphicsContext2D) {
        Vector chunkPos = vector.multi(1.0/Chunk.CHUNK_SIZE);
        if(chunks.get(new Vector((int)chunkPos.getX(), (int)chunkPos.getY())) == null)
        {
            return;
        }
        Tile t = chunks.get(new Vector((int)chunkPos.getX(), (int)chunkPos.getY())).getTile(Chunk.reduceToChunkPos(vector));
        if(t != null)
        {
            t.draw(graphicsContext2D, displayPos);
        }
    }

    public List<AntHillTile> getAntHills()
    {
        return new ArrayList<>(antHills);
    }

    /**
     * Kill all living entities on the map
     */
    public void killAll()
    {
        for (Thread t : new ArrayList<>(cores)) {
            try {
                t.join();
            } catch (InterruptedException ignored) {
            }
        }
    }

    /**
     * Update living entities, removing dead ones
     * @return The amount of remaining living entities
     */
    public int updateLivings()
    {
        List<LivingEntity> trash = new ArrayList<>();
        synchronized (livings) {
            for (LivingEntity l : livings) {
                if (!l.isAlive()) {
                    trash.add(l);
                }
            }
            for (LivingEntity l : trash) {
                livings.remove(l);
            }
            return livings.size();
        }
    }

    /**
     * Get all living entities on a position
     * @param position The position where to look for living entities
     * @return A list of entities
     */
    public List<LivingEntity> getLivingsAt(Vector position) {
        List<LivingEntity> res = new ArrayList<>();
        synchronized (livings) {
            for (LivingEntity l : livings) {
                if (l != null && l.getPosition() != null && l.getPosition().equals(position)) {
                    res.add(l);
                }
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
            if (t instanceof ResourceHoldTile rt) {
                for (Resource r : rt.getResources()) {
                    if(r != null)
                        r.draw(context, r.getPosition().multi(WorldView.TILE_SIZE).add(displayPos));
                }
            }
        }
    }

    public Vector getTilePosition(Tile tile) {
        for(java.util.Map.Entry<Vector, Chunk> entry : chunks.entrySet())
        {
            Vector tilePos = entry.getValue().getTilePos(tile);
            if(tilePos != null)
            {
                return tilePos.add(entry.getKey().multi(Chunk.CHUNK_SIZE));
            }
        }
        return null;
    }

    public Vector getChunkPos(Chunk chunk) {
        for(java.util.Map.Entry<Vector, Chunk> entry : chunks.entrySet())
        {
            if(entry.getValue() == chunk)
            {
                return entry.getKey();
            }
        }
        return null;
    }
}
