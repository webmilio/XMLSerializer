package com.webmilio.xml.serializer;

public class XmlSerializerException extends Exception
{
	private static final long serialVersionUID = -4328254876684962033L;
	private final Exception _innerException;
	
	public XmlSerializerException(Exception innerException)
	{
		_innerException = innerException;
	}
	
	public Exception getInnerException()
	{
		return _innerException;
	}
}
