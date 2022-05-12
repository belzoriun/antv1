package fr.florian.ants.antv1.map;

import fr.florian.ants.antv1.util.Vector;
import fr.florian.ants.antv1.util.resource.IResourcePlacer;

import java.util.HashMap;

public class Chunk {

    public static final int CHUNK_SIZE = 16;

    private java.util.Map<Vector, Tile> tiles;

    public static Vector reduceToChunkPos(Vector v)
    {
        return new Vector((int)(v.getX()%CHUNK_SIZE), (int)(v.getY()%CHUNK_SIZE));
    }

    public Chunk(Vector pos, IResourcePlacer placer)
    {
        tiles = new HashMap<>();
        for(int x = 0; x<CHUNK_SIZE; x++)
        {
            for(int y = 0; y<CHUNK_SIZE; y++)
            {
                Vector tilePos = new Vector(x, y);
                tiles.put(tilePos, placer.placeTile(tilePos.add(pos.multi(CHUNK_SIZE))));
            }
        }
    }

    public Tile getTile(Vector pos)
    {
        return tiles.get(pos);
    }

    public void setTile(Vector v, Tile hill) {
        if(v.getX()>=0 && v.getX()<CHUNK_SIZE && v.getY()>=0 && v.getY()<CHUNK_SIZE)
        {
            tiles.put(v, hill);
        }
    }

    public Vector getTilePos(Tile tile) {
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
