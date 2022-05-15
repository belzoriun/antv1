package fr.florian.ants.antv1.util.registry;

public class DefaultedRegistry<T> extends Registry<T>{
	
	private Identifier defaultId;

	protected DefaultedRegistry(RegistryKey<? extends Registry<T>> key, Identifier defaultId) {
		super(key);
		this.defaultId = defaultId;
	}

	@Override
	public T get(Identifier id) {
		if(!idEntries.containsKey(id))
		{
			return idEntries.get(defaultId);
		}
		return idEntries.get(id);
	}
	
	public Identifier getDefaultId()
	{
		return defaultId;
	}
	
	public T getDefaultValue()
	{
		return idEntries.get(defaultId);
	}

	@Override
	public Identifier getId(T value) {
		if(!values.inverse().containsKey(value))
		{
			return defaultId;
		}
		return values.inverse().get(value).getValue();
	}

}
