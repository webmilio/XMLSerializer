package com.webmilio.xml.serializer.datatypes;

public class FloatParser extends DataParser
{
	public FloatParser()
	{
		super(float.class, Float.class);
	}

	@Override
	public void accept(String str)
	{
		value = Float.parseFloat(str);
	}
}
