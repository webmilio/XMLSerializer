package com.webmilio.xml.serializer.datatypes;

public class StringParser extends DataParser
{
	public StringParser()
	{
		super(String.class);
	}

	@Override
	public void accept(String str)
	{
		value = str;
	}
}
