package fr.florian.ants.antv1.living;

public class Property<T> {

    private T value;
    private String name;

    protected Property(String name, T defaultValue)
    {
        this.name = name;
        value = defaultValue;
    }

    public T get()
    {
        return value;
    }


}
