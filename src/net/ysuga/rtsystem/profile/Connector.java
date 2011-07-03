/**
 * Connector.java
 * 
 * @author Yuki Suga (ysuga@ysuga.net)
 * @copyright 2011, ysuga.net allrights reserved.
 */

package net.ysuga.rtsystem.profile;

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
public class Connector extends RTSProperties {

	/**
	 * 
	 */
	public static final String SUBSCRIPTION_TYPE = "rts:subscriptionType";

	/**
	 * 
	 */
	public static final String DATAFLOW_TYPE = "rts:dataflowType";

	/**
	 * 
	 */
	public  static final String INTERFACE_TYPE = "rts:interfaceType";

	public  static final String DATA_TYPE = "rts:dataType";

	public  static final String CONNECTOR_ID = "rts:connectorId";

	public  static final String NAME = "rts:name";

	/**
	 * 
	 */
	private static final long serialVersionUID = 8324464900056590583L;
	
	/**
	 * Source Data Port
	 */
	public DataPort sourceDataPort;
	
	/**
	 * Target Data Port
	 */
	public DataPort targetDataPort;

	/**
	 * DataPort
	 * @author ysuga
	 *
	 */
	public class DataPort extends RTSProperties{

		/**
		 * 
		 */
		public static final String COMPONENT_ID = "rts:componentId";

		/**
		 * 
		 */
		public static final String TYPE = "xsi:type";

		/**
		 * 
		 */
		public static final String PORT_NAME = "rts:portName";

		/**
		 * 
		 */
		public static final String INSTANCE_NAME = "rts:instanceName";

		/**
		 * 
		 */
		private static final long serialVersionUID = 6914733543269380440L;
		
		/**
		 * Properties
		 */
		public Properties properties;
		
		/**
		 * Constructor
		 */
		public DataPort() {
			super();
			put(INSTANCE_NAME, "defaultInstanceName");
			put(PORT_NAME, "defaultPortName");
			put(TYPE, "defaultType");
			put(COMPONENT_ID, "defaultComponentId");
		}


		/**
		 * Constructor
		 * @param node XML node.
		 */
		public DataPort(Node node) throws IOException {
			this();
			load(node);
			
			NodeList nodeList = node.getChildNodes();
			for (int i = 0; i < nodeList.getLength(); i++) {
				Node cnode = nodeList.item(i);
				if (cnode.getNodeName().equals("rtsExt:Properties")) {
					properties = new Properties(cnode);
				}
			}		
		}

		@Override
		/**
		 * XML data Save helper function
		 * @param elementName XML format element name
		 * @param document XML document object
		 */
		public Element getElement(String elementName, Document document) {
			Element element = createElement(elementName, document);
			element.appendChild(properties.getElement("rtsExt:Properties", document));
			return element;
		}
	}
	
	/**
	 * Constructor
	 */
	public Connector() {
		super();
		put(INTERFACE_TYPE, "defaultInterfaceType");
		put(DATAFLOW_TYPE, "defaultDataflowType");
		put(SUBSCRIPTION_TYPE, "defaultSubscriptionType");
		put(NAME, "defaultName");
		put(CONNECTOR_ID, "defaultConnectorId");
		put(DATA_TYPE, "defaultDataType");
	}

	/**
	 * Constructor
	 * @param node XML node.
	 * @throws IOException
	 */
	public Connector(Node node) throws IOException {
		this();
		load(node);
		
		NodeList nodeList = node.getChildNodes();
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node cnode = nodeList.item(i);
			if (cnode.getNodeName().equals("rts:sourceDataPort")) {
				sourceDataPort = new DataPort(cnode);
			} else if (cnode.getNodeName().equals("rts:targetDataPort")) {
				targetDataPort = new DataPort(cnode);
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
		element.appendChild(sourceDataPort.getElement("rts:sourceDataPort", document));
		element.appendChild(targetDataPort.getElement("rts:targetDataPort", document));
		return null;
	}

	
	public String getSourceComponentInstanceName() {
		return sourceDataPort.get(Connector.DataPort.INSTANCE_NAME);
	}
	
	public String getSourceComponentPathUri() {
		return sourceDataPort.properties.get(Properties.VALUE);
	}
	
	public String getTargetComponentInstanceName() {
		return targetDataPort.get(Connector.DataPort.INSTANCE_NAME);
	}

	public String getTargetComponentPathUri() {
		return targetDataPort.properties.get(Properties.VALUE);
	}
	/**
	 * @return
	 */
	public String getSourceDataPortName() {
		return sourceDataPort.get(Connector.DataPort.PORT_NAME);
	}

	/**
	 * 
	 * @return
	 */
	public String getTargetDataPortName() {
		return targetDataPort.get(Connector.DataPort.PORT_NAME);
	}
	
	public void connect() throws Exception {
		RTSystemBuilder.connect(this);
	}
	
	public void disconnect() throws Exception {
		RTSystemBuilder.disconnect(this);
	}
}
