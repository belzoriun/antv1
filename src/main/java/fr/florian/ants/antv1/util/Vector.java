package fr.florian.ants.antv1.util;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;

import java.util.Objects;

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

    public Vector inverse()
    {
        this.x *= (-1);
        this.y *= (-1);
        return this;
    }

    public String toString()
    {
        return "("+this.x+";"+this.y+")";
    }

    public Vector unit()
    {
        float mag = magnitude();
        this.x/=(mag);
        this.y/=(mag);
        return this;
    }

    public Vector mult(double v)
    {
        return new Vector(x*v, y*v);
    }

    public float magnitude()
    {
        return Math.round(Math.sqrt(Math.pow(this.x, 2)+Math.pow(this.y, 2)));
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
}
