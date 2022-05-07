package fr.florian.ants.antv1.util;

import fr.florian.ants.antv1.ui.Application;

/**
 * Enum listing possible directions
 */
public enum Direction {

    UP(new Vector(0, -1)), DOWN(new Vector(0, 1)), LEFT(new Vector(-1, 0)), RIGHT(new Vector(1, 0));

    private final Vector offset;

    Direction(Vector offset)
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

    /**
     * Pick a random direction from the enum
     * @return A random direction
     */
    public static Direction random()
    {
        Direction[] dirs = Direction.values();
        return dirs[Application.random.nextInt(0, dirs.length)];
    }
}
