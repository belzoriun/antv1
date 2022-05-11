package fr.florian.ants.antv1.map;

import fr.florian.ants.antv1.ui.Application;
import fr.florian.ants.antv1.util.TickWaiter;
import fr.florian.ants.antv1.util.Vector;

import java.util.ArrayList;
import java.util.List;

public class ChunkUpdater implements Runnable{

    private Chunk chunk;

    private static List<ChunkUpdateFeature> updateFeatures = new ArrayList<>();

    public static void useUpdateFeature(ChunkUpdateFeature feature)
    {
        updateFeatures.add(feature);
    }

    public ChunkUpdater(Chunk c)
    {
        chunk = c;
    }

    @Override
    public void run() {
        while(TickWaiter.isLocked())
        {
            TickWaiter.waitTick(this);
            for(int i = 0; i<3; i++)
            {
                Vector pos = new Vector(Application.random.nextInt(Chunk.CHUNK_SIZE), Application.random.nextInt(Chunk.CHUNK_SIZE));
                Tile t = chunk.getTile(pos);
                for(ChunkUpdateFeature feature : updateFeatures)
                {
                    feature.call(pos,t);
                }
            }
        }
    }
}
