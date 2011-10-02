package net.ysuga.rtsystem.profile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.TimeZone;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class RTSystemProfile extends RTSProperties {

	/**
	 * 
	 */
	public static final String VERSION = "rts:version";

	/**
	 * 
	 */
	public static final String ID = "rts:id";

	/**
	 * 
	 */
	private static final long serialVersionUID = 5515514928310883134L;

	private String fileName;

	final public String getFileName() {
		return fileName;
	}

	public Set<Component> componentSet;

	public Set<DataPortConnector> dataPortConnectorSet;

	public Set<ServicePortConnector> servicePortConnectorSet;

	protected String formatCalendar(Calendar calendar) {
		MessageFormat mf = new MessageFormat(
				"{0,date,yyyy-MM-dd}T{0,date,HH:mm:ss.SSSZ}");
		Object[] objs = { calendar.getTime() };
		StringBuffer buf = new StringBuffer(mf.format(objs));
		buf.insert(buf.length() - 2, ":");
		return buf.toString();
	}

	public RTSystemProfile(String systemName, String vendorName, String version) {
		put("xmlns:rtsExt", "http://www.openrtp.org/namespaces/rts_ext");
		put("xmlns:rts", "http://www.openrtp.org/namespaces/rts");
		put("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
		Calendar current = Calendar.getInstance();
		put("rts:updateDate", formatCalendar(current));
		put("rts:creationDate", formatCalendar(current));
		put(VERSION, "0.2");
		put(ID, "RTSystem:" + vendorName + ":" + systemName + ":" + version);
		componentSet = new HashSet<Component>();
		dataPortConnectorSet = new HashSet<DataPortConnector>();
		servicePortConnectorSet = new HashSet<ServicePortConnector>();
	}

	@Override
	/*
	 * * XML data Save helper function
	 * 
	 * @param elementName XML format element name
	 * 
	 * @param document XML document object
	 */
	public Element getElement(String elementName, Document document) {
		Element element = createElement(elementName, document);
		for (RTSProperties component : componentSet) {
			element.appendChild(component
					.getElement("rts:Components", document));
		}
		for (PortConnector connector : dataPortConnectorSet) {
			element.appendChild(connector.getElement("rts:DataPortConnectors",
					document));
		}
		return element;
	}

	public RTSystemProfile(File file) throws ParserConfigurationException,
			SAXException, IOException {
		this("defaultName", "defaultVendorName", "defaultVersion");
		load(file);
	}

	/**
	 * 
	 * @param file
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 */
	public void load(File file) throws ParserConfigurationException,
			SAXException, IOException {
		fileName = file.getName();
		Element rootElement;
		Document xmlDocument;
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = null;
		db = dbf.newDocumentBuilder();
		xmlDocument = db.parse(file);
		rootElement = xmlDocument.getDocumentElement();

		load(rootElement);

		NodeList nodeList = rootElement.getChildNodes();
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node node = nodeList.item(i);
			if (node.getNodeName().equals("rts:Components")) {
				addComponent(new Component(node));
			} else if (node.getNodeName().equals("rts:DataPortConnectors")) {
				addDataPortConnector(new DataPortConnector(node));
			} else if (node.getNodeName().equals("rts:ServicePortConnectors")) {
				addServicePortConnector(new ServicePortConnector(node));
			}
		}

		/**
		 * for (Component component : componentSet) { for (Component.DataPort
		 * dataPort : component.dataPortSet) { for (Connector connector :
		 * connectorSet) { if (dataPort.get(Component.DataPort.RTS_NAME).equals(
		 * connector.sourceDataPort .get(Connector.DataPort.PORT_NAME)) &&
		 * component.get(Component.PATH_URI).equals(
		 * connector.sourceDataPort.properties .get(Properties.VALUE))) {
		 * System.out.println(component.get(Component.PATH_URI) +
		 * dataPort.get(Component.DataPort.RTS_NAME) + " is OutPort");
		 * dataPort.setDirection(Component.DataPort.DIRECTION_OUT); } else if
		 * (dataPort.get(Component.DataPort.RTS_NAME)
		 * .equals(connector.targetDataPort .get(Connector.DataPort.PORT_NAME))
		 * && component.get(Component.PATH_URI).equals(
		 * connector.targetDataPort.properties .get(Properties.VALUE))) {
		 * System.out.println(component.get(Component.PATH_URI) +
		 * dataPort.get(Component.DataPort.RTS_NAME) + " is InPort");
		 * 
		 * dataPort.setDirection(Component.DataPort.DIRECTION_IN); }
		 * 
		 * }
		 * 
		 * } }
		 */

	}

	/**
	 * Save to File
	 * 
	 * @param file
	 * @throws TransformerFactoryConfigurationError
	 * @throws TransformerException
	 * @throws FileNotFoundException
	 * @throws ParserConfigurationException
	 */
	public void save(File file) throws TransformerFactoryConfigurationError,
			FileNotFoundException, TransformerException,
			ParserConfigurationException {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setValidating(true);
		DocumentBuilder db = dbf.newDocumentBuilder();
		Document document = db.newDocument();
		Element rootElement = getElement("rts:RtsProfile", document);
		document.appendChild(rootElement);

		Transformer t;
		t = TransformerFactory.newInstance().newTransformer();

		t.setOutputProperty("indent", "yes");
		t.transform(new javax.xml.transform.dom.DOMSource(document),
				new javax.xml.transform.stream.StreamResult(
						new java.io.FileOutputStream(file)));
	}

	/**
	 * 
	 * @param component
	 */
	public void addComponent(Component component) {
		componentSet.add(component);
	}

	/**
	 * 
	 * @param connector
	 */
	public void addDataPortConnector(DataPortConnector connector) {
		dataPortConnectorSet.add(connector);
	}

	public void removeComponent(RTSProperties component) {
		componentSet.remove(component.get("rts:instanceName"));

	}

	public void removeDataPortConnector(PortConnector connector) {
		dataPortConnectorSet.remove(connector.get("rts:name"));
	}

	public void removeServicePortConnector(PortConnector connector) {
		servicePortConnectorSet.remove(connector.get("rts:name"));
	}

	/**
	 * 
	 * @param name
	 * @return
	 */
	final public RTSProperties getComponent(String name) {
		for (RTSProperties component : componentSet) {
			if (component.get(Component.INSTANCE_NAME).equals(name)) {
				return component;
			}
		}
		return null;
	}

	/**
	 * 
	 * @param name
	 * @return
	 */
	final public PortConnector getDataConnector(String name) {
		for (PortConnector connector : dataPortConnectorSet) {
			if (connector.get(PortConnector.CONNECTOR_ID).equals(name)) {
				return connector;
			}
		}
		return null;
	}

	/**
	 * addServicePortConnector
	 * 
	 * @param connector
	 */
	public void addServicePortConnector(ServicePortConnector connector) {
		this.servicePortConnectorSet.add(connector);
	}

}
