package com.webmilio.xml.serializer;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class XmlHelpers
{
	public static List<Element> getChildElements(NodeList nodes)
	{
		List<Element> elements = new ArrayList<>(nodes.getLength());
		
		for (var i = 0; i < nodes.getLength(); i++)
		{
			Node node = nodes.item(i);
			
			if (node.getNodeType() == Node.ELEMENT_NODE)
			{
				elements.add((Element) node);
			}
		}
		
		return elements;
	}
}
