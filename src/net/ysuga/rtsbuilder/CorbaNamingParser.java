/**
 * CorbaNamingParser.java
 *
 * @author Yuki Suga (ysuga.net)
 * @date 2011/07/02
 * @copyright 2011, ysuga.net allrights reserved.
 *
 */
package net.ysuga.rtsbuilder;

import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;

import jp.go.aist.rtm.RTC.CorbaNaming;
import jp.go.aist.rtm.RTC.util.ORBUtil;
import net.ysuga.rtsystem.profile.Component;
import net.ysuga.rtsystem.profile.Connector;

import org.omg.CosNaming.Binding;
import org.omg.CosNaming.BindingIteratorHolder;
import org.omg.CosNaming.BindingListHolder;
import org.omg.CosNaming.BindingType;
import org.omg.CosNaming.NamingContext;

import RTC.RTObject;

/**
 * @author ysuga
 *
 */
public class CorbaNamingParser {

	String hostAddress;
	
	/**
	 * 
	 * @param hostAddress
	 */
	public CorbaNamingParser(String hostAddress) {
		this.hostAddress = hostAddress;
	}
	
	/**
	 * 
	 * @return
	 * @throws Exception
	 */
	public Set<String> getRTObjectPathUriSet() throws Exception {
		Set<String> pathUriSet = new HashSet<String>();
		BindingListHolder bl = new BindingListHolder();
		BindingIteratorHolder bi = new BindingIteratorHolder();

		String namingUri = hostAddress;
		StringTokenizer tokenizer2 = new StringTokenizer(namingUri, ":");
		if (tokenizer2.countTokens() == 1) {
			namingUri = namingUri + ":2809";
		}		
		CorbaNaming corbaNaming = new CorbaNaming(ORBUtil.getOrb(), namingUri);
		
		NamingContext namingContext = corbaNaming.getRootContext();
		namingContext.list(30, bl, bi);
		for(Binding binding : bl.value) {
			String bindingName = binding.binding_name[0].id + "." + binding.binding_name[0].kind;

			if(binding.binding_type == BindingType.ncontext) {
				Set<String> childSet = getRTObjectPathUriSetSub((NamingContext)namingContext.resolve(binding.binding_name));
				for(String childPathUri : childSet) {
					pathUriSet.add(hostAddress + "/" + bindingName + "/" + childPathUri);
				}
			} else {
				pathUriSet.add(hostAddress + "/" + bindingName);
			}
		}
		return pathUriSet;
	}
	
	/**
	 * 
	 * @param namingContext
	 * @return
	 * @throws Exception
	 */
	protected Set<String> getRTObjectPathUriSetSub(NamingContext namingContext) throws Exception {
		Set<String> pathUriSet = new HashSet<String>();
		BindingListHolder bl = new BindingListHolder();
		BindingIteratorHolder bi = new BindingIteratorHolder();
		namingContext.list(30, bl, bi);
		
		for(Binding binding : bl.value) {
			String bindingName = binding.binding_name[0].id + "." + binding.binding_name[0].kind;

			if(binding.binding_type == BindingType.ncontext) {
				Set<String> childSet = getRTObjectPathUriSetSub((NamingContext)namingContext.resolve(binding.binding_name));
				for(String childPathUri : childSet) {
					pathUriSet.add(bindingName + "/" + childPathUri);
				}
			} else {
				pathUriSet.add(bindingName);
			}
		}
		return pathUriSet;
	}
	
	/**
	 * 
	 * @return
	 * @throws Exception
	 */
	public Set<Component> getRegisteredComponentSet() throws Exception {
		Set<Component> componentSet = new HashSet<Component>();
		Set<String> componentUriSet = getRTObjectPathUriSet();

		String namingUri = hostAddress;
		StringTokenizer tokenizer2 = new StringTokenizer(namingUri, ":");
		if (tokenizer2.countTokens() == 1) {
			namingUri = namingUri + ":2809";
		}		
		
		for(String uri : componentUriSet) {
			Component component = RTSystemBuilder.createComponent(uri);
			if(component != null) {
				componentSet.add(component);
			}
		}
		
		return componentSet;
	}
	
	/**
	 * éQâ¡ÇµÇƒÇ¢ÇÈRTCä‘ÇÃê⁄ë±ÇÃÇ›ÇèoóÕ
	 * 
	 * @param componentSet
	 * @return
	 * @throws Exception
	 */
	public Set<Connector> getConnectorSet(Set<Component> componentSet) throws Exception {
		Set<Connector> connectorSet = new HashSet<Connector>();
		
		for(Component component : componentSet) {
			RTObject sourceRTObject = RTSystemBuilder.findComponent(component);
			
			RTC.PortService[] portServices = sourceRTObject.get_ports();
			for(RTC.PortService portService : portServices) {
				RTC.ConnectorProfile[] connectorProfiles = portService.get_connector_profiles();
				for(RTC.ConnectorProfile connectorProfile : connectorProfiles) {
					connectorSet.add(RTSystemBuilder.createConnector(componentSet, connectorProfile));
				}
			}
		}
		return connectorSet;
	}
	
}
