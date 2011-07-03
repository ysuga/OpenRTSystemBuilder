/**
 * Properties.java
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
 * Properties
 * @author ysuga
 *
 */
public class Properties extends RTSProperties {
	
	/**
	 * 
	 */
	public static final String VALUE = "rtsExt:value";
	/**
	 * 
	 */
	public static final String NAME = "rtsExt:name";
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Constructor
	 */
	public Properties(String name, String value) {
		put(NAME, name);
		put(VALUE, value);
	}
	
	/**
	 * Constructor
	 * @param node xml node
	 * @throws IOException
	 */
	public Properties(Node node) throws IOException {
		this("defaultName", "defaultValue");
		load(node);
	}

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