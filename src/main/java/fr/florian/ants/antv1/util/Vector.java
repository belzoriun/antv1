package fr.florian.ants.antv1.util;

import java.util.Objects;

/**
 * Class used as a vector / point management
 */
public class Vector {
    private double x;
    private double y;

    public Vector(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public Vector add(Vector v)
    {
        return new Vector(this.x+v.getX(), this.y+v.getY());
    }

    public Vector add(double v)
    {
        return new Vector(this.x+v, this.y+v);
    }

    public String toString()
    {
        return "("+this.x+";"+this.y+")";
    }

    public Vector multi(double v)
    {
        return new Vector(x*v, y*v);
    }

    public Vector up()
    {
        return new Vector(this.x, this.y-1);
    }

    public Vector down()
    {
        return new Vector(this.x, this.y+1);
    }

    public Vector left()
    {
        return new Vector(this.x-1, this.y);
    }

    public Vector right()
    {
        return new Vector(this.x+1, this.y);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Vector vector = (Vector) o;
        return x == vector.x && y == vector.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    public double delta(Vector from) {
        return Math.sqrt(Math.pow(from.getX()-x, 2)+Math.pow(from.getY() - y, 2));
    }
}
