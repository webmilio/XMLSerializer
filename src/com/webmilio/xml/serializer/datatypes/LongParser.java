package com.webmilio.xml.serializer.datatypes;

public class LongParser extends DataParser
{
	public LongParser()
	{
		super(long.class, Long.class);
	}

	@Override
	public void accept(String str)
	{
		value = Long.parseLong(str);
	}
}
