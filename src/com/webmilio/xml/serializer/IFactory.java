package com.webmilio.xml.serializer;

public interface IFactory<K, V>
{
	public void register(Class<? extends V> clazz);

	public V get(K key);
}
