package fr.florian.ants.antv1.util;

import java.util.Random;

public enum Direction {

    UP(new Vector(0, -1)), DOWN(new Vector(0, 1)), LEFT(new Vector(-1, 0)), RIGHT(new Vector(1, 0));

    private Vector offset;

    private Direction(Vector offset)
    {
        this.offset = offset;
    }

    public static Direction fromOffset(Vector add) {
        for(Direction dir : Direction.values())
        {
            if(dir.getOffset().equals(add))
            {
                return dir;
            }
        }
        return null;
    }

    public Vector getOffset()
    {
        return this.offset;
    }

    public static Direction random()
    {
        Direction[] dirs = Direction.values();
        return dirs[new Random().nextInt(0, dirs.length)];
    }
}
