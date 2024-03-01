package com.webmilio.xml.serializer.datatypes;

public class BooleanParser extends DataParser
{
	public BooleanParser()
	{
		super(boolean.class, Boolean.class);
	}

	@Override
	public void accept(String str)
	{
		value = Boolean.parseBoolean(str);
	}
}
