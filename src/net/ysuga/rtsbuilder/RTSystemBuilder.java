package net.ysuga.rtsbuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.logging.Logger;

import jp.go.aist.rtm.RTC.CorbaNaming;
import jp.go.aist.rtm.RTC.port.CorbaConsumer;
import jp.go.aist.rtm.RTC.util.CORBA_SeqUtil;
import jp.go.aist.rtm.RTC.util.NVUtil;
import jp.go.aist.rtm.RTC.util.ORBUtil;
import net.ysuga.rtsystem.profile.Component;
import net.ysuga.rtsystem.profile.ConfigurationData;
import net.ysuga.rtsystem.profile.ConfigurationSet;
import net.ysuga.rtsystem.profile.DataPort;
import net.ysuga.rtsystem.profile.DataPort.Interface;
import net.ysuga.rtsystem.profile.DataPortConnector;
import net.ysuga.rtsystem.profile.ExecutionContext;
import net.ysuga.rtsystem.profile.Location;
import net.ysuga.rtsystem.profile.PortConnector;
import net.ysuga.rtsystem.profile.Properties;
import net.ysuga.rtsystem.profile.RTSProperties;
import net.ysuga.rtsystem.profile.RTSystemProfile;
import OpenRTM.DataFlowComponent;
import RTC.ComponentProfile;
import RTC.ConnectorProfile;
import RTC.ConnectorProfileHolder;
import RTC.ExecutionContextListHolder;
import RTC.LifeCycleState;
import RTC.PortInterfaceProfile;
import RTC.PortService;
import RTC.RTObject;
import _SDOPackage.NVListHolder;
import _SDOPackage.NameValue;

/**
 * @author ysuga
 * 
 */
public class RTSystemBuilder {
	static private Logger logger;

	static private Map<String, CorbaNaming> corbaNamingMap;

	static {
		logger = Logger.getLogger("net.ysuga.rtsbuilder");
		corbaNamingMap = new HashMap<String, CorbaNaming>();
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
		for (Component component : rtSystemProfile.componentSet) {
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
	 */
	public static void searchConnections(RTSystemProfile rtSystemProfile) {
		for (DataPortConnector connector : rtSystemProfile.dataPortConnectorSet) {
			try {
				findConnector(connector);
			} catch (Exception e) {
			}
		}
	}

	/**
	 * Configure All RTCs in RTS profile.
	 * 
	 * @param rtSystemProfile
	 */
	static public void configure(RTSystemProfile rtSystemProfile) {
		logger.info("configure:" + rtSystemProfile.get(RTSystemProfile.ID));
		for (Component component : rtSystemProfile.componentSet) {
			try {
				configureComponent(component);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 
	 * <div lang="ja"> RT�V�X�e���̍\�z
	 * 
	 * @param rtSystemProfile
	 *            </div> <div lang="en">
	 * @param rtSystemProfile
	 *            </div>
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
	 * <div lang="ja"> RT�R���|�[�l���g�̃R���t�B�O���[�V����
	 * 
	 * @param component
	 * @throws Exception
	 *             </div> <div lang="en">
	 * @param component
	 * @throws Exception
	 *             </div>
	 */
	static public void configureComponent(Component component) throws Exception {
		logger.info("configureComponent:"
				+ component.get(Component.INSTANCE_NAME));
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
			if (component.get(Component.ACTIVE_CONFIGURATION_SET).equals(
					sdoConfigurationSet.id)) {
				sdoConfiguration
						.activate_configuration_set(sdoConfigurationSet.id);
			}
		}
	}

	/**
	 * 
	 * <div lang="ja"> RT�V�X�e���̔j��D�v���t�@�C���ɓo�^����Ă��邷�ׂĂ̐ڑ�������
	 * 
	 * @param rtSystemProfile
	 *            </div> <div lang="en">
	 * 
	 * @param rtSystemProfile
	 *            </div>
	 */
	static public void destroyRTSystem(RTSystemProfile rtSystemProfile) {
		logger.info("destroyRTSystem:"
				+ rtSystemProfile.get(RTSystemProfile.ID));
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
	 * <div lang="ja">
	 * �v���t�@�C���ɓo�^����Ă��邷�ׂĂ�RT�R���|�[�l���g�̎��|�[�g�̃R�l�N�V�������폜
	 * 
	 * @param rtSystemProfile
	 *            </div> <div lang="en">
	 * 
	 * @param rtSystemProfile
	 *            </div>
	 */
	static public void clearAllConnection(RTSystemProfile rtSystemProfile) {
		logger.info("clearAllConnection:"
				+ rtSystemProfile.get(RTSystemProfile.ID));
		for (Component component : rtSystemProfile.componentSet) {
			try {
				clearAllConnection(component);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 
	 * <div lang="ja"> �R���|�[�l���g�̂��ׂĂ̐ڑ����폜
	 * 
	 * @param component
	 *            </div> <div lang="en">
	 * 
	 * @param component
	 *            </div>
	 * @throws Exception
	 */
	public static void clearAllConnection(Component component) throws Exception {
		logger.info("clearAllConnection:" + component.get(Component.ID));
		RTObject rtObject = getComponent(component);
		for (PortService portService : rtObject.get_ports()) {
			portService.disconnect_all();
		}
	}

	/**
	 * 
	 * <div lang="ja"> �R�l�N�^�[���폜
	 * 
	 * @param connector
	 * @throws Exception
	 *             </div> <div lang="en">
	 * 
	 * @param connector
	 * @throws Exception
	 *             </div>
	 */
	static public void disconnect(DataPortConnector connector) throws Exception {
		logger.info("disconnect:" + connector.getSourceComponentInstanceName()
				+ connector.getSourcePortName() + "->"
				+ connector.getTargetComponentInstanceName()
				+ connector.getTargetPortName());
		RTObject sourceRTObject = getComponent(connector
				.getSourceComponentPathUri());
		for (PortService portService : sourceRTObject.get_ports()) {
			if (portService.get_port_profile().name.equals(connector
					.getSourcePortName())) {
				portService.disconnect(connector.get(PortConnector.CONNECTOR_ID));
			}
		}
	}
	
	static public void disconnect(PortConnector connector) throws Exception {
		logger.info("disconnect:" + connector.getSourceComponentInstanceName()
				+ connector.getSourcePortName() + "->"
				+ connector.getTargetComponentInstanceName()
				+ connector.getTargetPortName());
		RTObject sourceRTObject = getComponent(connector
				.getSourceComponentPathUri());
		for (PortService portService : sourceRTObject.get_ports()) {
			if (portService.get_port_profile().name.equals(connector
					.getSourcePortName())) {
				portService.disconnect(connector.get(PortConnector.CONNECTOR_ID));
			}
		}
	}

	static public PortService getPortService(Component component,
			String portName) throws Exception {
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
	 * <div lang="ja"> �R�l�N�^�[���쐬���Đڑ�������
	 * 
	 * @param connector
	 * @throws Exception
	 *             </div> <div lang="en">
	 * 
	 * @param connector
	 * @throws Exception
	 *             </div>
	 */
	static public void connect(DataPortConnector connector) throws Exception {
		logger.info("connect:" + connector.getSourceComponentInstanceName()
				+ connector.getSourcePortName() + "->"
				+ connector.getTargetComponentInstanceName()
				+ connector.getTargetPortName());

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
			throw new Exception("Invalid RTS Profile");
		}

		NVListHolder nvholder = new NVListHolder();
		nvholder.value = prof.properties;
		if (nvholder.value == null)
			nvholder.value = new NameValue[0];
		CORBA_SeqUtil.push_back(
				nvholder,
				NVUtil.newNVString("dataport.interface_type",
						connector.get(PortConnector.INTERFACE_TYPE)));
		CORBA_SeqUtil.push_back(
				nvholder,
				NVUtil.newNVString("dataport.dataflow_type",
						connector.get(PortConnector.DATAFLOW_TYPE)));
		CORBA_SeqUtil.push_back(
				nvholder,
				NVUtil.newNVString("dataport.subscription_type",
						connector.get(PortConnector.SUBSCRIPTION_TYPE)));
		prof.properties = nvholder.value;

		ConnectorProfileHolder proflist = new ConnectorProfileHolder();
		proflist.value = prof;

		if (prof.ports[0].connect(proflist) != RTC.ReturnCode_t.RTC_OK) {
			throw new Exception("Cannot Connect");
		}
	}

	static public void connect(PortConnector connector) throws Exception {
		// TODO:
		logger.info("connect:" + connector.getSourceComponentInstanceName()
				+ connector.getSourcePortName() + "->"
				+ connector.getTargetComponentInstanceName()
				+ connector.getTargetPortName());

		if(connector instanceof DataPortConnector) {
			connect((DataPortConnector)connector);
			return;
		}
		
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
			throw new Exception("Invalid RTS Profile");
		}

		/**
		NVListHolder nvholder = new NVListHolder();
		nvholder.value = prof.properties;
		if (nvholder.value == null)
			nvholder.value = new NameValue[0];
		CORBA_SeqUtil.push_back(
				nvholder,
				NVUtil.newNVString("dataport.interface_type",
						connector.get(DataPortConnector.INTERFACE_TYPE)));
		CORBA_SeqUtil.push_back(
				nvholder,
				NVUtil.newNVString("dataport.dataflow_type",
						connector.get(DataPortConnector.DATAFLOW_TYPE)));
		CORBA_SeqUtil.push_back(
				nvholder,
				NVUtil.newNVString("dataport.subscription_type",
						connector.get(DataPortConnector.SUBSCRIPTION_TYPE)));

		prof.properties = nvholder.value;
						**/
		ConnectorProfileHolder proflist = new ConnectorProfileHolder();
		proflist.value = prof;

		if (prof.ports[0].connect(proflist) != RTC.ReturnCode_t.RTC_OK) {
			throw new Exception("Cannot Connect");
		}
	}
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
		for (Component component : rtSystemProfile.componentSet) {
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
		for (Component component : rtSystemProfile.componentSet) {
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
		for (Component component : rtSystemProfile.componentSet) {
			resetComponent(component);
		}
	}

	/**
	 * 
	 * <div lang="ja">
	 * 
	 * @param rtObject
	 * @return </div> <div lang="en">
	 * 
	 * @param rtObject
	 * @return </div>
	 */
	static public String buildComponentId(RTC.RTObject rtObject) {
		ComponentProfile profile;
		profile = rtObject.get_component_profile();
		return "RTC:" + profile.vendor + ":" + profile.category + ":"
				+ profile.type_name + ":" + profile.version;
	}

	/**
	 * 
	 * <div lang="ja">
	 * 
	 * @param pathUri
	 * @return
	 * @throws Exception
	 *             </div> <div lang="en">
	 * 
	 * @param pathUri
	 * @return
	 * @throws Exception
	 *             </div>
	 */
	static public Component createComponent(String pathUri) throws Exception {
		RTObject rtObject = getComponent(pathUri);
		ComponentProfile profile;
		try {
			// TODO: �ǂ��������RTObject�̗L������S�Ɍ��؂ł���̂��킩��܂���D
			profile = rtObject.get_component_profile();
		} catch (Exception ex) {
			return null;
		}

		Component component = new Component(profile.instance_name, pathUri,
				buildComponentId(rtObject), rtObject.get_configuration()
						.get_active_configuration_set().id, false, "None");

		PortService[] portServices = rtObject.get_ports();
		for (PortService portService : portServices) {
			String name = portService.get_port_profile().name;
			/*
			 * NameValue[] nvs = portService.get_port_profile().properties;
			 * String dataType = ""; for(NameValue nv : nvs) {
			 * if(nv.name.equals("dataport.data_type")) { dataType =
			 * nv.value.extract_wstring(); } }
			 */
			DataPort dataPort = new DataPort(name);
			component.dataPortSet.add(dataPort);
		}

		// Adding Configuration Sets
		_SDOPackage.Configuration configuration = rtObject.get_configuration();
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
			if(executionContext.get_kind().equals(executionContext.get_kind().PERIODIC)) {
				kindText = "PERIODIC";
			} else if(executionContext.get_kind().equals(executionContext.get_kind().EVENT_DRIVEN)) {
				kindText = "EVENT_DRIVEN";
			} else {
				kindText = "OTHER";
			}
			ExecutionContext myEc = new ExecutionContext(Integer.toString(i),
					"rtsExt:execution_context_ext", kindText, Double.toString(executionContext
							.get_rate()));
			component.executionContextSet.add(myEc);
		}

		// Adding Location
		component.location = new Location(-1, -1, -1, -1);

		org.omg.CORBA.ORB orb = ORBUtil.getOrb();
		String str = orb.object_to_string(rtObject._duplicate());
		component.properties = new Properties("IOR", str);
		return component;
	}

	/**
	 * 
	 * <div lang="ja"> �ڑ��v���t�@�C���̍쐬
	 * 
	 * @param componentSet
	 * @param connectorProfile
	 * @return
	 * @throws Exception
	 *             </div> <div lang="en">
	 * 
	 * @param componentSet
	 * @param connectorProfile
	 * @return
	 * @throws Exception
	 *             </div>
	 */
	static public DataPortConnector createConnector(Set<Component> componentSet,
			ConnectorProfile connectorProfile) throws Exception {
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

		DataPortConnector connector = new DataPortConnector(connectorId, name, dataType,
				interfaceType, dataflowType, subscriptionType);

		String sourcePathUri = null, targetPathUri = null;
		RTObject sourceRTC = connectorProfile.ports[0].get_port_profile().owner;
		RTObject targetRTC = connectorProfile.ports[1].get_port_profile().owner;
		for (Component component : componentSet) {
			if (getComponent(component).equals(sourceRTC)) {
				sourcePathUri = component.get(Component.PATH_URI);
			}
		}
		for (Component component : componentSet) {
			if (getComponent(component).equals(targetRTC)) {
				targetPathUri = component.get(Component.PATH_URI);
			}

		}
		if (sourcePathUri == null || targetPathUri == null) {
			throw new Exception();
		}

		connector.sourcePort = connector.new Port(
				connectorProfile.ports[0].get_port_profile().name,
				connectorProfile.ports[0].get_port_profile().owner
						.get_component_profile().instance_name,
				RTSystemBuilder.buildComponentId(connectorProfile.ports[0]
						.get_port_profile().owner));
		connector.sourcePort.properties = new Properties(
				"COMPONENT_PATH_ID", sourcePathUri);

		connector.targetPort = connector.new Port(
				connectorProfile.ports[1].get_port_profile().name,
				connectorProfile.ports[1].get_port_profile().owner
						.get_component_profile().instance_name,
				RTSystemBuilder.buildComponentId(connectorProfile.ports[1]
						.get_port_profile().owner));
		connector.targetPort.properties = new Properties(
				"COMPONENT_PATH_ID", targetPathUri);

		return connector;
	}

	static public ConnectorProfile findConnector(DataPortConnector connector)
			throws Exception {

		logger.info("findConnector:"
				+ connector.getSourceComponentInstanceName()
				+ connector.getSourcePortName() + "->"
				+ connector.getTargetComponentInstanceName()
				+ connector.getTargetPortName());

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
			throw new Exception("Invalid RTS Profile");
		}

		ConnectorProfile[] con_pros = prof.ports[0].get_connector_profiles();
		for (ConnectorProfile con_prof : con_pros) {
			if (/*
				 * con_prof.connector_id.equals(connector.get(Connector.CONNECTOR_ID
				 * )) &&
				 */
			con_prof.name.equals(connector.get(PortConnector.NAME))
					&& (con_prof.ports[0].get_port_profile().name
							.equals(connector.getSourcePortName()) || con_prof.ports[0]
							.get_port_profile().name.equals(connector
							.getTargetPortName()))
					&& (con_prof.ports[1].get_port_profile().name
							.equals(connector.getSourcePortName()) || con_prof.ports[1]
							.get_port_profile().name.equals(connector
							.getTargetPortName()))) {

				connector.setState(RTSProperties.ONLINE_ACTIVE);

				return con_prof;
			}

		}

		connector.setState(RTSProperties.OFFLINE);

		return null;
	}

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
	 * <div lang="ja"> URI����R���|�[�l���g������
	 * 
	 * @param pathUri
	 * @return
	 * @throws Exception
	 *             </div> <div lang="en">
	 * 
	 * @param pathUri
	 * @return
	 * @throws Exception
	 *             </div>
	 */
	static public RTC.RTObject getComponent(String pathUri) throws Exception {
		logger.info("getComponent:" + pathUri);
		String namingURI = RTSystemBuilder.getNamingUri(pathUri);
		String compURI = RTSystemBuilder.getComponentUri(pathUri);

		// load naming service reference
		CorbaNaming naming = corbaNamingMap.get(namingURI);
		if (naming == null || !naming.isAlive()) {
			naming = new CorbaNaming(ORBUtil.getOrb(), namingURI);
			corbaNamingMap.put(namingURI, naming);
		}

		org.omg.CORBA.Object obj = naming.resolve(compURI); // throws NotFound,
		// CannotProceed,
		// InvalidName
		CorbaConsumer<DataFlowComponent> corbaConsumer = new CorbaConsumer<DataFlowComponent>(
				DataFlowComponent.class);
		corbaConsumer.setObject(obj);
		RTObject rtObject = corbaConsumer._ptr();

		return rtObject;
	}

	static public void findComponent(Component component) throws Exception {
		getComponent(component);
	}

	/**
	 * <div lang="ja"> �R���|�[�l���g�v���t�@�C������RTObject������
	 * 
	 * @param component
	 * @return
	 * @throws Exception
	 *             </div> <div lang="en">
	 * 
	 * @param component
	 * @return
	 * @throws Exception
	 *             </div>
	 */
	static public RTC.RTObject getComponent(Component component)
			throws Exception {
		component.setState(RTSProperties.OFFLINE);
		RTObject rtObject = getComponent(component.get(Component.PATH_URI));

		component.setState(RTSProperties.ONLINE_UNKNOWN);
		RTC.ExecutionContext[] ecs = rtObject.get_owned_contexts();
		LifeCycleState state = ecs[0].get_component_state(rtObject);
		if (state.equals(LifeCycleState.ACTIVE_STATE)) {
			component.setState(RTSProperties.ONLINE_ACTIVE);
		} else if (state.equals(LifeCycleState.INACTIVE_STATE)) {
			component.setState(RTSProperties.ONLINE_INACTIVE);
		} else if (state.equals(LifeCycleState.CREATED_STATE)) {
			component.setState(RTSProperties.ONLINE_CREATED);
		} else if (state.equals(LifeCycleState.ERROR_STATE)) {
			component.setState(RTSProperties.ONLINE_ERROR);
		}

		for (DataPort dataPort : (Set<DataPort>) component.dataPortSet) {
			if (dataPort.getDirection() == DataPort.DIRECTION_UNKNOWN) {
				PortService[] portServices = rtObject.get_ports();
				for (int i = 0; i < portServices.length; i++) {
					if (portServices[i].get_port_profile().name.equals(dataPort
							.get(DataPort.RTS_NAME))) {
						for (NameValue profile : portServices[i]
								.get_port_profile().properties) {
							if (profile.name.equals("port.port_type")) {
								String port_type = profile.value
										.extract_string();
								if (port_type.equals("DataInPort")) {
									dataPort.setDirection(DataPort.DIRECTION_IN);
									dataPort.setDataType(RTSystemBuilder.getDataType(component, dataPort));
								} else if (port_type.equals("DataOutPort")) {
									dataPort.setDirection(DataPort.DIRECTION_OUT);
									dataPort.setDataType(RTSystemBuilder.getDataType(component, dataPort));
								} else if (port_type.equals("CorbaPort")) {
									dataPort.setDirection(DataPort.SERVICE_PORT);
								}
							}
						}
						for (PortInterfaceProfile prof : portServices[i]
								.get_port_profile().interfaces) {
							dataPort.addInterface(prof.type_name,
									prof.instance_name, prof.polarity);
						}

					}
				}
			}
		}

		return rtObject;
	}

	/**
	 * <div lang="ja">
	 * 
	 * @param pathUri
	 * @return
	 * @throws Exception
	 *             </div> <div lang="en">
	 * 
	 * @param pathUri
	 * @return
	 * @throws Exception
	 *             </div>
	 */
	static public RTCCondition getComponentCondition(String pathUri)
			throws Exception {
		RTObject rtObject;
		try {
			rtObject = getComponent(pathUri);
		} catch (Exception ex) {
			logger.warning("Component(" + pathUri + ") cannot found");
			return RTCCondition.NONE;
		}

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
		} else {
			throw new Exception();
		}

	}

	/**
	 * <div lang="ja"> �R���|�[�l���g�v���t�@�C����RTC���������C�����ɏ�Ԃ��擾����
	 * 
	 * @param component
	 * @return
	 * @throws Exception
	 *             </div> <div lang="en">
	 * 
	 * @param component
	 * @return
	 * @throws Exception
	 *             </div>
	 */
	static public RTCCondition getComponentCondition(RTSProperties component)
			throws Exception {
		return getComponentCondition(component.get(Component.PATH_URI));
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
	static public void activateComponent(Component component) throws Exception {
		try {
			logger.info("activateComponent:"
					+ component.get(Component.INSTANCE_NAME));
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
	static public void deactivateComponent(Component component)
			throws Exception {
		try {
			logger.info("deactivateComponent:"
					+ component.get(Component.INSTANCE_NAME));
			RTObject obj = getComponent(component);

			ExecutionContextListHolder ecListHolder = new ExecutionContextListHolder();
			ecListHolder.value = new RTC.ExecutionContext[1];
			ecListHolder.value = obj.get_owned_contexts();

			LifeCycleState currentState = ecListHolder.value[0]
					.get_component_state(obj);
			if (!currentState.equals(LifeCycleState.ACTIVE_STATE)) {

				// TODO: Invalid pre-state

				return;
			}

			ecListHolder.value[0].deactivate_component(obj);

			/**
			 * LifeCycleState state; do { try { Thread.sleep(10); } catch
			 * (Exception ex) { ex.printStackTrace(); } state =
			 * ecListHolder.value[0].get_component_state(obj); } while
			 * (!state.equals(LifeCycleState.INACTIVE_STATE));
			 */
		} catch (org.omg.CORBA.COMM_FAILURE e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * <div lang="ja">
	 * �R���|�[�l���g�v���t�@�C������RTObject���������đ�����reset
	 * 
	 * @param component
	 * @throws Exception
	 *             </div> <div lang="en">
	 * 
	 * @param component
	 * @throws Exception
	 *             </div>
	 */
	static public void resetComponent(Component component) throws Exception {
		try {
			logger.info("resetComponent:"
					+ component.get(Component.INSTANCE_NAME));
			RTObject obj = getComponent(component);

			ExecutionContextListHolder ecListHolder = new ExecutionContextListHolder();
			ecListHolder.value = new RTC.ExecutionContext[1];
			ecListHolder.value = obj.get_owned_contexts();
			LifeCycleState currentState = ecListHolder.value[0]
					.get_component_state(obj);
			if (!currentState.equals(LifeCycleState.ERROR_STATE)) {

				// TODO: Invalid pre-state

				return;
			}

			ecListHolder.value[0].reset_component(obj);
			/**
			 * LifeCycleState state; do { try { Thread.sleep(10); } catch
			 * (Exception ex) { ex.printStackTrace(); } state =
			 * ecListHolder.value[0].get_component_state(obj); } while
			 * (!state.equals(LifeCycleState.INACTIVE_STATE));
			 */
		} catch (org.omg.CORBA.COMM_FAILURE e) {
			e.printStackTrace();
		}
	}

	static public String getDataType(Component component,
			DataPort dataPort) throws Exception {
		if(dataPort.getDataType() != null) {
			return dataPort.getDataType();
		}
		
		RTObject rtObject = RTSystemBuilder.getComponent(component);
		PortService port = getPortService(component,
				dataPort.get(DataPort.RTS_NAME));
		if (port != null) {
			for (NameValue nv : port.get_port_profile().properties) {
				if (nv.name.equals("dataport.data_type")) {
					return nv.value.extract_string();
				}
			}
		}
		return null;
	}

	static public boolean isProvider(Component component,
			DataPort dataPort) throws Exception {
		PortService port = getPortService(component,
				dataPort.get(DataPort.RTS_NAME));
		if (port != null) {

			for (PortInterfaceProfile prof : port.get_port_profile().interfaces) {
				return prof.polarity.value() == 0;
			}
		}
		return false;
	}

	static public boolean isConsumer(Component component,
			DataPort dataPort) throws Exception {
		PortService port = getPortService(component,
				dataPort.get(DataPort.RTS_NAME));
		if (port != null) {

			for (PortInterfaceProfile prof : port.get_port_profile().interfaces) {
				return prof.polarity.value() == 1;
			}
		}
		return false;
	}

	static public List<String> getInterfaceNameList(Component component,
			DataPort dataPort) throws Exception {
		ArrayList<String> inList = new ArrayList<String>();
		if(dataPort.getInterfaceList() != null) {
			for(Interface intf : dataPort.getInterfaceList()) {
				inList.add(intf.getName());
			}
			return inList;
		}
		PortService port = getPortService(component,
				dataPort.get(DataPort.RTS_NAME));
		if (port != null) {
			for (PortInterfaceProfile prof : port.get_port_profile().interfaces) {
				inList.add(prof.type_name);
			}
		}
		return inList;
	}

	static public boolean isConnectable(Component sourceComponent,
			DataPort sourceDataPort, Component targetComponent,
			DataPort targetDataPort) throws Exception {
		boolean connectable = false;
		String dataType = RTSystemBuilder.getDataType(sourceComponent,
				sourceDataPort);
		String targetDataType = RTSystemBuilder.getDataType(targetComponent,
				targetDataPort);

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
			Component sourceComponent, DataPort sourceDataPort,
			Component targetComponent, DataPort targetDataPort)
			throws Exception {
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
}