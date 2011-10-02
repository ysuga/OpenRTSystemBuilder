/**
 * Connector.java
 * 
 * @author Yuki Suga (ysuga@ysuga.net)
 * @copyright 2011, ysuga.net allrights reserved.
 */

package net.ysuga.rtsystem.profile;

import java.awt.Point;
import java.io.IOException;

import net.ysuga.rtsbuilder.RTSystemBuilder;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Connector
 * @author ysuga
 *
 */
public class ServicePortConnector extends PortConnector {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8324464900056590583L;


	/**
	 * Constructor
	 */
	public ServicePortConnector(String connectorId, String name) {
		super();
		pivotList = new PivotList();
		put(NAME, name);
		put(TYPE, "rtsExt:serviceport_connector_ex");
		put(CONNECTOR_ID, connectorId);
	}

	public ServicePortConnector() throws IOException {
		this("defaultConnectorId", "defaultName");
	}
	/**
	 * Constructor
	 * @param node XML node.
	 * @throws IOException
	 */
	public ServicePortConnector(Node node) throws IOException {
		this();
		load(node);
		
		NodeList nodeList = node.getChildNodes();
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node cnode = nodeList.item(i);
			if (cnode.getNodeName().equals("rts:sourceServicePort")) {
				sourcePort = new Port(cnode);
			} else if (cnode.getNodeName().equals("rts:targetServicePort")) {
				targetPort = new Port(cnode);
			}
		}
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
		Element element = createElement(elementName, document);
		element.appendChild(sourcePort.getElement("rts:sourceServicePort", document));
		element.appendChild(targetPort.getElement("rts:targetServicePort", document));
		return element;
	}

	
	public void connect() throws Exception {
		RTSystemBuilder.connect(this);
	}
	
	public void disconnect() throws Exception {
		RTSystemBuilder.disconnect(this);
	}


}
