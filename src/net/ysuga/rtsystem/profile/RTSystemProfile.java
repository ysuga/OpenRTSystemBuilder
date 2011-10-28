package net.ysuga.rtsystem.profile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;

import net.ysuga.corbanaming.RTNamingContext;
import net.ysuga.rtsbuilder.RTSystemBuilder;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
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

	public Set<RTComponent> componentSet;

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

	private String name;

	public String getName() {
		return name;
	}

	public RTSystemProfile(String systemName, String vendorName, String version) {
		name = systemName;
		put("xmlns:rtsExt", "http://www.openrtp.org/namespaces/rts_ext");
		put("xmlns:rts", "http://www.openrtp.org/namespaces/rts");
		put("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
		Calendar current = Calendar.getInstance();
		put("rts:updateDate", formatCalendar(current));
		put("rts:creationDate", formatCalendar(current));
		put(VERSION, "0.2");
		put(ID, "RTSystem:" + vendorName + ":" + systemName + ":" + version);
		componentSet = new HashSet<RTComponent>();
		dataPortConnectorSet = new HashSet<DataPortConnector>();
		servicePortConnectorSet = new HashSet<ServicePortConnector>();

		executor = Executors.newFixedThreadPool(16);
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
				NamedNodeMap attrs = node.getAttributes();
				if (attrs.getNamedItem("PAIO") != null) {
					addComponent(new PyIOComponent(node));
				} else {
					addComponent(new RTComponent(node));
				}
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
	public void addComponent(RTComponent component) {
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
		componentSet.remove(component);
		for(PortConnector pc:getPortConnectors()) {
			if(pc.getSourceComponentPathUri().equals(component.get(RTComponent.PATH_URI))) {
				removePortConnector(pc);
			}
			if(pc.getTargetComponentPathUri().equals(component.get(RTComponent.PATH_URI))) {
				removePortConnector(pc);
			}
		}
	}

	public void removeDataPortConnector(PortConnector connector) {
		dataPortConnectorSet.remove(connector);
	}

	public void removeServicePortConnector(PortConnector connector) {
		servicePortConnectorSet.remove(connector);
	}

	public void removePortConnector(PortConnector connector) {
		dataPortConnectorSet.remove(connector);
		servicePortConnectorSet.remove(connector);
	}
	/**
	 * 
	 * @param name
	 * @return
	 */
	final public RTSProperties getComponentByInstanceName(String name) {
		for (RTSProperties component : componentSet) {
			if (component.get(RTComponent.INSTANCE_NAME).equals(name)) {
				return component;
			}
		}
		return null;
	}

	final public RTSProperties getComponentByFullPath(String pathUri) {
		for (RTSProperties component : componentSet) {
			if (component.get(RTComponent.PATH_URI).equals(pathUri)) {
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

	/**
	 * getOwnedConnectorSet
	 * 
	 * @param selectedRTSObject
	 * @return
	 */
	public Set<PortConnector> getOwnedConnectorSet(RTSObject selectedRTSObject) {
		Set<PortConnector> conSet = new HashSet<PortConnector>();
		String pathUri = selectedRTSObject.get(RTComponent.PATH_URI);

		for (DataPortConnector con : dataPortConnectorSet) {
			if (con.getSourceComponentPathUri().equals(pathUri)) {
				conSet.add(con);
			}

			if (con.getTargetComponentPathUri().equals(pathUri)) {
				conSet.add(con);
			}
		}
		for (ServicePortConnector con : servicePortConnectorSet) {
			if (con.getSourceComponentPathUri().equals(pathUri)) {
				conSet.add(con);
			}

			if (con.getTargetComponentPathUri().equals(pathUri)) {
				conSet.add(con);
			}
		}
		return conSet;
	}

	/**
	 * getOwner
	 * 
	 * @param source
	 * @return
	 */
	public RTComponent getOwner(DataPort source) {
		for (RTSObject component : this.componentSet) {
			if (component instanceof RTComponent) {
				for (DataPort port : ((RTComponent) component).dataPortSet) {
					if (port == source) {
						return ((RTComponent) component);
					}
				}
			}
		}
		return null;
	}


	class AsynchAddThread implements Callable<Integer> {
		private String fullpath;

		public AsynchAddThread(String fullpath) {
			this.fullpath = fullpath;
		}

		public Integer call() {
			RTComponent comp;
			try {
				comp = RTSystemBuilder.createComponent(fullpath);
				if (comp != null) {
					addComponent(comp);
				}
				return new Integer(0);
			} catch (Exception e) {
				// TODO 自動生成された catch ブロック
				return new Integer(-1);
			}
		}
	};

	private ExecutorService executor;
	private ArrayList<Future<Integer>> futureList;

	public void addComponentAsynch(String fullpath) {
		futureList.add(executor.submit(new AsynchAddThread(fullpath)));
	}

	public void asynchAddJoin() {
		for (Future<Integer> t : futureList) {
			try {
				t.get();
			} catch (ExecutionException e) {
				// TODO 自動生成された catch ブロック
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO 自動生成された catch ブロック
				e.printStackTrace();
			}
		}
	}

	/**
	 * Add All Components included in NamingContext.
	 * 
	 * @param nc
	 *            NamingContext of a Name Service.
	 * @throws Exception
	 */
	public synchronized void addAllComponent(RTNamingContext nc) throws Exception {
		Exception ebuf = null;
		boolean startFlag = false;
		if (futureList == null) {
			futureList = new ArrayList<Future<Integer>>();
			startFlag = true;
		}
		for (RTNamingContext rtnc : nc) {
			if (rtnc.getKind().equals("rtc")) {
				String fullpath = rtnc.getFullPath();
				if (this.getComponentByFullPath(fullpath) == null) {
					try {
						this.addComponentAsynch(fullpath);
					} catch (Exception e) {
						// TODO 自動生成された catch ブロック
						e.printStackTrace();
						ebuf = e;
					}
				}
			} else {
				try {

					addAllComponent(rtnc);
				} catch (Exception e) {
					ebuf = e;
				}
			}
		}
		if (startFlag) {
			asynchAddJoin();
			futureList = null;
		}
		if (ebuf != null) {
			throw ebuf;
		}
	}

	/**
	 * getConnectorByName
	 *
	 * @param name
	 * @return
	 */
	public PortConnector getConnectorByName(String name) {
		for(DataPortConnector dpc:this.dataPortConnectorSet) {
			if(dpc.get(PortConnector.NAME).equals(name)) {
				return dpc;
			}
		}
		for(ServicePortConnector spc:this.servicePortConnectorSet) {
			if(spc.get(PortConnector.NAME).equals(name)) {
				return spc;
			}
		}
		return null;
	}

	public void addPortConnector(PortConnector pc) {
		if (pc instanceof ServicePortConnector) {
			addServicePortConnector((ServicePortConnector) pc);
		} else {
			addDataPortConnector((DataPortConnector) pc);
		}
	}

	/**
	 * getPortConnectorSet
	 *
	 * @return
	 */
	public PortConnector[] getPortConnectors(){
		PortConnector[] pcs = new PortConnector[dataPortConnectorSet.size() + servicePortConnectorSet.size()];
		int counter = 0;
		Iterator<DataPortConnector> i = dataPortConnectorSet.iterator();
		while(i.hasNext()) {
			pcs[counter] = (PortConnector)i.next();
			counter++;
		}
		Iterator<ServicePortConnector> si = servicePortConnectorSet.iterator();
		while(si.hasNext()) {
			pcs[counter] = (PortConnector)si.next();
			counter++;
		}
		return pcs;
	}
	
}
