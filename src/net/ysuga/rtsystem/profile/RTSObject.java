/**
 * RTSObject.java
 *
 * @author Yuki Suga (ysuga.net)
 * @date 2011/10/06
 * @copyright 2011, ysuga.net allrights reserved.
 *
 */
package net.ysuga.rtsystem.profile;

import java.awt.Point;
import java.io.IOException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * 
 * @author ysuga
 * 
 */
public abstract class RTSObject extends RTSProperties {

	/**
	 * Location
	 */
	public Location location;

	/**
	 * Constructor
	 */
	public RTSObject() {
		location = new Location();
	}

	/**
	 * setLocation <div lang="ja">
	 * 
	 * @param point
	 *            </div> <div lang="en">
	 * 
	 * @param point
	 *            </div>
	 */
	public void setLocation(Point point) {
		this.location.put(Location.RTS_EXT_X, Integer.toString(point.x));
		this.location.put(Location.RTS_EXT_Y, Integer.toString(point.y));
	}

	public void load(Node node) throws IOException {
		super.load(node);

		NodeList nodeList = node.getChildNodes();
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node cnode = nodeList.item(i);
			if (cnode.getNodeName().equals("rtsExt:Location")) {
				location = new Location(cnode);
			}
		}
	}
	
	@Override
	public Element getElement(String elementName, Document document) {
		Element element = createElement(elementName, document);
		element.appendChild(location.getElement("rtsExt:Location", document));
		return element;
	}

	/**
	 * downwardSynchronize
	 * @throws Exception 
	 *
	 */
	abstract public void downwardSynchronize() throws Exception;
}
