package com.webmilio.xml.serializer.datatypes;

import java.util.function.Consumer;
import java.util.function.Supplier;

public abstract class DataParser implements Consumer<String>, Supplier<Object>
{
	private final Class<?>[] _classes;
	protected Object value;
	
	public DataParser(Class<?>... classes)
	{
		if (classes.length == 0)
			throw new IllegalArgumentException(String.format("Registered a DataParser class %s with no data type. This DataParser will not work.", 
					getClass().getSimpleName()));
		
		_classes = classes;
	}
	
	@Override
	public abstract void accept(String str);
	
	@Override
	public Object get()
	{
		return value;
	}
	
	public Class<?>[] getType()
	{
		return _classes;
	}
}
