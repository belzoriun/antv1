package fr.florian.ants.antv1.util;

import java.util.Objects;

/**
 * Class used as a vector / point management
 */
public record Vector(double x, double y) {

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public Vector add(Vector v) {
        return new Vector(this.x + v.getX(), this.y + v.getY());
    }

    public Vector add(double v) {
        return new Vector(this.x + v, this.y + v);
    }

    public String toString() {
        return "(" + this.x + ";" + this.y + ")";
    }

    public Vector multi(double v) {
        return new Vector(x * v, y * v);
    }

    public Vector up() {
        return new Vector(this.x, this.y - 1);
    }

    public Vector down() {
        return new Vector(this.x, this.y + 1);
    }

    public Vector left() {
        return new Vector(this.x - 1, this.y);
    }

    public Vector right() {
        return new Vector(this.x + 1, this.y);
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
        return Math.sqrt(Math.pow(from.getX() - x, 2) + Math.pow(from.getY() - y, 2));
    }

    public double angle(Vector vector) {
        return Math.acos(vector.scalar(this) / (magnitude() * vector.magnitude())) * 180 / Math.PI;
    }
    public double angle(Vector vector, boolean rad) {
        if(!rad)
        {
            return angle(vector);
        }
        return Math.acos(vector.scalar(this) / (magnitude() * vector.magnitude()));
    }

    public double scalar(Vector v) {
        return v.getX() * x + v.getY() * y;
    }

    public double magnitude() {
        return Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));
    }
}
