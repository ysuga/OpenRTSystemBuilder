package net.ysuga.rtsbuilder;

import java.util.HashMap;
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
import net.ysuga.rtsystem.profile.Connector;
import net.ysuga.rtsystem.profile.ExecutionContext;
import net.ysuga.rtsystem.profile.Location;
import net.ysuga.rtsystem.profile.Properties;
import net.ysuga.rtsystem.profile.RTSystemProfile;

import org.omg.CORBA.AnyHolder;

import com.sun.xml.internal.bind.v2.util.TypeCast;

import OpenRTM.DataFlowComponent;
import RTC.ComponentProfile;
import RTC.ConnectorProfile;
import RTC.ConnectorProfileHolder;
import RTC.ExecutionContextListHolder;
import RTC.LifeCycleState;
import RTC.PortService;
import RTC.RTObject;
import _SDOPackage.NVListHolder;
import _SDOPackage.NameValue;

public class RTSystemBuilder {
	static private Logger logger;
	
	static private Map<String, CorbaNaming> corbaNamingMap;

	static {
		logger = Logger.getLogger("net.ysuga.rtsbuilder");
		corbaNamingMap = new HashMap<String, CorbaNaming>();
	}
	
	/**
	 * Constructor
	 * 
	 * @param rtSystemProfile
	 */
	public RTSystemBuilder() {
	}	

	static public boolean searchRTCs(RTSystemProfile rtSystemProfile) {
		boolean ret = false;
		for(Component component: rtSystemProfile.componentSet) {
			try {
				findComponent(component);
			} catch (Exception e) {
				ret = true;
			}
		}
		return ret;
	}
		
	/**
	 * 
	 */
	static public void buildRTSystem(RTSystemProfile rtSystemProfile) {
		logger.info("buildRTSystem:" + rtSystemProfile.get(RTSystemProfile.ID));
		for(Component component: rtSystemProfile.componentSet) {
			try {
				configureComponent(component);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		for(Connector connector : rtSystemProfile.connectorSet) {
			try {
				connect(connector);
		 	} catch (Exception e) {
		 		e.printStackTrace();
		 	}
		}
	}

	/**
	 * @param component
	 * @throws Exception 
	 */
	static public void configureComponent(Component component) throws Exception {
		logger.info("configureComponent:" + component.get(Component.INSTANCE_NAME));
		RTObject rtObject = findComponent(component);
		
		_SDOPackage.Configuration sdoConfiguration = rtObject.get_configuration();
		for(ConfigurationSet configurationSet : component.configurationSetSet) {
			_SDOPackage.ConfigurationSet sdoConfigurationSet = sdoConfiguration.get_configuration_set(configurationSet.get(ConfigurationSet.ID));
			NVListHolder nvListHolder = new NVListHolder();
			nvListHolder.value = new NameValue[0];
			for(ConfigurationData configurationData : configurationSet.configurationDataSet) {
				CORBA_SeqUtil.push_back(nvListHolder, NVUtil.newNVString(
						configurationData.get(ConfigurationData.NAME),
						configurationData.get(ConfigurationData.DATA)));
			}
			sdoConfigurationSet.configuration_data = nvListHolder.value;
			sdoConfiguration.add_configuration_set(sdoConfigurationSet);
			if(component.get(Component.ACTIVE_CONFIGURATION_SET).equals(sdoConfigurationSet.id)) {
				sdoConfiguration.activate_configuration_set(sdoConfigurationSet.id);
			}
		}
	}


	static public void destroyRTSystem(RTSystemProfile rtSystemProfile) {
		logger.info("destroyRTSystem:" + rtSystemProfile.get(RTSystemProfile.ID));
		for(Connector connector : rtSystemProfile.connectorSet) {
			try {
				disconnect(connector);
		 	} catch (Exception e) {
		 		e.printStackTrace();
		 	}
		}
	}

	static public void clearAllConnection() {

	}

	static public void disconnect(Connector connector) throws Exception {
		logger.info("disconnect:" + connector.getSourceComponentInstanceName() + connector.getSourceDataPortName() + "->" 
				+ connector.getTargetComponentInstanceName() +  connector.getTargetDataPortName());
		RTObject sourceRTObject = findComponent(connector.getSourceComponentPathUri());
		for(PortService portService : sourceRTObject.get_ports()) {
			if(portService.get_port_profile().name.equals(connector.getSourceDataPortName())) {
				 portService.disconnect(connector.get(Connector.CONNECTOR_ID));
			}
		}
	}
	
	
	/**
	 * 
	 * @param connector
	 * @throws Exception
	 */
	static public void connect(Connector connector) throws Exception {
		logger.info("connect:" + connector.getSourceComponentInstanceName() + connector.getSourceDataPortName() + "->" 
				+ connector.getTargetComponentInstanceName() +  connector.getTargetDataPortName());
		
		RTObject sourceRTObject = findComponent(connector.getSourceComponentPathUri());
		RTObject targetRTObject = findComponent(connector.getTargetComponentPathUri());

		// Building Connector Profile
		ConnectorProfile prof = new ConnectorProfile();
		prof.connector_id = connector.get(Connector.CONNECTOR_ID);
		prof.name = connector.get(Connector.NAME);
		prof.ports = new PortService[2];

		for(PortService portService : sourceRTObject.get_ports()) {
			if(portService.get_port_profile().name.equals(connector.getSourceDataPortName())) {
				prof.ports[1] = portService;
			}
		}
		for(PortService portService : targetRTObject.get_ports()) {
			if(portService.get_port_profile().name.equals(connector.getTargetDataPortName())) {
				prof.ports[0] = portService;
			}
		}
		if(prof.ports[0] == null || prof.ports[1] == null) {
			throw new Exception("Invalid RTS Profile");
		}
		
		NVListHolder nvholder = new NVListHolder();
		nvholder.value = prof.properties;
		if (nvholder.value == null)
			nvholder.value = new NameValue[0];
		CORBA_SeqUtil.push_back(nvholder, NVUtil.newNVString(
				"dataport.interface_type", connector.get(Connector.INTERFACE_TYPE)));
		CORBA_SeqUtil.push_back(nvholder, NVUtil.newNVString(
				"dataport.dataflow_type", connector.get(Connector.DATAFLOW_TYPE)));
		CORBA_SeqUtil.push_back(nvholder, NVUtil.newNVString(
				"dataport.subscription_type", connector.get(Connector.SUBSCRIPTION_TYPE)));
		prof.properties = nvholder.value;

		ConnectorProfileHolder proflist = new ConnectorProfileHolder();
		proflist.value = prof;

		if( prof.ports[0].connect(proflist) != RTC.ReturnCode_t.RTC_OK) {
			throw new Exception ("Cannot Connect");
		}
	}
	
	/**
	 * 
	 * @param rtSystemProfile
	 * @throws Exception
	 */
	static public void activateRTCs(RTSystemProfile rtSystemProfile) throws Exception {
		logger.info("activateRTSystem:" + rtSystemProfile.get(RTSystemProfile.ID));
		for(Component component: rtSystemProfile.componentSet) {
			activateComponent(component);
		}
	}

	/**
	 * @throws Exception 
	 * 
	 */
	static public void deactivateRTCs(RTSystemProfile rtSystemProfile) throws Exception {
		logger.info("deactivateRTSystem:" + rtSystemProfile.get(RTSystemProfile.ID));
		for(Component component: rtSystemProfile.componentSet) {
			deactivateComponent(component);
		}
	}

	/**
	 * @throws Exception 
	 * 
	 */
	static public void resetRTCs(RTSystemProfile rtSystemProfile) throws Exception {
		logger.info("resetRTSystem:" + rtSystemProfile.get(RTSystemProfile.ID));
		for(Component component: rtSystemProfile.componentSet) {
			resetComponent(component);
		}
	}
	
	static public String buildComponentId(RTC.RTObject rtObject) {
		ComponentProfile profile;
		profile = rtObject.get_component_profile();
		return "RTC:" + profile.vendor + ":" + profile.category + ":" 
		 + profile.type_name + ":" + profile.version;
	}

	/**
	 * 
	 * @param pathUri
	 * @return
	 * @throws Exception
	 */
	static public Component createComponent(String pathUri) throws Exception {
		RTObject rtObject = findComponent(pathUri);
		ComponentProfile profile;
		try {
			// TODO: Ç«Ç§Ç‚Ç¡ÇΩÇÁRTObjectÇÃóLå¯ê´Çà¿ëSÇ…åüèÿÇ≈Ç´ÇÈÇÃÇ©ÇÌÇ©ÇËÇ‹ÇπÇÒÅD
			profile = rtObject.get_component_profile();
		} catch (Exception ex) {
			return null;
		}
		
		
		Component component =  new Component(profile.instance_name, 
				pathUri,
				buildComponentId(rtObject),
				rtObject.get_configuration().get_active_configuration_set().id,
				false,
				"None");
		
		PortService[] portServices = rtObject.get_ports();
		for(PortService portService : portServices) {
			String name = portService.get_port_profile().name;
			/*
			NameValue[] nvs = portService.get_port_profile().properties;
			String dataType = "";
			for(NameValue nv : nvs) {
				if(nv.name.equals("dataport.data_type")) {
					dataType = nv.value.extract_wstring();
				}
			}
			*/
			Component.DataPort dataPort = component.new DataPort(name);
			component.dataPortSet.add(dataPort);
		}
		
		
		// Adding Configuration Sets
		_SDOPackage.Configuration configuration = rtObject.get_configuration();
		_SDOPackage.ConfigurationSet[] configurationSets = configuration.get_configuration_sets();
		for(_SDOPackage.ConfigurationSet configurationSet : configurationSets) {
			ConfigurationSet myConfigurationSet = new ConfigurationSet(configurationSet.id);
			for(NameValue configurationData : configurationSet.configuration_data) {
				myConfigurationSet.configurationDataSet.add(
						new ConfigurationData(configurationData.name, configurationData.value.toString()));
			}
			component.configurationSetSet.add(myConfigurationSet);
		}


		// Adding Execution Context
		RTC.ExecutionContext[] executionContexts = rtObject.get_owned_contexts();
		for(int i = 0;i < executionContexts.length;i++) {
			RTC.ExecutionContext executionContext = executionContexts[i];
			ExecutionContext myEc = new ExecutionContext(Integer.toString(i),
					"rtsExt:execution_context_ext",
					executionContext.get_kind().toString(),
					Double.toString(executionContext.get_rate()));
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
	 * @param connectorProfile
	 * @return
	 * @throws Exception 
	 */
	static protected Connector createConnector(Set<Component> componentSet, ConnectorProfile connectorProfile) throws Exception {
		String connectorId, name, dataType = "";
		String interfaceType = "", dataflowType = "", subscriptionType = "";
		connectorId = connectorProfile.connector_id;
		name = connectorProfile.name;
		for(_SDOPackage.NameValue nameValue : connectorProfile.properties) {
			if(nameValue.name.equals("dataport.data_type")) {
				dataType = nameValue.value.extract_string();
			} else if(nameValue.name.equals("dataport.dataflow_type")) {
				dataflowType = nameValue.value.extract_string();
			} else if(nameValue.name.equals("dataport.subscription_type")) {
				subscriptionType = nameValue.value.extract_string();
			} else if(nameValue.name.equals("dataport.interface_type")) {
				interfaceType = nameValue.value.extract_string();
			}
		}
		
		Connector connector = new Connector(connectorId, name, 
				dataType, interfaceType, dataflowType, subscriptionType);
	
		String sourcePathUri = null, targetPathUri = null;
		RTObject sourceRTC = connectorProfile.ports[0].get_port_profile().owner;
		RTObject targetRTC = connectorProfile.ports[1].get_port_profile().owner;
		for(Component component : componentSet) {
			if(findComponent(component).equals(sourceRTC)) {
				sourcePathUri = component.get(Component.PATH_URI);
			}
		}
		for(Component component : componentSet) {
			if(findComponent(component).equals(targetRTC)) {
				targetPathUri = component.get(Component.PATH_URI);
			}
		
		}
		if(sourcePathUri == null || targetPathUri == null) {
			throw new Exception();
		}
		
		connector.sourceDataPort = connector.new DataPort(
				connectorProfile.ports[0].get_port_profile().name,
				connectorProfile.ports[0].get_port_profile().owner.get_component_profile().instance_name,
				RTSystemBuilder.buildComponentId(connectorProfile.ports[0].get_port_profile().owner));
		connector.sourceDataPort.properties = new Properties("COMPONENT_PATH_ID", sourcePathUri);

		connector.targetDataPort = connector.new DataPort(
				connectorProfile.ports[1].get_port_profile().name,
				connectorProfile.ports[1].get_port_profile().owner.get_component_profile().instance_name,
				RTSystemBuilder.buildComponentId(connectorProfile.ports[1].get_port_profile().owner));					
		connector.targetDataPort.properties = new Properties("COMPONENT_PATH_ID", targetPathUri);
		
	
		return connector;
	}
	
	static public RTC.RTObject findComponent(String pathUri) throws Exception {
		logger.info("findComponent:" + pathUri);
		StringTokenizer tokenizer = new StringTokenizer(pathUri, "/");
		String namingURI = tokenizer.nextToken();
		String compURI = pathUri.substring(
				namingURI.length() + 1);
		StringTokenizer tokenizer2 = new StringTokenizer(namingURI, ":");
		if (tokenizer2.countTokens() == 1) {
			namingURI = namingURI + ":2809";
		}

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
		return corbaConsumer._ptr();
	}
	
	
	/**
	 * 
	 * @param component
	 * @return
	 * @throws Exception
	 */
	static public RTC.RTObject findComponent(Component component) throws Exception {
		return findComponent(component.get(Component.PATH_URI));
	}

	/**
	 * 
	 * @param obj
	 * @throws Exception
	 */
	static public void activateComponent(Component component) throws Exception {
		try {
			logger.info("activateComponent:" + component.get(Component.INSTANCE_NAME));
			RTObject obj = findComponent(component);

			ExecutionContextListHolder ecListHolder = new ExecutionContextListHolder();
			ecListHolder.value = new RTC.ExecutionContext[1];
			ecListHolder.value = obj.get_owned_contexts();
			
			LifeCycleState currentState = ecListHolder.value[0].get_component_state(obj);
			if(!currentState.equals(LifeCycleState.INACTIVE_STATE)) {
				
				// TODO: Invalid pre-state
				
				return ;
			}
			
			ecListHolder.value[0].activate_component(obj);
			LifeCycleState state;
			do {
				try {
					Thread.sleep(10);
				} catch (Exception ex) {
					ex.printStackTrace();
				}
				state = ecListHolder.value[0].get_component_state(obj);
			} while (!state.equals(LifeCycleState.ACTIVE_STATE));
		} catch (org.omg.CORBA.COMM_FAILURE e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @param obj
	 * @throws Exception 
	 */
	static public void deactivateComponent(Component component) throws Exception {
		try {
			logger.info("deactivateComponent:" + component.get(Component.INSTANCE_NAME));
			RTObject obj = findComponent(component);
			
			ExecutionContextListHolder ecListHolder = new ExecutionContextListHolder();
			ecListHolder.value = new RTC.ExecutionContext[1];
			ecListHolder.value = obj.get_owned_contexts();
			
			LifeCycleState currentState = ecListHolder.value[0].get_component_state(obj);
			if(!currentState.equals(LifeCycleState.ACTIVE_STATE)) {
				
				// TODO: Invalid pre-state
				
				return ;
			}
			
			ecListHolder.value[0].deactivate_component(obj);
			
			LifeCycleState state;
			do {
				try {
					Thread.sleep(10);
				} catch (Exception ex) {
					ex.printStackTrace();
				}
				state = ecListHolder.value[0].get_component_state(obj);
			} while (!state.equals(LifeCycleState.INACTIVE_STATE));
		} catch (org.omg.CORBA.COMM_FAILURE e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @param obj
	 * @throws Exception 
	 */
	static public void resetComponent(Component component) throws Exception {
		try {
			logger.info("resetComponent:" + component.get(Component.INSTANCE_NAME));
			RTObject obj = findComponent(component);

			ExecutionContextListHolder ecListHolder = new ExecutionContextListHolder();
			ecListHolder.value = new RTC.ExecutionContext[1];
			ecListHolder.value = obj.get_owned_contexts();
			LifeCycleState currentState = ecListHolder.value[0].get_component_state(obj);
			if(!currentState.equals(LifeCycleState.ERROR_STATE)) {
				
				// TODO: Invalid pre-state
				
				return ;
			}
			
			
			ecListHolder.value[0].reset_component(obj);
			LifeCycleState state;
			do {
				try {
					Thread.sleep(10);
				} catch (Exception ex) {
					ex.printStackTrace();
				}
				state = ecListHolder.value[0].get_component_state(obj);
			} while (!state.equals(LifeCycleState.INACTIVE_STATE));
		} catch (org.omg.CORBA.COMM_FAILURE e) {
			e.printStackTrace();
		}
	}

}
