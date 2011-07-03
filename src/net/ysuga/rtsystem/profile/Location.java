/**
 * Location.java
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
 * Location
 * @author ysuga
 *
 */
public class Location extends RTSProperties {
 
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	 
	/**
	 * Constructor
	 */
	public Location(int x, int y, int width, int height) {
		put("rtsExt:direction", "RIGHT");
		put("rtsExt:width", Integer.toString(width));
		put("rtsExt:height", Integer.toString(height));
		put("rtsExt:y", Integer.toString(y));
		put("rtsExt:x", Integer.toString(x));
	}
		
	/**
	 * Constructor
	 * @param node
	 * @throws IOException
	 */
	public Location(Node node) throws IOException {
		this(-1, -1, -1, -1);
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
 
