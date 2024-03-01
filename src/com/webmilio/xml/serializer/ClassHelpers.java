package com.webmilio.xml.serializer;

import java.lang.reflect.InvocationTargetException;

public class ClassHelpers
{
	public static <T> T Create(Class<T> clazz) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException
	{
		return clazz.getConstructor().newInstance();
	}
}
