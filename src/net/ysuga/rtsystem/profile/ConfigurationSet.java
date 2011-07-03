/**
 * ConfigurationSet.java
 * 
 * @author Yuki Suga (ysuga@ysuga.net)
 * @copyright 2011, ysuga.net allrights reserved.
 */
package net.ysuga.rtsystem.profile;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * ConfigurationSet
 * @author ysuga
 *
 */
public class ConfigurationSet extends RTSProperties {
 
	/**
	 * 
	 */
	public static final String ID = "rts:id";

	/**
	 * 
	 */
	private static final long serialVersionUID = 7737167550430948592L;
	
	/**
	 * 
	 */
	public Set<ConfigurationData> configurationDataSet;
	
	/**
	 * Constructor
	 */
	public ConfigurationSet(String id) {
		put(ID, id);
		configurationDataSet = new HashSet<ConfigurationData>();
	}

	/**
	 * Constructor
	 * @param node XML document node
	 * @throws IOException
	 */
	public ConfigurationSet(Node node) throws IOException {
		this("defaultId");
		load(node);
		
		NodeList nodeList = node.getChildNodes();
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node cnode = nodeList.item(i);
			if (cnode.getNodeName().equals("rts:ConfigurationData")) {
				 configurationDataSet.add(new ConfigurationData(cnode));
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
		for(ConfigurationData configurationData : configurationDataSet) {
			element.appendChild(configurationData.getElement("rts:ConfigurationData", document));
		}
		return element;
	}
	 
}
 
