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
public class DataPortConnector extends RTSProperties {

	/**
	 * 
	 */
	public static final String SUBSCRIPTION_TYPE = "rts:subscriptionType";


	/**
	 * 
	 */
	public static final String TYPE = "xsi:type";
	
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


	private static final String PUSH_INTERVAL = "rts:pushInterval";
	
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
		public DataPort(String portName, String instanceName, String componentId) {
			super();
			put(INSTANCE_NAME, instanceName );
			put(PORT_NAME, portName);
			put(TYPE, "rtsExt:target_port_ext");
			put(COMPONENT_ID, componentId);
		}


		/**
		 * Constructor
		 * @param node XML node.
		 */
		public DataPort(Node node) throws IOException {
			this("defaultPortName", "defaultInstanceName", "defaultComponentId");
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
	public DataPortConnector(String connectorId, String name, 
			String dataType, String interfaceType, 
			String dataflowType, String subscriptionType) {
		super();
		pivotList = new PivotList();
		put(INTERFACE_TYPE, interfaceType);
		put(DATAFLOW_TYPE, dataflowType);
		put(SUBSCRIPTION_TYPE, subscriptionType);
		put(NAME, name);
		put(TYPE, "rtsExt:dataport_connector_ext");
		put(CONNECTOR_ID, connectorId);
		put(DATA_TYPE, dataType);
	}

	public DataPortConnector(String connectorId, String name, 
			String dataType, String interfaceType, 
			String dataflowType, String subscriptionType, String pushInterval) {
		super();
		pivotList = new PivotList();
		put(INTERFACE_TYPE, interfaceType);
		put(DATAFLOW_TYPE, dataflowType);
		put(SUBSCRIPTION_TYPE, subscriptionType);
		put(NAME, name);
		put(TYPE, "rtsExt:dataport_connector_ext");
		put(CONNECTOR_ID, connectorId);
		put(DATA_TYPE, dataType);
		put(PUSH_INTERVAL, pushInterval);
	}
	
	public DataPortConnector() throws IOException {
		this("defaultConnectorId", "defaultName",
				"defaultDataType", "defaultInterfaceType",
				"defaultDataflowType", "defaultSubscriptionType");
	}
	/**
	 * Constructor
	 * @param node XML node.
	 * @throws IOException
	 */
	public DataPortConnector(Node node) throws IOException {
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
		return element;
	}

	
	public String getSourceComponentInstanceName() {
		return sourceDataPort.get(DataPortConnector.DataPort.INSTANCE_NAME);
	}
	
	public String getSourceComponentPathUri() {
		return sourceDataPort.properties.get(Properties.VALUE);
	}
	
	public String getTargetComponentInstanceName() {
		return targetDataPort.get(DataPortConnector.DataPort.INSTANCE_NAME);
	}

	public String getTargetComponentPathUri() {
		return targetDataPort.properties.get(Properties.VALUE);
	}
	/**
	 * @return
	 */
	public String getSourceDataPortName() {
		return sourceDataPort.get(DataPortConnector.DataPort.PORT_NAME);
	}

	/**
	 * 
	 * @return
	 */
	public String getTargetDataPortName() {
		return targetDataPort.get(DataPortConnector.DataPort.PORT_NAME);
	}
	
	public void connect() throws Exception {
		RTSystemBuilder.connect(this);
	}
	
	public void disconnect() throws Exception {
		RTSystemBuilder.disconnect(this);
	}

	private PivotList pivotList;
	/**
	 * getPivotList
	 * <div lang="ja">
	 * 
	 * @return
	 * </div>
	 * <div lang="en">
	 *
	 * @return
	 * </div>
	 */
	public PivotList getPivotList() {
		// TODO �����������ꂽ���\�b�h�E�X�^�u
		return pivotList;
	}

	/**
	 * getSelectedPivot
	 * <div lang="ja">
	 * 
	 * @return
	 * </div>
	 * <div lang="en">
	 *
	 * @return
	 * </div>
	 */
	public Point getSelectedPivot() {
		// TODO �����������ꂽ���\�b�h�E�X�^�u
		return selectedPivot;
	}

	private Point selectedPivot;
	/**
	 * setSelectedPivot
	 * <div lang="ja">
	 * 
	 * @param selectedPivot
	 * </div>
	 * <div lang="en">
	 *
	 * @param selectedPivot
	 * </div>
	 */
	public void setSelectedPivot(Point selectedPivot) {
		this.selectedPivot = selectedPivot;
	}

}
