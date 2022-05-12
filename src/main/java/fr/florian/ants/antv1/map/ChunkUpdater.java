package fr.florian.ants.antv1.map;

import fr.florian.ants.antv1.ui.Application;
import fr.florian.ants.antv1.util.TickWaiter;
import fr.florian.ants.antv1.util.Vector;
import fr.florian.ants.antv1.util.option.OptionKey;

import java.util.ArrayList;
import java.util.List;

public class ChunkUpdater implements Runnable{

    private List<Chunk> chunks;

    private static List<ChunkUpdateFeature> updateFeatures = new ArrayList<>();

    public static void useUpdateFeature(ChunkUpdateFeature feature)
    {
        updateFeatures.add(feature);
    }

    public ChunkUpdater(List<Chunk> c)
    {
        chunks = c;
    }

    @Override
    public void run() {
        while(TickWaiter.isLocked())
        {
            TickWaiter.waitTick();
            for(Chunk chunk : chunks) {
                for (int i = 0; i < 3; i++) {
                    Vector pos = new Vector(Application.random.nextInt(Chunk.CHUNK_SIZE), Application.random.nextInt(Chunk.CHUNK_SIZE));
                    Tile t = chunk.getTile(pos);
                    Vector chunkPos = Map.getInstance().getChunkPos(chunk);
                    if(chunkPos != null) {
                        for (ChunkUpdateFeature feature : updateFeatures) {
                            feature.call(pos.add(chunkPos.multi(new Vector(Application.options.getInt(OptionKey.MAP_WIDTH), Application.options.getInt(OptionKey.MAP_HEIGHT)))), t);
                        }
                    }
                }
            }
        }
    }
}
