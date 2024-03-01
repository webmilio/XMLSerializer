package com.webmilio.xml.serializer.datatypes;

public class DoubleParser extends DataParser
{
	public DoubleParser()
	{
		super(double.class, Double.class);
	}
	
	@Override
	public void accept(String str)
	{
		value = Double.parseDouble(str);
	}
}
