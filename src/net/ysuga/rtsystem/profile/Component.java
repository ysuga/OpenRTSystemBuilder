/**
 * Component.java
 * 
 * @author Yuki Suga (ysuga@ysuga.net)
 * @copyright 2011, ysuga.net allrights reserved.
 */

package net.ysuga.rtsystem.profile;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import net.ysuga.rtsbuilder.RTSystemBuilder;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Component
 * @author ysuga
 *
 */
public class Component extends RTSProperties {

	/**
	 * 
	 */
	public static final String ID = "rts:id";

	public static final String TYPE = "xsi:type";
	/**
	 * 
	 */
	public static final String PATH_URI = "rts:pathUri";

	/**
	 * 
	 */
	public static final String ACTIVE_CONFIGURATION_SET = "rts:activeConfigurationSet";

	/**
	 * 
	 */
	public static final String COMPOSITE_TYPE = "rts:compositeType";

	/**
	 * 
	 */
	private static final long serialVersionUID = 8071577410052606100L;

	public static final String INSTANCE_NAME = "rts:instanceName";

	public static final String IS_REQUIRED = "rts:isRequired";

	/**
	 * Set of DataPort 
	 */
	public Set<DataPort> dataPortSet;
	
	/**
	 * Set of ConfigurationSet 
	 */
	public Set<ConfigurationSet> configurationSetSet;
	
	/**
	 * Set of ExecutionContext
	 */
	public Set<ExecutionContext> executionContextSet;

	/**
	 * Location
	 */
	public Location location;
	
	/**
	 * Properties
	 */
	public Properties properties;
	
	/**
	 * DataPort
	 * @author ysuga
	 *
	 */
	public class DataPort extends RTSProperties {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		/**
		 * Constructor
		 */
		public DataPort(String name) {
			put("xsi:type", "rtsExt:dataport_ext");
			put("rts:name", name);
		}
		
		/**
		 * Constructor
		 * @param node XML node
		 * @throws IOException
		 */
		public DataPort(Node node) throws IOException {
			this("defaultName");
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

	public Component() throws IOException {
		this("defaultInstanceName", "defaultPathUri",
				"defaultId", "default", 
				true, "None");
	}
	/**
	 * Constructor
	 * @throws IOException
	 */
	public Component(String instanceName, String pathUri, 
			String Id, String activeConfigurationSet, 
			boolean required, String compositeType) throws IOException {
		super();
		put(TYPE, "rtsExt:component_ext");
		put(IS_REQUIRED, Boolean.toString(required));
		put(COMPOSITE_TYPE, compositeType);
		put(ACTIVE_CONFIGURATION_SET, activeConfigurationSet);
		put(INSTANCE_NAME, instanceName);
		put(PATH_URI, pathUri);
		put(ID, Id);

		dataPortSet = new HashSet<DataPort>();
		configurationSetSet = new HashSet<ConfigurationSet>();
		executionContextSet = new HashSet<ExecutionContext>();	
	}

	
	/**
	 * Constructor
	 * @param node
	 * @throws IOException
	 */
	public Component(Node node) throws IOException {
		this("defaultInstanceName", "defaultPathUri",
				"defaultId", "default", 
				true, "None");
		load(node);

		NodeList nodeList = node.getChildNodes();
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node cnode = nodeList.item(i);
			if (cnode.getNodeName().equals("rts:DataPorts")) {
				dataPortSet.add(new DataPort(cnode));
			} else if (cnode.getNodeName().equals("rts:ConfigurationSets")) {
				configurationSetSet.add(new ConfigurationSet(cnode));
			} else if (cnode.getNodeName().equals("rts:ExecutionContexts")) {
				executionContextSet.add(new ExecutionContext(cnode));
			} else if (cnode.getNodeName().equals("rtsExt:Location")) {
				location = new Location(cnode);
			} else if (cnode.getNodeName().equals("rtsExt:Properties")) {
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
		
		for(DataPort dataPort : dataPortSet) {
			element.appendChild(dataPort.getElement("rts:DataPorts", document));
		}
		for(ConfigurationSet configurationSet : configurationSetSet) {
			element.appendChild(configurationSet.getElement("rts:ConfigurationSets", document));
		}
		for(ExecutionContext executionContext : executionContextSet) {
			element.appendChild(executionContext.getElement("rts:ExecutionContexts", document));
		}
		
		element.appendChild(location.getElement("rtsExt:Location", document));
		element.appendChild(properties.getElement("rtsExt:Properties", document));
		
		return element;
	}

	public void activate() throws Exception {
		RTSystemBuilder.activateComponent(this);
	}
	
	public void deactivate() throws Exception {
		RTSystemBuilder.activateComponent(this);
	}
	
	public void reset() throws Exception {
		RTSystemBuilder.activateComponent(this);
	}
	
	public void configure() throws Exception {
		RTSystemBuilder.configureComponent(this);
	}
	
}
