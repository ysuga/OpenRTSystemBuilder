/**
 * PortConnector.java
 *
 * @author Yuki Suga (ysuga.net)
 * @date 2011/10/02
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
public abstract  class PortConnector extends RTSProperties {

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
	public static final String INTERFACE_TYPE = "rts:interfaceType";
	public static final String DATA_TYPE = "rts:dataType";
	public static final String CONNECTOR_ID = "rts:connectorId";
	public static final String NAME = "rts:name";
	protected static final String PUSH_INTERVAL = "rts:pushInterval";
	protected PivotList pivotList;
	private Point selectedPivot;
	/**
	 * Source Data Port
	 */
	public Port sourcePort;
	/**
	 * Target Data Port
	 */
	public Port targetPort;

	/**
	 * Constructor
	 */
	public PortConnector() {
		// TODO 自動生成されたコンストラクター・スタブ
	}

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

	/**
	 * getElement
	 * @param elementName
	 * @param document
	 * @return
	 */
	@Override
	abstract public Element getElement(String elementName, Document document);

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

	public String getSourceComponentInstanceName() {
		return sourcePort.get(ServicePortConnector.Port.INSTANCE_NAME);
	}

	public String getSourceComponentPathUri() {
		return sourcePort.properties.get(Properties.VALUE);
	}

	public String getTargetComponentInstanceName() {
		return targetPort.get(ServicePortConnector.Port.INSTANCE_NAME);
	}

	public String getTargetComponentPathUri() {
		return targetPort.properties.get(Properties.VALUE);
	}

	/**
	 * @return
	 */
	public String getSourcePortName() {
		return sourcePort.get(ServicePortConnector.Port.PORT_NAME);
	}

	/**
	 * 
	 * @return
	 */
	public String getTargetPortName() {
		return targetPort.get(ServicePortConnector.Port.PORT_NAME);
	}

	/**
	 * DataPort
	 * @author ysuga
	 *
	 */
	public class Port extends RTSProperties{

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
		public Port(String portName, String instanceName, String componentId) {
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
		public Port(Node node) throws IOException {
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
	
}
