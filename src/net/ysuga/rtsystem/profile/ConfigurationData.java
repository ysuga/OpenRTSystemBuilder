/**
 * ConfigurationData.java
 * 
 * @author Yuki Suga (ysuga@ysuga.net)
 * @copyright 2011, ysuga.net allrights reserved.
 */

package net.ysuga.rtsystem.profile;

import java.io.IOException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * ConfigurationData
 * @author ysuga
 *
 */
public class ConfigurationData extends RTSProperties {
 
	 
	/**
	 * 
	 */
	public static final String DATA = "rts:data";
	/**
	 * 
	 */
	public static final String NAME = "rts:name";
	/**
	 * 
	 */
	private static final long serialVersionUID = -6692006581951546773L;

	/**
	 * Constructor
	 */
	public ConfigurationData(String name, String data) {
		super();
		put(NAME, name);
		put(DATA, data);
	}

	/**
	 * Constructor 
	 * @param node
	 * @throws IOException
	 */
	public ConfigurationData(Node node) throws IOException {
		this("defaultName", "defaultData");
		load(node);
	}

	/* (non-Javadoc)
	 * @see net.ysuga.rtsystem.profile.RTSProperties#getElement(java.lang.String, org.w3c.dom.Document)
	 */
	@Override
	/**
	 * XML data Save helper function
	 * @param elementName XML format element name
	 * @param document XML document object
	 */	
	public Element getElement(String elementName, Document document) {
		return createElement(elementName, document);
	}
	 
}
 
