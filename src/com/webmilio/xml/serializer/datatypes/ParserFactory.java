package com.webmilio.xml.serializer.datatypes;

import com.webmilio.xml.serializer.Factory;

public class ParserFactory extends Factory<Class<?>, DataParser>
{
	private static ParserFactory _instance;
	
	private ParserFactory()
	{
		initialize();
	}
	
	private void initialize()
	{
		try
		{
			register(BooleanParser.class);

			register(FloatParser.class);
			register(DoubleParser.class);
			register(IntegerParser.class);
			register(LongParser.class);

			register(StringParser.class);
			register(URIParser.class);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	@Override
	protected void register(Class<? extends DataParser> clazz, DataParser instance)
	{
		for (var clazzType : instance.getType())
		{
			items.put(clazzType, clazz);
		}
	}
	
	public static ParserFactory getInstance()
	{
		if (_instance == null)
		{
			_instance = new ParserFactory();
		}
		
		return _instance;
	}
}
