package com.webmilio.xml.serializer;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.webmilio.xml.serializer.datatypes.DataParser;
import com.webmilio.xml.serializer.datatypes.ParserFactory;

public class XmlDeserializer
{
	private static DocumentBuilder _builder;
	private static XPath _xPath = XPathFactory.newInstance().newXPath();

	static
	{
		// I hate doing this, but I can't see how this would ever occur.
		try
		{
			_builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		}
		catch (ParserConfigurationException e)
		{
			e.printStackTrace();
		}
	}

	private static <T> T deserializeIndividual(Map<Class<?>, Map<String, Field>> mappings, Class<T> clazz,
			Element element)
			throws XmlSerializerException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException
	{
		var instance = (T) ClassHelpers.Create(clazz);

		mapClass(mappings, clazz);
		var parsers = ParserFactory.getInstance();

		NamedNodeMap attributes = element.getAttributes();

		for (int i = 0; i < attributes.getLength(); i++)
		{
			trySetValue(mappings, parsers, clazz, instance, attributes.item(i));
		}

		var elements = XmlHelpers.getChildElements(element.getChildNodes());

		if (!elements.isEmpty())
		{
			for (Element e : elements)
			{
				trySetValue(mappings, parsers, clazz, instance, e);
			}
		}
		else
		{
			Field valueField = getOrMakeFieldMapping(mappings.get(clazz), "value");

			if (valueField != null)
			{
				trySetValue(valueField, mappings, parsers, instance, element);
			}
		}

		return instance;
	}

	private static <T> void trySetValue(Map<Class<?>, Map<String, Field>> mappings, ParserFactory parsers,
			Class<T> clazz, T instance, Node node)
			throws XmlSerializerException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException
	{
		var name = node.getNodeName();
		var field = getOrMakeFieldMapping(mappings.get(clazz), name);

		if (field == null) return;

		trySetValue(field, mappings, parsers, instance, node);
	}

	private static <T> void trySetValue(Field field, Map<Class<?>, Map<String, Field>> mappings, ParserFactory parsers,
			T instance, Node node)
			throws XmlSerializerException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException
	{
		Class<?> fieldType = field.getType();
		Object fieldValue;

        try
		{
            fieldValue = field.get(instance);
        }
		catch (IllegalAccessException e)
		{
            throw new RuntimeException(e);
        }

        if (fieldType.isArray())
		{
			if (fieldValue != null)  return;

			NodeList list;

			try
			{
				list = (NodeList) _xPath.evaluate(node.getNodeName(), node.getParentNode(), XPathConstants.NODESET);
			}
			catch (Exception e)
			{
				throw new XmlSerializerException(e);
			}

			Object array = Array.newInstance(fieldType.getComponentType(), list.getLength());

			for (int i = 0; i < list.getLength(); i++)
			{
				Node item = list.item(i);
				Object value = getValue(mappings, parsers, fieldType.getComponentType(), item);

				Array.set(array, i, value);
			}

			try
			{
				field.set(instance, array);
			}
			catch (Exception e)
			{
				throw new XmlSerializerException(e);
			}
		}
		else
		{
			Object value = getValue(mappings, parsers, fieldType, node);

			try
			{
				field.set(instance, value);
			}
			catch (Exception e)
			{
				throw new XmlSerializerException(e);
			}
		}
	}

	private static Object getValue(Map<Class<?>, Map<String, Field>> mappings, ParserFactory parsers,
			Class<?> fieldType, Node node)
			throws XmlSerializerException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException
	{
		DataParser parser = parsers.get(fieldType);

		if (parser == null)
		{
			if (node.getNodeType() == Node.ELEMENT_NODE)
			{
				return deserializeIndividual(mappings, fieldType, (Element) node);
			}
		}
		else
		{
			parser.accept(node.getTextContent());
			return parser.get();
		}

		return null;
	}

	// Mapping
	private static void mapClass(Map<Class<?>, Map<String, Field>> mappings, Class<?> clazz)
	{
		if (mappings.containsKey(clazz))
		{
			return;
		}

		var classMappings = new HashMap<String, Field>();

		for (Field field : getFields(clazz))
		{
			classMappings.put(field.getName().toLowerCase(), field);
		}

		mappings.put(clazz, classMappings);
	}

	// Fields
	private static Field getOrMakeFieldMapping(Map<String, Field> map, String name)
	{
		name = name.toLowerCase();
		Field field = map.get(name);

		if (field != null)
		{
			return field;
		}

		var nString = name.replace("-", "");

		if (map.containsKey(nString))
		{
			map.put(name, field = map.get(nString));
		}

		nString += "s";

		if (map.containsKey(nString))
		{
			map.put(name, field = map.get(nString));
		}

		return field;
	}

	private static Field[] getFields(Class<?> clazz)
	{
		return clazz.getFields();
	}

	// Deserialize
	public static <T> List<T> deserialize(Class<T> clazz, String content) throws XmlSerializerException
	{
		try
		{
			return deserialize(clazz, _builder.parse(new ByteArrayInputStream(content.getBytes())));
		}
		catch (Exception e)
		{
			throw new XmlSerializerException(e);
		}
	}

	public static <T> List<T> deserialize(Class<T> clazz, File file) throws XmlSerializerException
	{
		try
		{
			return deserialize(clazz, _builder.parse(file));
		}
		catch (Exception e)
		{
			throw new XmlSerializerException(e);
		}
	}

	public static <T> List<T> deserialize(Class<T> clazz, InputSource source) throws XmlSerializerException
	{
		try
		{
			return deserialize(clazz, _builder.parse(source));
		}
		catch (Exception e)
		{
			throw new XmlSerializerException(e);
		}
	}

	public static <T> List<T> deserialize(Class<T> clazz, InputStream stream) throws XmlSerializerException
	{
		try
		{
			return deserialize(clazz, _builder.parse(stream));
		}
		catch (Exception e)
		{
			throw new XmlSerializerException(e);
		}
	}

	public static <T> List<T> deserialize(Class<T> clazz, Document document)
			throws XmlSerializerException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException
	{
		return deserialize(clazz, document.getDocumentElement());
	}

	public static <T> List<T> deserialize(Class<T> clazz, Element root)
			throws XmlSerializerException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException
	{
		List<T> instances = new ArrayList<>();
		List<Element> children = XmlHelpers.getChildElements(root.getChildNodes());

		Map<Class<?>, Map<String, Field>> mappings = new HashMap<>();

		for (int i = 0; i < children.size(); i++)
		{
			instances.add(deserializeIndividual(mappings, clazz, children.get(i)));
		}

		return instances;
	}
}
