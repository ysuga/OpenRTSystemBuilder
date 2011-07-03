/**
 * RTSProperties.java
 * 
 * @author Yuki Suga (ysuga@ysuga.net)
 * @copyright 2011, ysuga.net allrights reserved.
 */

package net.ysuga.rtsystem.profile;

import java.io.IOException;
import java.util.HashMap;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/**
 * RTSProperties
 * @author ysuga
 *
 */
public abstract class RTSProperties extends HashMap<String, String>{

	/**
	 * 
	 */
	private static final long serialVersionUID = -5882683826959094864L;

	/**
	 * Constructor
	 */
	public RTSProperties() {
		super();
	}
	
	/**
	 * Load XML Node (attribute only)
	 * @param node xml node.
	 * @throws IOException
	 */
	final public void load(Node node) throws IOException{
		NamedNodeMap attribute = node.getAttributes();
		for(String name: keySet()) {
			Node attributeNode = attribute.getNamedItem(name);
			if(attributeNode == null) {
				throw new IOException();
			}
			put(name, attributeNode.getNodeValue());
		}
	}
	
	/**
	 * Create XML node. attribute is automatically appended.
	 * @param elementName XML tag name of element 
	 * @param document XML document object
	 * @return Element object.
	 */
	final public Element createElement(String elementName, Document document) {
		Element element = document.createElement(elementName);
		for(String name: keySet()) {
			String value = get(name);
			element.setAttribute(name, value);
		}
		return element;
	}
	
	/**
	 * Pure virtual function for offspring classes.
	 * Return well build xml element object.
	 * 
	 * createElement method appends only attributes (Map member).
	 * So, offspring classes must override this method and in this method,
	 * offspring class must manually append their child elements.
	 * 
	 * @param elementName XML tag name of element 
	 * @param document XML document object
	 * @return Element object 
	 */
	public abstract Element getElement(String elementName, Document document);
}
 