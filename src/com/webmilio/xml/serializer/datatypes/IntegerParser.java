package com.webmilio.xml.serializer.datatypes;

public class IntegerParser extends DataParser
{
	public IntegerParser()
	{
		super(int.class, Integer.class);
	}
	
	@Override
	public void accept(String str)
	{
		value = Integer.parseInt(str);
	}
}
