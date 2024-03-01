package com.webmilio.xml.serializer;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

public abstract class Factory<K, V>
{
	protected final HashMap<K, Class<? extends V>> items = new HashMap<>();

	public void register(Class<? extends V> clazz)
			throws InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException
	{
		V item = ClassHelpers.Create(clazz);
		register(clazz, item);
	}
	
	protected abstract void register(Class<? extends V> clazz, V instance);

	public V get(K key)
			throws InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException
	{
		if (items.containsKey(key))
		{
		return ClassHelpers.Create(items.get(key));
		}

		return null;
	}
	
}
