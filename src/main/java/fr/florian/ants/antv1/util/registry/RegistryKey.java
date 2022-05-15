package fr.florian.ants.antv1.util.registry;

import java.util.Collections;
import java.util.Map;

import com.google.common.collect.Maps;

public class RegistryKey<T> {
	private Identifier registry;
	private Identifier value;
	
	private static final Map<String, RegistryKey<?>> INSTANCES = Collections.synchronizedMap(Maps.newIdentityHashMap());;
	
	private RegistryKey(Identifier registry, Identifier value)
	{
		this.registry = registry;
		this.value=value;
	}
	
	public static <T> RegistryKey<T> of(Identifier registry, Identifier value)
	{
		String id = (registry+":"+value).intern();
		return (RegistryKey<T>) INSTANCES.computeIfAbsent(id, string->new RegistryKey<T>(registry, value));
	}
	
	public static <T> RegistryKey<T> of(RegistryKey<? extends RegistryKey<T>> registry, Identifier id)
	{
		return RegistryKey.of(registry.value, id);
	}
	
	public static <T> RegistryKey<T> ofRegistry(Identifier registry)
	{
		return RegistryKey.of(Registry.ROOT_KEY, registry);
	}
	
	public boolean isOf(RegistryKey<? extends Registry<T>> registry)
	{
		return this.registry.equals(registry.value);
	}

	public Identifier getValue() {
		return value;
	}
	
	public String toString()
	{
		return this.registry+":"+this.value;
	}
}
