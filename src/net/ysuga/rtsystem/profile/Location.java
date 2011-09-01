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
	private static final String RTS_EXT_HEIGHT = "rtsExt:height";

	/**
	 * 
	 */
	private static final String RTS_EXT_WIDTH = "rtsExt:width";

	/**
	 * 
	 */
	private static final String RTS_EXT_DIRECTION = "rtsExt:direction";

	public static String RTS_EXT_X = "rtsExt:x";

	public static String RTS_EXT_Y = "rtsExt:y";
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	 
	
	public Location() {
		this(0, 0, 80, 80);
	}
	
	/**
	 * Constructor
	 */
	public Location(int x, int y, int width, int height) {
		put(RTS_EXT_DIRECTION, "RIGHT");
		put(RTS_EXT_WIDTH, Integer.toString(width));
		put(RTS_EXT_HEIGHT, Integer.toString(height));
		put(RTS_EXT_Y, Integer.toString(y));
		put(RTS_EXT_X, Integer.toString(x));
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
 
