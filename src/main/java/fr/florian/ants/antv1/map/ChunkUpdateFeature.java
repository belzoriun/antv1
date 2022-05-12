package fr.florian.ants.antv1.map;

import fr.florian.ants.antv1.ui.Application;
import fr.florian.ants.antv1.util.Vector;
import fr.florian.ants.antv1.util.resource.Resource;

public interface ChunkUpdateFeature {
    public void call(Vector v, Tile t);

    public static class ResourceUpdateFeature implements ChunkUpdateFeature{

        Resource resource;

        public ResourceUpdateFeature(Resource resource)
        {
            this.resource = resource;
        }

        @Override
        public void call(Vector v, Tile t) {
            if(t instanceof ResourceTile rt) {
                double xMin = 0.3;
                double xMax = 0.7;
                double yMin = 0.3;
                double yMax = 0.7;
                if (Application.random.nextDouble() > resource.getRarity() && Application.random.nextDouble()<0.05) {
                    Vector pos = new Vector(Application.random.nextDouble(xMin, xMax), Application.random.nextDouble(yMin, yMax));
                    rt.placeResource(resource.clone(pos));
                }
            }
        }
    }
}
