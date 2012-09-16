package net.ysuga.rtsbuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.logging.Logger;

import jp.go.aist.rtm.RTC.CorbaNaming;
import jp.go.aist.rtm.RTC.port.CorbaConsumer;
import jp.go.aist.rtm.RTC.util.CORBA_SeqUtil;
import jp.go.aist.rtm.RTC.util.NVUtil;
import jp.go.aist.rtm.RTC.util.ORBUtil;
import net.ysuga.corbanaming.CorbaNamingCannotFindException;
import net.ysuga.corbanaming.CorbaNamingManager;
import net.ysuga.corbanaming.CorbaNamingResolveException;
import net.ysuga.rtsystem.profile.ConfigurationData;
import net.ysuga.rtsystem.profile.ConfigurationSet;
import net.ysuga.rtsystem.profile.DataPort;
import net.ysuga.rtsystem.profile.DataPort.Interface;
import net.ysuga.rtsystem.profile.DataPortConnector;
import net.ysuga.rtsystem.profile.ExecutionContext;
import net.ysuga.rtsystem.profile.Location;
import net.ysuga.rtsystem.profile.PortConnector;
import net.ysuga.rtsystem.profile.Properties;
import net.ysuga.rtsystem.profile.PyIOComponent;
import net.ysuga.rtsystem.profile.RTComponent;
import net.ysuga.rtsystem.profile.RTSObject;
import net.ysuga.rtsystem.profile.RTSProperties;
import net.ysuga.rtsystem.profile.RTSystemProfile;
import net.ysuga.rtsystem.profile.ServicePortConnector;

import org.omg.CORBA.TCKind;

import OpenRTM.DataFlowComponent;
import RTC.ComponentProfile;
import RTC.ConnectorProfile;
import RTC.ConnectorProfileHolder;
import RTC.ExecutionContextListHolder;
import RTC.ExecutionKind;
import RTC.LifeCycleState;
import RTC.PortInterfaceProfile;
import RTC.PortService;
import RTC.RTObject;
import _SDOPackage.InterfaceNotImplemented;
import _SDOPackage.InternalError;
import _SDOPackage.NVListHolder;
import _SDOPackage.NameValue;
import _SDOPackage.NotAvailable;

/**
 * @author ysuga
 * 
 */
public class RTSystemBuilder {
	static private Logger logger;

	// static private Map<String, CorbaNaming> corbaNamingMap;

	static {
		logger = Logger.getLogger("net.ysuga.rtsbuilder");
		// corbaNamingMap = new HashMap<String, CorbaNaming>();
	}

	/**
	 * Constructor
	 */
	public RTSystemBuilder() {

	}

	/**
	 * Search RTCs in RT System Profile. This function just searches RTCs and
	 * return true if success.
	 * 
	 * @param rtSystemProfile
	 * @return true if all RTCs are found
	 */
	static public boolean searchRTCs(RTSystemProfile rtSystemProfile) {
		boolean ret = false;
		for (RTComponent component : rtSystemProfile.componentSet) {
			try {
				getComponent(component);
			} catch (Exception e) {
				ret = true;
			}
		}
		return ret;
	}

	/**
	 * searchConnections <div lang="ja">
	 * 
	 * @param rtSystemProfile
	 *            </div> <div lang="en">
	 * 
	 * @param rtSystemProfile
	 *            </div>
	 * 
	 *            public static void searchConnections(RTSystemProfile
	 *            rtSystemProfile) { for (DataPortConnector connector :
	 *            rtSystemProfile.dataPortConnectorSet) { try {
	 *            findConnector(connector); } catch (Exception e) { } } }
	 * 
	 */

	/**
	 * Configure All RTCs in RTS profile.
	 * 
	 * @param rtSystemProfile
	 */
	static public void configure(RTSystemProfile rtSystemProfile) {
		for (RTComponent component : rtSystemProfile.componentSet) {
			try {
				configureComponent(component);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 
	 * buildConnection
	 * 
	 * @param rtSystemProfile
	 */
	static public void buildConnection(RTSystemProfile rtSystemProfile) {
		logger.info("buildConnection:"
				+ rtSystemProfile.get(RTSystemProfile.ID));
		for (DataPortConnector connector : rtSystemProfile.dataPortConnectorSet) {
			try {
				connect(connector);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		for (PortConnector connector : rtSystemProfile.servicePortConnectorSet) {
			try {
				connect(connector);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 
	 * configureComponent
	 * 
	 * @param component
	 * @throws Exception
	 */
	static public void configureComponent(RTComponent component)
			throws Exception {
		logger.info("configureComponent:"
				+ component.get(RTComponent.INSTANCE_NAME));
		RTObject rtObject = getComponent(component);

		_SDOPackage.Configuration sdoConfiguration = rtObject
				.get_configuration();
		for (ConfigurationSet configurationSet : component.configurationSetSet) {
			_SDOPackage.ConfigurationSet sdoConfigurationSet = sdoConfiguration
					.get_configuration_set(configurationSet
							.get(ConfigurationSet.ID));
			NVListHolder nvListHolder = new NVListHolder();
			nvListHolder.value = new NameValue[0];
			for (ConfigurationData configurationData : configurationSet.configurationDataSet) {
				CORBA_SeqUtil.push_back(nvListHolder, NVUtil.newNVString(
						configurationData.get(ConfigurationData.NAME),
						configurationData.get(ConfigurationData.DATA)));
			}
			sdoConfigurationSet.configuration_data = nvListHolder.value;
			sdoConfiguration.add_configuration_set(sdoConfigurationSet);
			if (component.get(RTComponent.ACTIVE_CONFIGURATION_SET).equals(
					sdoConfigurationSet.id)) {
				sdoConfiguration
						.activate_configuration_set(sdoConfigurationSet.id);
			}
		}
	}

	/**
	 * Destroy RT System. Disconnect connections just described in the RT System
	 * Profile (not all connections are disconnected) destroyRTSystem
	 * 
	 * @param rtSystemProfile
	 */
	static public void destroyRTSystem(RTSystemProfile rtSystemProfile) {
		logger.entering(RTSystemBuilder.class.getName(), "destroyRTSystem",
				rtSystemProfile);
		for (DataPortConnector connector : rtSystemProfile.dataPortConnectorSet) {
			try {
				disconnect(connector);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 
	 * clearAllConnection
	 * 
	 * @param rtSystemProfile
	 */
	static public void clearAllConnection(RTSystemProfile rtSystemProfile) {
		for (RTComponent component : rtSystemProfile.componentSet) {
			try {
				clearAllConnection(component);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 
	 * clearAllConnection
	 * 
	 * @param component
	 * @throws CorbaNamingResolveException
	 * @throws CorbaNamingCannotFindException
	 * @throws Exception
	 */
	public static void clearAllConnection(RTComponent component)
			throws CorbaNamingCannotFindException, CorbaNamingResolveException {
		logger.entering(RTSystemBuilder.class.getName(), "clearAllConnection",
				component);
		RTObject rtObject = getComponent(component);
		for (PortService portService : rtObject.get_ports()) {
			portService.disconnect_all();
		}
	}

	/**
	 * 
	 * disconnect
	 * 
	 * @param connector
	 * @throws CorbaNamingResolveException
	 * @throws CorbaNamingCannotFindException
	 * @throws Exception
	 */
	static public void disconnect(PortConnector connector)
			throws CorbaNamingCannotFindException, CorbaNamingResolveException {
		logger.entering(RTSystemBuilder.class.getName(), "disconnect",
				connector);
		RTObject sourceRTObject = getComponent(connector
				.getSourceComponentPathUri());
		for (PortService portService : sourceRTObject.get_ports()) {
			if (portService.get_port_profile().name.equals(connector
					.getSourcePortName())) {
				portService.disconnect(connector
						.get(PortConnector.CONNECTOR_ID));
			}
		}
	}

	/**
	 * 
	 * getPortService
	 * 
	 * @param component
	 * @param portName
	 * @return
	 * @throws CorbaNamingCannotFindException
	 * @throws CorbaNamingResolveException
	 */
	static public PortService getPortService(RTComponent component,
			String portName) throws CorbaNamingCannotFindException,
			CorbaNamingResolveException {
		RTObject sourceRTObject = getComponent(component);
		for (PortService portService : sourceRTObject.get_ports()) {
			if (portService.get_port_profile().name.equals(portName)) {
				return portService;
			}
		}
		return null;
	}

	/**
	 * 
	 * connect
	 * 
	 * @param connector
	 * @throws Exception
	 */
	static public void connect(PortConnector connector) throws Exception {
		logger.entering(RTSystemBuilder.class.getName(), "connect", connector);

		RTObject sourceRTObject = getComponent(connector
				.getSourceComponentPathUri());
		RTObject targetRTObject = getComponent(connector
				.getTargetComponentPathUri());

		// Building Connector Profile
		ConnectorProfile prof = new ConnectorProfile();
		prof.connector_id = connector.get(PortConnector.CONNECTOR_ID);
		prof.name = connector.get(PortConnector.NAME);
		prof.ports = new PortService[2];

		for (PortService portService : sourceRTObject.get_ports()) {
			String n = portService.get_port_profile().name;
			System.out.println(n);
			if (portService.get_port_profile().name.equals(connector
					.getSourcePortName())) {
				prof.ports[1] = portService;
			}
		}
		for (PortService portService : targetRTObject.get_ports()) {
			if (portService.get_port_profile().name.equals(connector
					.getTargetPortName())) {
				prof.ports[0] = portService;
			}
		}
		if (prof.ports[0] == null || prof.ports[1] == null) {
			throw new InvalidProfileException();
		}

		// Avoid double connection.
		for (ConnectorProfile connectorProfile : prof.ports[0]
				.get_connector_profiles()) {
			if (connectorProfile.name.equals(prof.name)) {
				return; // Do nothing
			}
		}

		NVListHolder nvholder = new NVListHolder();
		nvholder.value = prof.properties;
		if (nvholder.value == null)
			nvholder.value = new NameValue[0];
		String strbuf = null;
		if ((strbuf = connector.get(PortConnector.INTERFACE_TYPE)) != null) {
			CORBA_SeqUtil.push_back(nvholder, NVUtil.newNVString(
					"dataport.interface_type", strbuf));
		}
		if ((strbuf = connector.get(PortConnector.DATAFLOW_TYPE)) != null) {
			CORBA_SeqUtil.push_back(nvholder, NVUtil.newNVString(
					"dataport.dataflow_type", strbuf));
		}
		if ((strbuf = connector.get(PortConnector.SUBSCRIPTION_TYPE)) != null) {
			CORBA_SeqUtil.push_back(nvholder, NVUtil.newNVString(
					"dataport.subscription_type", strbuf));
		}
		prof.properties = nvholder.value;

		ConnectorProfileHolder proflist = new ConnectorProfileHolder();
		proflist.value = prof;

		if (prof.ports[0].connect(proflist) != RTC.ReturnCode_t.RTC_OK) {
			throw new Exception("Cannot Connect");
		}
	}

	/*
	 * static public void connect(PortConnector connector) throws Exception {
	 * logger.entering(RTSystemBuilder.class.getName(), "connect", connector);
	 * 
	 * if (connector instanceof DataPortConnector) { connect((DataPortConnector)
	 * connector); return; }
	 * 
	 * RTObject sourceRTObject = getComponent(connector
	 * .getSourceComponentPathUri()); RTObject targetRTObject =
	 * getComponent(connector .getTargetComponentPathUri());
	 * 
	 * // Building Connector Profile ConnectorProfile prof = new
	 * ConnectorProfile(); prof.connector_id =
	 * connector.get(PortConnector.CONNECTOR_ID); prof.name =
	 * connector.get(PortConnector.NAME); prof.ports = new PortService[2];
	 * 
	 * for (PortService portService : sourceRTObject.get_ports()) { if
	 * (portService.get_port_profile().name.equals(connector
	 * .getSourcePortName())) { prof.ports[1] = portService; } }
	 * 
	 * for (PortService portService : targetRTObject.get_ports()) { if
	 * (portService.get_port_profile().name.equals(connector
	 * .getTargetPortName())) { prof.ports[0] = portService; } } if
	 * (prof.ports[0] == null || prof.ports[1] == null) { throw new
	 * Exception("Invalid RTS Profile"); }
	 * 
	 * ConnectorProfileHolder proflist = new ConnectorProfileHolder();
	 * proflist.value = prof;
	 * 
	 * if (prof.ports[0].connect(proflist) != RTC.ReturnCode_t.RTC_OK) { throw
	 * new Exception("Cannot Connect"); } }
	 */
	/**
	 * 
	 * <div lang="ja"> �v���t�@�C���ɓo�^����Ă��邷�ׂĂ�RTC��activate
	 * 
	 * @param rtSystemProfile
	 * @throws Exception
	 *             </div> <div lang="en">
	 * 
	 * @param rtSystemProfile
	 * @throws Exception
	 *             </div>
	 */
	static public void activateRTCs(RTSystemProfile rtSystemProfile)
			throws Exception {
		logger.info("activateRTSystem:"
				+ rtSystemProfile.get(RTSystemProfile.ID));
		for (RTComponent component : rtSystemProfile.componentSet) {
			activateComponent(component);
		}
	}

	/**
	 * 
	 * <div lang="ja"> �v���t�@�C���ɓo�^����Ă��邷�ׂĂ�RTC��deactivate
	 * 
	 * @param rtSystemProfile
	 * @throws Exception
	 *             </div> <div lang="en">
	 * 
	 * @param rtSystemProfile
	 * @throws Exception
	 *             </div>
	 */
	static public void deactivateRTCs(RTSystemProfile rtSystemProfile)
			throws Exception {
		logger.info("deactivateRTSystem:"
				+ rtSystemProfile.get(RTSystemProfile.ID));
		for (RTComponent component : rtSystemProfile.componentSet) {
			deactivateComponent(component);
		}
	}

	/**
	 * 
	 * <div lang="ja"> �v���t�@�C���ɓo�^����Ă��邷�ׂĂ�RTC��reset
	 * 
	 * @param rtSystemProfile
	 * @throws Exception
	 *             </div> <div lang="en">
	 * 
	 * @param rtSystemProfile
	 * @throws Exception
	 *             </div>
	 */
	static public void resetRTCs(RTSystemProfile rtSystemProfile)
			throws Exception {
		logger.info("resetRTSystem:" + rtSystemProfile.get(RTSystemProfile.ID));
		for (RTComponent component : rtSystemProfile.componentSet) {
			resetComponent(component);
		}
	}

	/**
	 * 
	 * buildComponentId
	 * 
	 * @param rtObject
	 * @return
	 */
	static public String buildComponentId(RTC.RTObject rtObject) {
		ComponentProfile profile;
		profile = rtObject.get_component_profile();
		return "RTC:" + profile.vendor + ":" + profile.category + ":"
				+ profile.type_name + ":" + profile.version;
	}

	/**
	 * 
	 * createComponent
	 * 
	 * @param pathUri
	 * @return
	 * @throws CorbaNamingCannotFindException
	 * @throws CorbaNamingResolveException
	 * @throws ZombieRTObjectException
	 * @throws InvalidProfileException
	 * @throws NotAvailable
	 * @throws InternalError
	 * @throws InterfaceNotImplemented
	 * @throws IOException
	 */
	static public RTComponent createComponent(String pathUri)
			throws CorbaNamingCannotFindException, CorbaNamingResolveException,
			ZombieRTObjectException, InvalidProfileException {
		RTObject rtObject = getComponent(pathUri);

		ComponentProfile profile;
		try {
			profile = rtObject.get_component_profile();
		} catch (Exception ex) {
			throw new ZombieRTObjectException();
		}

		RTComponent component;
		try {
			component = new RTComponent(profile.instance_name, pathUri,
					buildComponentId(rtObject), rtObject.get_configuration()
							.get_active_configuration_set().id, false, "None");

			PortService[] portServices = rtObject.get_ports();
			for (PortService portService : portServices) {
				String name = portService.get_port_profile().name;
				DataPort dataPort = new DataPort(name);
				component.dataPortSet.add(dataPort);
			}

			// Adding Configuration Sets
			_SDOPackage.Configuration configuration = rtObject
					.get_configuration();
			_SDOPackage.ConfigurationSet[] configurationSets = configuration
					.get_configuration_sets();
			for (_SDOPackage.ConfigurationSet configurationSet : configurationSets) {
				ConfigurationSet myConfigurationSet = new ConfigurationSet(
						configurationSet.id);
				for (NameValue configurationData : configurationSet.configuration_data) {
					myConfigurationSet.configurationDataSet
							.add(new ConfigurationData(configurationData.name,
									configurationData.value.toString()));
				}
				component.configurationSetSet.add(myConfigurationSet);
			}

			// Adding Execution Context
			RTC.ExecutionContext[] executionContexts = rtObject
					.get_owned_contexts();
			for (int i = 0; i < executionContexts.length; i++) {
				RTC.ExecutionContext executionContext = executionContexts[i];
				String kindText = null;
				executionContext.get_kind();
				if (executionContext.get_kind().equals(ExecutionKind.PERIODIC)) {
					kindText = "PERIODIC";
				} else {
					executionContext.get_kind();
					if (executionContext.get_kind().equals(
							ExecutionKind.EVENT_DRIVEN)) {
						kindText = "EVENT_DRIVEN";
					} else {
						kindText = "OTHER";
					}
				}
				ExecutionContext myEc = new ExecutionContext(Integer
						.toString(i), "rtsExt:execution_context_ext", kindText,
						Double.toString(executionContext.get_rate()));
				component.executionContextSet.add(myEc);
			}

			// Adding Location
			component.location = new Location(-1, -1, -1, -1);

			org.omg.CORBA.ORB orb = ORBUtil.getOrb();
			String str = orb.object_to_string(rtObject._duplicate());
			component.properties = new Properties("IOR", str);
			return component;
		} catch (Exception e) {
			throw new InvalidProfileException();
		}
	}

	/**
	 * 
	 * createConnector
	 * 
	 * @param componentSet
	 * @param connectorProfile
	 * @return
	 * @throws Exception
	 */
	static public DataPortConnector createConnector(
			Set<RTComponent> componentSet, ConnectorProfile connectorProfile)
			throws Exception {
		String connectorId, name, dataType = "";
		String interfaceType = "", dataflowType = "", subscriptionType = "";
		connectorId = connectorProfile.connector_id;
		name = connectorProfile.name;
		for (_SDOPackage.NameValue nameValue : connectorProfile.properties) {
			if (nameValue.name.equals("dataport.data_type")) {
				dataType = nameValue.value.extract_string();
			} else if (nameValue.name.equals("dataport.dataflow_type")) {
				dataflowType = nameValue.value.extract_string();
			} else if (nameValue.name.equals("dataport.subscription_type")) {
				subscriptionType = nameValue.value.extract_string();
			} else if (nameValue.name.equals("dataport.interface_type")) {
				interfaceType = nameValue.value.extract_string();
			}
		}

		DataPortConnector connector = new DataPortConnector(connectorId, name,
				dataType, interfaceType, dataflowType, subscriptionType);

		String sourcePathUri = null, targetPathUri = null;
		RTObject sourceRTC = connectorProfile.ports[0].get_port_profile().owner;
		RTObject targetRTC = connectorProfile.ports[1].get_port_profile().owner;
		for (RTComponent component : componentSet) {
			if (getComponent(component).equals(sourceRTC)) {
				sourcePathUri = component.get(RTComponent.PATH_URI);
			}
		}
		for (RTComponent component : componentSet) {
			if (getComponent(component).equals(targetRTC)) {
				targetPathUri = component.get(RTComponent.PATH_URI);
			}

		}
		if (sourcePathUri == null || targetPathUri == null) {
			throw new Exception();
		}

		connector.sourcePort = connector.new Port(connectorProfile.ports[0]
				.get_port_profile().name,
				connectorProfile.ports[0].get_port_profile().owner
						.get_component_profile().instance_name, RTSystemBuilder
						.buildComponentId(connectorProfile.ports[0]
								.get_port_profile().owner));
		connector.sourcePort.properties = new Properties("COMPONENT_PATH_ID",
				sourcePathUri);

		connector.targetPort = connector.new Port(connectorProfile.ports[1]
				.get_port_profile().name,
				connectorProfile.ports[1].get_port_profile().owner
						.get_component_profile().instance_name, RTSystemBuilder
						.buildComponentId(connectorProfile.ports[1]
								.get_port_profile().owner));
		connector.targetPort.properties = new Properties("COMPONENT_PATH_ID",
				targetPathUri);

		return connector;
	}

	final static public void downwardSynchronization(RTSystemProfile profile)
			throws CorbaNamingCannotFindException, CorbaNamingResolveException {
		for (RTSObject component : profile.componentSet) {
			try {
				if (component instanceof PyIOComponent) {
					if (!((PyIOComponent) component).isLaunched()) {
						component.setState(RTComponent.OFFLINE);
						continue;
					}
				}
				RTSystemBuilder
						.downwardSynchronization((RTComponent) component);

				ArrayList<PortConnector> connectionList = RTSystemBuilder
						.getOwnedConnections((RTComponent) component);
				for (PortConnector pc : connectionList) {
					if (pc.getSourceComponentPathUri().equals(
							component.get(RTComponent.PATH_URI))) {
						for (RTSObject target : profile.componentSet) {
							if (target.get(RTComponent.PATH_URI).equals(
									pc.getTargetComponentPathUri())) {
								if (profile.getConnectorByName(pc
										.get(PortConnector.NAME)) == null) {
									profile.addPortConnector(pc);

								}
							}
						}
					} else if (pc.getTargetComponentPathUri().equals(
							component.get(RTComponent.PATH_URI))) {
						for (RTSObject source : profile.componentSet) {
							if (source.get(RTComponent.PATH_URI).equals(
									pc.getSourceComponentPathUri())) {
								if (profile.getConnectorByName(pc
										.get(PortConnector.NAME)) == null) {
									profile.addPortConnector(pc);

								}
							}
						}
					}
				}
			} catch (CorbaNamingCannotFindException e) {
				// e.printStackTrace();
				continue;
			} catch (CorbaNamingResolveException e) {
				// e.printStackTrace();
				continue;
			} catch (org.omg.CORBA.COMM_FAILURE e) {
				continue;
			} catch (org.omg.CORBA.OBJECT_NOT_EXIST e) {
				continue;
			}
		}
		for (PortConnector portConnector : profile.getPortConnectors()) {
			downwardSynchronization(portConnector);
		}
	}

	final static public void upwardSynchronization(RTSystemProfile profile) {
		for (PortConnector portConnector : profile.getPortConnectors()) {
			try {
				upwardSynchronization(portConnector);
			} catch (Exception e) {
				// TODO 自動生成された catch ブロック
				// e.printStackTrace();
			}
		}
	}

	final static public void upwardSynchronization(PortConnector connector)
			throws Exception {
		connect(connector);
	}

	final static public void downwardSynchronization(PortConnector connector) {
		connector.setState(PortConnector.OFFLINE);
		RTObject sourceRTObject;
		RTObject targetRTObject;
		try {
			targetRTObject = getComponent(connector.getTargetComponentPathUri());
			sourceRTObject = getComponent(connector.getSourceComponentPathUri());
		} catch (CorbaNamingCannotFindException e) {
			// TODO 自動生成された catch ブロック
			// e.printStackTrace();
			return;
		} catch (CorbaNamingResolveException e) {
			// TODO 自動生成された catch ブロック
			// e.printStackTrace();
			return;
		}

		int sameNameConnectionFoundCount = 0;
		for (PortService portService : sourceRTObject.get_ports()) {
			if (portService.get_port_profile().name.equals(connector
					.getSourcePortName())) {
				for (ConnectorProfile connectorProfile : portService
						.get_connector_profiles()) {
					connectorProfile.name.equals(connector
							.get(PortConnector.NAME));
					sameNameConnectionFoundCount++;
					break;
				}
				break;
			}
		}
		for (PortService portService : targetRTObject.get_ports()) {
			if (portService.get_port_profile().name.equals(connector
					.getTargetPortName())) {
				for (ConnectorProfile connectorProfile : portService
						.get_connector_profiles()) {
					connectorProfile.name.equals(connector
							.get(PortConnector.NAME));
					sameNameConnectionFoundCount++;
					break;
				}
				break;
			}
		}
		if (sameNameConnectionFoundCount == 2) {
			connector.setState(PortConnector.ONLINE_ACTIVE);
		}

	}

	/**
	 * 
	 * findConnector
	 * 
	 * @param connector
	 * @return
	 * @throws CorbaNamingCannotFindException
	 * @throws CorbaNamingResolveException
	 * @throws InvalidProfileException
	 * 
	 *             final static public ConnectorProfile findConnector(
	 *             DataPortConnector connector) throws
	 *             CorbaNamingCannotFindException, CorbaNamingResolveException,
	 *             InvalidProfileException {
	 * 
	 *             logger.entering(RTSystemBuilder.class.getName(),
	 *             "findConnector", connector);
	 * 
	 *             RTObject sourceRTObject = getComponent(connector
	 *             .getSourceComponentPathUri()); RTObject targetRTObject =
	 *             getComponent(connector .getTargetComponentPathUri());
	 * 
	 *             // Building Connector Profile ConnectorProfile prof = new
	 *             ConnectorProfile(); prof.connector_id =
	 *             connector.get(PortConnector.CONNECTOR_ID); prof.name =
	 *             connector.get(PortConnector.NAME); prof.ports = new
	 *             PortService[2];
	 * 
	 *             for (PortService portService : sourceRTObject.get_ports()) {
	 *             if (portService.get_port_profile().name.equals(connector
	 *             .getSourcePortName())) { prof.ports[1] = portService; } } for
	 *             (PortService portService : targetRTObject.get_ports()) { if
	 *             (portService.get_port_profile().name.equals(connector
	 *             .getTargetPortName())) { prof.ports[0] = portService; } } if
	 *             (prof.ports[0] == null || prof.ports[1] == null) { throw new
	 *             InvalidProfileException(); }
	 * 
	 *             ConnectorProfile[] connectorProfiles = prof.ports[0]
	 *             .get_connector_profiles(); for (ConnectorProfile
	 *             connectorProfile : connectorProfiles) { if
	 *             (connectorProfile.name
	 *             .equals(connector.get(PortConnector.NAME)) &&
	 *             (connectorProfile.ports[0].get_port_profile().name
	 *             .equals(connector.getSourcePortName()) ||
	 *             connectorProfile.ports[0]
	 *             .get_port_profile().name.equals(connector
	 *             .getTargetPortName())) &&
	 *             (connectorProfile.ports[1].get_port_profile().name
	 *             .equals(connector.getSourcePortName()) ||
	 *             connectorProfile.ports[1]
	 *             .get_port_profile().name.equals(connector
	 *             .getTargetPortName()))) { return connectorProfile; }
	 * 
	 *             } return null; }
	 * 
	 * 
	 */

	static public void findComponent(String pathUri) throws Exception {
		getComponent(pathUri);
	}

	static public String getNamingUri(String pathUri) {
		StringTokenizer tokenizer = new StringTokenizer(pathUri, "/");
		String namingURI = tokenizer.nextToken();
		String compURI = pathUri.substring(namingURI.length() + 1);
		StringTokenizer tokenizer2 = new StringTokenizer(namingURI, ":");
		if (tokenizer2.countTokens() == 1) {
			namingURI = namingURI + ":2809";
		}
		return namingURI;
	}

	static public String getComponentUri(String pathUri) {
		StringTokenizer tokenizer = new StringTokenizer(pathUri, "/");
		String namingURI = tokenizer.nextToken();
		String compURI = pathUri.substring(namingURI.length() + 1);
		return compURI;
	}

	/**
	 * 
	 * getComponent
	 * 
	 * @param component
	 * @return
	 * @throws CorbaNamingCannotFindException
	 * @throws CorbaNamingResolveException
	 */
	final static public RTC.RTObject getComponent(RTComponent component)
			throws CorbaNamingCannotFindException, CorbaNamingResolveException {
		// component.setState(RTSProperties.OFFLINE);
		return getComponent(component.get(RTComponent.PATH_URI));
		// return RTC.RTObjectHelper.narrow(rtObject._duplicate());
	}

	/**
	 * 
	 * getComponent
	 * 
	 * @param pathUri
	 * @return
	 * @throws CorbaNamingCannotFindException
	 * @throws CorbaNamingResolveException
	 */
	final static public RTC.RTObject getComponent(String pathUri)
			throws CorbaNamingCannotFindException, CorbaNamingResolveException {
		logger.entering(RTSystemBuilder.class.getName(), "getComponent:",
				pathUri);
		String namingURI = RTSystemBuilder.getNamingUri(pathUri);
		String compURI = RTSystemBuilder.getComponentUri(pathUri);

		// load naming service reference
		CorbaNaming naming = CorbaNamingManager.get(namingURI);

		org.omg.CORBA.Object obj;
		try {
			obj = naming.resolve(compURI);
		} catch (Exception e) {
			// e.printStackTrace();
			throw new CorbaNamingResolveException();
		}
		CorbaConsumer<DataFlowComponent> corbaConsumer = new CorbaConsumer<DataFlowComponent>(
				DataFlowComponent.class);
		corbaConsumer.setObject(obj);
		RTObject rtObject = corbaConsumer._ptr();
		return rtObject;
	}

	/**
	 * 
	 * downwardSynchronization
	 * 
	 * @param component
	 * @param dataPort
	 * @throws CorbaNamingCannotFindException
	 * @throws CorbaNamingResolveException
	 */
	static public void downwardSynchronization(RTComponent component,
			DataPort dataPort) throws CorbaNamingCannotFindException,
			CorbaNamingResolveException {
		if (dataPort.getDirection() == DataPort.DIRECTION_UNKNOWN) {
			PortService portService = RTSystemBuilder.getPortService(component,
					dataPort.get(DataPort.RTS_NAME));
			if (portService != null) {
				for (NameValue profile : portService.get_port_profile().properties) {
					if (profile.name.equals("port.port_type")) {
						String port_type = null;
						if (profile.value.type().kind().value() == (TCKind._tk_wstring)) {
							port_type = profile.value.extract_wstring();
						} else {
							port_type = profile.value.extract_string();
						}
						if (port_type.equals("DataInPort")) {
							dataPort.setDirection(DataPort.DIRECTION_IN);
							dataPort.setDataType(RTSystemBuilder.getDataType(
									component, dataPort));
						} else if (port_type.equals("DataOutPort")) {
							dataPort.setDirection(DataPort.DIRECTION_OUT);
							dataPort.setDataType(RTSystemBuilder.getDataType(
									component, dataPort));
						} else if (port_type.equals("CorbaPort")) {
							dataPort.setDirection(DataPort.SERVICE_PORT);
						}
					}
				}
				for (PortInterfaceProfile prof : portService.get_port_profile().interfaces) {
					dataPort.addInterface(prof.type_name, prof.instance_name,
							prof.polarity);
				}

			}
		}
	}

	/**
	 * Downward Synchronization. The argument Component Data is updated with
	 * referring the Launched RT-component that is specified by the URI the
	 * Component Data includes. downwardSynchronization
	 * 
	 * @param component
	 * @throws CorbaNamingResolveException
	 * @throws CorbaNamingCannotFindException
	 * @throws Exception
	 */
	static public void downwardSynchronization(RTComponent component)
			throws CorbaNamingCannotFindException, CorbaNamingResolveException {
		component.setState(RTSProperties.OFFLINE);
		RTObject rtObject = getComponent(component.get(RTComponent.PATH_URI));
		component.setState(RTSProperties.ONLINE_UNKNOWN);
		RTCCondition condition = getComponentCondition(rtObject);
		if (condition.equals(RTCCondition.ACTIVE)) {
			component.setState(RTSProperties.ONLINE_ACTIVE);
		} else if (condition.equals(RTCCondition.INACTIVE)) {
			component.setState(RTSProperties.ONLINE_INACTIVE);
		} else if (condition.equals(RTCCondition.CREATED)) {
			component.setState(RTSProperties.ONLINE_CREATED);
		} else if (condition.equals(RTCCondition.ERROR)) {
			component.setState(RTSProperties.ONLINE_ERROR);
		}

		for (DataPort dataPort : (Set<DataPort>) component.dataPortSet) {
			downwardSynchronization(component, dataPort);
		}
	}

	/**
	 * Get ComponentCondition. This method always uses the first execution
	 * context.
	 * 
	 * @param rtObject
	 * @return
	 */
	final static public RTCCondition getComponentCondition(RTObject rtObject) {
		RTC.ExecutionContext[] ecs = rtObject.get_owned_contexts();
		LifeCycleState state = ecs[0].get_component_state(rtObject);
		if (state.equals(LifeCycleState.ACTIVE_STATE)) {
			return RTCCondition.ACTIVE;
		} else if (state.equals(LifeCycleState.INACTIVE_STATE)) {
			return RTCCondition.INACTIVE;
		} else if (state.equals(LifeCycleState.CREATED_STATE)) {
			return RTCCondition.CREATED;
		} else if (state.equals(LifeCycleState.ERROR_STATE)) {
			return RTCCondition.ERROR;
		}
		return RTCCondition.NONE;
	}

	/**
	 * Get ComponentCondition. This method always uses the first execution
	 * context.
	 * 
	 * @param pathUri
	 * @return
	 * @throws CorbaNamingResolveException
	 * @throws CorbaNamingCannotFindException
	 */
	final static public RTCCondition getComponentCondition(String pathUri)
			throws CorbaNamingCannotFindException, CorbaNamingResolveException {
		RTObject rtObject = getComponent(pathUri);
		return getComponentCondition(rtObject);
	}

	/**
	 * Get ComponentCondition. This method always uses the first execution
	 * context.
	 * 
	 * @param component
	 * @return
	 * @throws CorbaNamingResolveException
	 * @throws CorbaNamingCannotFindException
	 */
	final static public RTCCondition getComponentCondition(
			RTSProperties component) throws CorbaNamingCannotFindException,
			CorbaNamingResolveException {
		return getComponentCondition(component.get(RTComponent.PATH_URI));
	}

	/**
	 * 
	 * <div lang="ja">
	 * �R���|�[�l���g�v���t�@�C������RTObject���������đ�����activate
	 * 
	 * @param component
	 * @throws Exception
	 *             </div> <div lang="en">
	 * 
	 * @param component
	 * @throws Exception
	 *             </div>
	 */
	static public void activateComponent(RTComponent component)
			throws Exception {
		try {
			logger.info("activateComponent:"
					+ component.get(RTComponent.INSTANCE_NAME));
			RTObject obj = getComponent(component);

			ExecutionContextListHolder ecListHolder = new ExecutionContextListHolder();
			ecListHolder.value = new RTC.ExecutionContext[1];
			ecListHolder.value = obj.get_owned_contexts();

			LifeCycleState currentState = ecListHolder.value[0]
					.get_component_state(obj);
			if (!currentState.equals(LifeCycleState.INACTIVE_STATE)) {

				// TODO: Invalid pre-state

				return;
			}

			ecListHolder.value[0].activate_component(obj);

			/**
			 * LifeCycleState state; do { try { Thread.sleep(10); } catch
			 * (Exception ex) { ex.printStackTrace(); } state =
			 * ecListHolder.value[0].get_component_state(obj); } while
			 * (!state.equals(LifeCycleState.ACTIVE_STATE));
			 */
		} catch (org.omg.CORBA.COMM_FAILURE e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * <div lang="ja">
	 * �R���|�[�l���g�v���t�@�C������RTObject���������đ�����deactivate
	 * 
	 * @param component
	 * @throws Exception
	 *             </div> <div lang="en">
	 * 
	 * @param component
	 * @throws Exception
	 *             </div>
	 */
	static public void deactivateComponent(RTComponent component)
			throws Exception {
		try {
			logger.info("deactivateComponent:"
					+ component.get(RTComponent.INSTANCE_NAME));
			RTObject obj = getComponent(component);

			ExecutionContextListHolder ecListHolder = new ExecutionContextListHolder();
			ecListHolder.value = new RTC.ExecutionContext[1];
			ecListHolder.value = obj.get_owned_contexts();

			LifeCycleState currentState = ecListHolder.value[0]
					.get_component_state(obj);
			if (!currentState.equals(LifeCycleState.ACTIVE_STATE)) {
				throw new InvalidPreStateException();
			}

			ecListHolder.value[0].deactivate_component(obj);
		} catch (org.omg.CORBA.COMM_FAILURE e) {
			e.printStackTrace();
		}
	}

	/**
	 * Reset Component. This method always usess the first execution context.
	 * 
	 * @param component
	 * @throws Exception
	 */
	static public void resetComponent(RTComponent component) throws Exception {
		try {
			logger.info("resetComponent:"
					+ component.get(RTComponent.INSTANCE_NAME));
			RTObject obj = getComponent(component);

			ExecutionContextListHolder ecListHolder = new ExecutionContextListHolder();
			ecListHolder.value = new RTC.ExecutionContext[1];
			ecListHolder.value = obj.get_owned_contexts();
			LifeCycleState currentState = ecListHolder.value[0]
					.get_component_state(obj);
			if (!currentState.equals(LifeCycleState.ERROR_STATE)) {
				throw new InvalidPreStateException();
			}

			ecListHolder.value[0].reset_component(obj);
		} catch (org.omg.CORBA.COMM_FAILURE e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * exitComponent. This method always usess the first execution context.
	 * 
	 * @param component
	 * @throws Exception
	 */
	static public void exitComponent(RTComponent component) throws Exception {
		try {
			logger.info("exitComponent:"
					+ component.get(RTComponent.INSTANCE_NAME));
			RTObject obj = getComponent(component);

			obj.exit();
		} catch (org.omg.CORBA.COMM_FAILURE e) {
			e.printStackTrace();
		}
	}

	static public String getDataType(RTComponent component, DataPort dataPort)
			throws CorbaNamingCannotFindException, CorbaNamingResolveException {
		if (dataPort.getDataType() != null) {
			return dataPort.getDataType();
		}

		PortService port = RTSystemBuilder.getPortService(component, dataPort
				.get(DataPort.RTS_NAME));
		if (port != null) {
			for (NameValue nv : port.get_port_profile().properties) {
				if (nv.name.equals("dataport.data_type")) {
					if (nv.value.type().kind().value() == TCKind._tk_wstring) {
						return nv.value.extract_wstring();
					} else {
						return nv.value.extract_string();
					}
				}
			}
		}
		return null;
	}

	static public boolean isProvider(RTComponent component, DataPort dataPort)
			throws CorbaNamingCannotFindException, CorbaNamingResolveException {
		PortService port = getPortService(component, dataPort
				.get(DataPort.RTS_NAME));
		if (port != null) {

			for (PortInterfaceProfile prof : port.get_port_profile().interfaces) {
				return prof.polarity.value() == 0;
			}
		}
		return false;
	}

	static public boolean isConsumer(RTComponent component, DataPort dataPort)
			throws CorbaNamingCannotFindException, CorbaNamingResolveException {
		PortService port = getPortService(component, dataPort
				.get(DataPort.RTS_NAME));
		if (port != null) {

			for (PortInterfaceProfile prof : port.get_port_profile().interfaces) {
				return prof.polarity.value() == 1;
			}
		}
		return false;
	}

	static public List<String> getInterfaceNameList(RTComponent component,
			DataPort dataPort) throws CorbaNamingCannotFindException,
			CorbaNamingResolveException {
		ArrayList<String> inList = new ArrayList<String>();
		if (component instanceof PyIOComponent) {
			return inList; // null list.
		}

		if (dataPort.getInterfaceList() != null) {
			for (Interface intf : dataPort.getInterfaceList()) {
				inList.add(intf.getName());
			}
			return inList;
		}
		PortService port = getPortService(component, dataPort
				.get(DataPort.RTS_NAME));
		if (port != null) {
			for (PortInterfaceProfile prof : port.get_port_profile().interfaces) {
				inList.add(prof.type_name);
			}
		}
		return inList;
	}

	static public boolean isConnectable(RTComponent sourceComponent,
			DataPort sourceDataPort, RTComponent targetComponent,
			DataPort targetDataPort) throws CorbaNamingCannotFindException,
			CorbaNamingResolveException {
		boolean connectable = false;
		String dataType = RTSystemBuilder.getDataType(sourceComponent,
				sourceDataPort);
		if (!dataType.startsWith("IDL:RTC/")) {
			dataType = "IDL:RTC/" + dataType + ":1.0";
		}
		String targetDataType = RTSystemBuilder.getDataType(targetComponent,
				targetDataPort);
		if (!targetDataType.startsWith("IDL:RTC/")) {
			targetDataType = "IDL:RTC/" + targetDataType + ":1.0";
		}

		if (dataType != null && dataType.equals(targetDataType)) {
			// / if both ports are DataPort and if both ports' DataTypes are
			// same
			if ((sourceDataPort.getDirection() == DataPort.DIRECTION_IN && targetDataPort
					.getDirection() == DataPort.DIRECTION_OUT)
					|| (sourceDataPort.getDirection() == DataPort.DIRECTION_OUT && targetDataPort
							.getDirection() == DataPort.DIRECTION_IN)) {
				connectable = true; // Valid DataPort Connection.

			}
		} else if (dataType == null && targetDataType == null) { // ServicePortConnection
			String interfaceName = RTSystemBuilder
					.getConnectionServiceInterfaceName(sourceComponent,
							sourceDataPort, targetComponent, targetDataPort);
			if (interfaceName != null) {
				DataPort.Interface sourceInterface = sourceDataPort
						.getInterfaceByName(interfaceName);
				DataPort.Interface targetInterface = targetDataPort
						.getInterfaceByName(interfaceName);
				if ((sourceInterface.getPolarity() == Interface.POLARITY_CONSUMER && targetInterface
						.getPolarity() == Interface.POLARITY_PROVIDER)
						|| (sourceInterface.getPolarity() == Interface.POLARITY_PROVIDER && targetInterface
								.getPolarity() == Interface.POLARITY_CONSUMER)) {
					connectable = true;
				}

			}
		}
		return connectable;
	}

	public static String getConnectionServiceInterfaceName(
			RTComponent sourceComponent, DataPort sourceDataPort,
			RTComponent targetComponent, DataPort targetDataPort)
			throws CorbaNamingCannotFindException, CorbaNamingResolveException {
		List<String> sourceInterfaceNameList = RTSystemBuilder
				.getInterfaceNameList(sourceComponent, sourceDataPort);
		List<String> targetInterfaceNameList = RTSystemBuilder
				.getInterfaceNameList(targetComponent, targetDataPort);
		for (String interfaceName : sourceInterfaceNameList) {
			for (String targetInterfaceName : targetInterfaceNameList) {
				if (interfaceName != null
						&& interfaceName.equals(targetInterfaceName)) {
					return interfaceName;
				}
			}
		}
		return null;
	}

	/**
	 * getOwnedConnectioN
	 * 
	 * @param component
	 * @return
	 * @throws CorbaNamingResolveException
	 * @throws CorbaNamingCannotFindException
	 * @throws Exception
	 */
	public static ArrayList<PortConnector> getOwnedConnections(
			RTComponent component) throws CorbaNamingCannotFindException,
			CorbaNamingResolveException {
		ArrayList<PortConnector> connectorList = new ArrayList<PortConnector>();
		RTObject obj = RTSystemBuilder.getComponent(component);
		for (PortService portService : obj.get_ports()) {
			for (ConnectorProfile connectorProfile : portService
					.get_connector_profiles()) {
				String connectorId, name, dataType = "";
				String interfaceType = "", dataflowType = "", subscriptionType = "";
				connectorId = connectorProfile.connector_id;
				name = connectorProfile.name;
				for (_SDOPackage.NameValue nameValue : connectorProfile.properties) {
					if (nameValue.name.equals("dataport.data_type")) {
						dataType = nameValue.value.extract_string();
					} else if (nameValue.name.equals("dataport.dataflow_type")) {
						dataflowType = nameValue.value.extract_string();
					} else if (nameValue.name
							.equals("dataport.subscription_type")) {
						subscriptionType = nameValue.value.extract_string();
					} else if (nameValue.name.equals("dataport.interface_type")) {
						interfaceType = nameValue.value.extract_string();
					}
				}
				PortConnector connector;
				if (dataType.length() != 0) {
					connector = new DataPortConnector(connectorId, name,
							dataType, interfaceType, dataflowType,
							subscriptionType);
				} else {
					connector = new ServicePortConnector(connectorId, name);
				}
				String sourcePathUri = null, targetPathUri = null;
				RTObject sourceRTC = connectorProfile.ports[0]
						.get_port_profile().owner;
				RTObject targetRTC = connectorProfile.ports[1]
						.get_port_profile().owner;

				sourcePathUri = RTSystemBuilder.getPathUri(sourceRTC);
				targetPathUri = RTSystemBuilder.getPathUri(targetRTC);

				if (sourcePathUri == null || targetPathUri == null) {
					throw new CorbaNamingCannotFindException();
				}

				connector.sourcePort = connector.new Port(
						connectorProfile.ports[0].get_port_profile().name,
						connectorProfile.ports[0].get_port_profile().owner
								.get_component_profile().instance_name,
						RTSystemBuilder
								.buildComponentId(connectorProfile.ports[0]
										.get_port_profile().owner));
				connector.sourcePort.properties = new Properties(
						"COMPONENT_PATH_ID", sourcePathUri);

				connector.targetPort = connector.new Port(
						connectorProfile.ports[1].get_port_profile().name,
						connectorProfile.ports[1].get_port_profile().owner
								.get_component_profile().instance_name,
						RTSystemBuilder
								.buildComponentId(connectorProfile.ports[1]
										.get_port_profile().owner));
				connector.targetPort.properties = new Properties(
						"COMPONENT_PATH_ID", targetPathUri);

				connectorList.add(connector);
			}
		}
		return connectorList;
	}

	/**
	 * Get Path URI from RTObject.
	 * 
	 * This method searches the full path URI using the registered NamingService
	 * address. So if no NamingService is registered in the CorbaNamingManager,
	 * this will fail.
	 * 
	 * @param sourceRTC
	 * @return full length path uri of rt-object. if failed, null will returned.
	 */
	final public static String getPathUri(RTObject rtobject) {
		String namingName = "";

		ComponentProfile prof = rtobject.get_component_profile();
		for (NameValue nv : prof.properties) {
			if (nv.name.equals("naming.names")) {
				if (nv.value.type().kind().value() == TCKind._tk_wstring) {
					namingName = nv.value.extract_wstring();
				} else {
					namingName = nv.value.extract_string();
				}
			}
		}

		for (String address : CorbaNamingManager.getAddressSet()) {
			String componentUri = address + "/" + namingName;
			if (CorbaNamingManager.find(componentUri)) {
				return componentUri;
			}
		}

		return null;
	}

	/**
	 * getComponentConditionFromPath
	 *
	 * @param pathUri
	 * @return
	 * @throws CorbaNamingResolveException 
	 * @throws CorbaNamingCannotFindException 
	 */
	public static RTCCondition getComponentConditionFromPath(String pathUri) throws CorbaNamingCannotFindException, CorbaNamingResolveException {
		return getComponentCondition(pathUri);
	}

	/**
	 * getComponentConditionFromRecord
	 *
	 * @param component
	 * @return
	 * @throws CorbaNamingResolveException 
	 * @throws CorbaNamingCannotFindException 
	 */
	public static RTCCondition getComponentConditionFromRecord(
			RTSObject component) throws CorbaNamingCannotFindException, CorbaNamingResolveException {
		return getComponentCondition(component);
	}
}