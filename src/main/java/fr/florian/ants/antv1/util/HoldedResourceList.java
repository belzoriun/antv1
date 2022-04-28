package fr.florian.ants.antv1.util;

import fr.florian.ants.antv1.util.resource.Resource;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class HoldedResourceList {

    private List<Resource> resources;
    private int capacity;

    public HoldedResourceList(int capacity)
    {
        this.capacity = capacity;
        resources = new ArrayList<>();
    }

    public void add(Resource r) throws Exception {
        if(isFull()) throw new Exception("Unable to add a resource : max capacity reach");
        resources.add(r);
    }

    public Resource remove()
    {
        return resources.remove(resources.size()-1);
    }

    public boolean isFull()
    {
        return resources.size()==capacity;
    }

    public boolean isEmpty() {
        return resources.isEmpty();
    }

    public Collection<? extends Resource> getAll() {
        return resources;
    }
}
