/**
 * ExecutionContext.java
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
 * ExecutionContext
 * @author ysuga
 *
 */
public class ExecutionContext extends RTSProperties {
	 
	/**
	 * 
	 */
	public static final String RTS_ID = "rts:id";
	/**
	 * 
	 */
	public static final String RTS_KIND = "rts:kind";
	/**
	 * 
	 */
	public static final String RTS_RATE = "rts:rate";
	/**
	 * 
	 */
	public static final String XSI_TYPE = "xsi:type";
	/**
	 * 
	 */
	private static final long serialVersionUID = -1753850415115620108L;

	/**
	 * Constructor
	 * 
	 * @param node XML node.
	 * @throws IOException Invalid XML format
	 */
	public ExecutionContext(Node node) throws IOException {
		this("0", "rtsExt:execution_context_ext", "PERIODIC", "1000");
		load(node);
	}

	/**
	 * Default Constructor
	 */
	public ExecutionContext(String id, String type, String kind, String rate) {
		super();
		put(XSI_TYPE, type );
		put(RTS_RATE, rate);
		put(RTS_KIND, kind);
		put(RTS_ID, id);
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
 
