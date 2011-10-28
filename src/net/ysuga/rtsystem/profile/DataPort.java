/**
 * DataPort.java
 *
 * @author Yuki Suga (ysuga.net)
 * @date 2011/10/08
 * @copyright 2011, ysuga.net allrights reserved.
 *
 */
package net.ysuga.rtsystem.profile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import RTC.PortInterfacePolarity;

/**
 * DataPort
 * @author ysuga
 *
 */
public class DataPort extends RTSObject {
	
	
	
	/**
	 * 
	 */
	public static final String RTS_NAME = "rts:name";
	/**
	 * 
	 */
	public static final String XSI_TYPE = "xsi:type";

	public static final int DIRECTION_IN = 0;
	public static final int DIRECTION_OUT = 1;
	public static final int DIRECTION_UNKNOWN = 2;
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public static final int SERVICE_PORT = 10;
	public static final int SERVICE_PROVIDER = 11;
	public static final int SERVICE_CONSUMER = 12;
	

	private ArrayList<Interface> interfaceList;
	
	public List<Interface> getInterfaceList() {
		return interfaceList;
	}
	
	private int direction = DIRECTION_UNKNOWN;
	
	private String dataType = null;
	
	public String getDataType() {
		return dataType;
	}

	/**
	 * setDataType
	 *
	 * @param dataType
	 */
	public void setDataType(String dataType) {
		this.dataType = dataType;
	}
	
	public void setDirection(int direction) {
		this.direction = direction;
	}
	
	public int getDirection() {
		return direction;
	}
	


	/**
	 * Constructor
	 */
	public DataPort(String name) {
		put(XSI_TYPE, "rtsExt:dataport_ext");
		put(RTS_NAME, name);
		//this.interfaceList = new ArrayList<Interface>();
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

	public class Interface {
		final private int polarity;
		public final static int POLARITY_PROVIDER = 0;
		public final static int POLARITY_CONSUMER = 1;
		
		final private String interfaceTypeName;
		final private String instanceName;
		
		public Interface(String type, String name, int polarity) {
			this.interfaceTypeName = type;
			this.instanceName = name;
			this.polarity = polarity;
		}
		
		final public String getName() {
			return interfaceTypeName;
		}
		
		final public String getInstanceName() {
			return instanceName;
		}
		
		final public int getPolarity() {
			return polarity;
		}
	};
	
	
	
	final public Interface getInterfaceByName(String name) {
		for(Interface iface : interfaceList) {
			if(iface.getName().equals(name)) {
				return iface;
			}
		}
		return null;
	}
	/**
	 * 
	 * addInterface
	 *
	 * @param type_name
	 * @param instance_name
	 * @param polarity
	 */
	public void addInterface(String type_name, String instance_name,
			PortInterfacePolarity polarity) {
		if(interfaceList == null) {
			interfaceList = new ArrayList<Interface>();
		}
		interfaceList.add(new Interface(type_name, instance_name, polarity.value()));
	}

	/**
	 * getPlainName
	 *
	 * @return
	 */
	public String getPlainName() {
		String name = get(RTS_NAME);
		String[] names = name.split("\\.", 0);
		if(names.length >= 2) 
			return names[1];
		return name;
	}

	/**
	 * downwardSynchronize
	 * @throws Exception
	 */
	@Override
	public void downwardSynchronize() throws Exception {
		// TODO 自動生成されたメソッド・スタブ
		
	}

}