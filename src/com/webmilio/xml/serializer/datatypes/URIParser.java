package com.webmilio.xml.serializer.datatypes;

import java.net.URI;

public class URIParser extends DataParser
{	
	public URIParser()
	{
		super(URI.class);
	}
	
	@Override
	public void accept(String str)
	{
		value = URI.create(str);
	}
}
