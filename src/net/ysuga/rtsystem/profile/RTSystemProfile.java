package net.ysuga.rtsystem.profile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

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

	private Map<String, Component> componentMap;

	private Map<String, Connector> connectorMap;

	/**
	 * 
	 * @return
	 */
	final public Map<String, Component> componentMap() {
		return componentMap;
	}

	/**
	 * 
	 * @return
	 */
	final public Map<String, Connector> connectorMap() {
		return connectorMap;
	}

	protected String formatCalendar(Calendar calendar) {
		MessageFormat mf = new MessageFormat(
				"{0,date,yyyy-MM-dd}{1,choice,0#M|1#E|2#W|3#T|4#F|5#A|6#S}{0,date,HH:mm:ss.SSSZ}");
		Object[] objs = { calendar.getTime(), calendar.get(Calendar.DAY_OF_WEEK)};
		return mf.format(objs);
	}

	public RTSystemProfile() {
		componentMap = new HashMap<String, Component>();
		connectorMap = new HashMap<String, Connector>();
		put("xmlns:rtsExt", "http://www.openrtp.org/namespaces/rts_ext");
		put("xmlns:rts", "http://www.openrtp.org/namespaces/rts");
		put("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
		Calendar current = Calendar.getInstance();
		put("rts:updateDate", formatCalendar(current));
		put("rts:creationDate", formatCalendar(current));
		put(VERSION, "0.2");
		put(ID, "defaultId");
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
		for (Component component : componentMap.values()) {
			element.appendChild(component
					.getElement("rts:Components", document));
		}
		for (Connector connector : connectorMap.values()) {
			element.appendChild(connector.getElement("rts:DataPortConnectors",
					document));
		}
		return element;
	}

	public RTSystemProfile(File file) throws ParserConfigurationException,
			SAXException, IOException {
		this();
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
				addConnector(new Connector(node));
			}
		}
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
			FileNotFoundException, TransformerException, ParserConfigurationException {
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
		componentMap.put(component.get("rts:instanceName"), component);
	}

	/**
	 * 
	 * @param connector
	 */
	public void addConnector(Connector connector) {
		connectorMap.put(connector.get("rts:name"), connector);
	}

	public void removeComponent(Component component) {
		componentMap.remove(component.get("rts:instanceName"));

	}

	public void removeConnector(Connector connector) {
		connectorMap.remove(connector.get("rts:name"));
	}

	/**
	 * 
	 * @param name
	 * @return
	 */
	final public Component getComponent(String name) {
		return componentMap.get(name);
	}

	/**
	 * 
	 * @param name
	 * @return
	 */
	final public Connector getConnector(String name) {
		return connectorMap.get(name);
	}

}
