/**
 * CorbaNamingParser.java
 *
 * @author Yuki Suga (ysuga.net)
 * @date 2011/07/02
 * @copyright 2011, ysuga.net allrights reserved.
 *
 */
package net.ysuga.corbanaming;

import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;

import jp.go.aist.rtm.RTC.CorbaNaming;
import jp.go.aist.rtm.RTC.util.ORBUtil;
import net.ysuga.rtsbuilder.RTSystemBuilder;
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

	/**
	 * 
	 * @return
	 * @throws Exception
	 */
	public static Set<String> getRTObjectPathUriSet(String hostAddress)
			throws Exception {
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
		for (Binding binding : bl.value) {
			String bindingName = binding.binding_name[0].id + "."
					+ binding.binding_name[0].kind;

			if (binding.binding_type == BindingType.ncontext) {
				Set<String> childSet = getRTObjectPathUriSetSub((NamingContext) namingContext
						.resolve(binding.binding_name));
				for (String childPathUri : childSet) {
					pathUriSet.add(hostAddress + "/" + bindingName + "/"
							+ childPathUri);
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
	protected static Set<String> getRTObjectPathUriSetSub(
			NamingContext namingContext) throws Exception {
		Set<String> pathUriSet = new HashSet<String>();
		BindingListHolder bl = new BindingListHolder();
		BindingIteratorHolder bi = new BindingIteratorHolder();
		namingContext.list(30, bl, bi);

		for (Binding binding : bl.value) {
			String bindingName = binding.binding_name[0].id + "."
					+ binding.binding_name[0].kind;

			if (binding.binding_type == BindingType.ncontext) {
				Set<String> childSet = getRTObjectPathUriSetSub((NamingContext) namingContext
						.resolve(binding.binding_name));
				for (String childPathUri : childSet) {
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
	public static Set<Component> getRegisteredComponentSet(String hostAddress)
			throws Exception {
		Set<Component> componentSet = new HashSet<Component>();
		Set<String> componentUriSet = getRTObjectPathUriSet(hostAddress);

		String namingUri = hostAddress;
		StringTokenizer tokenizer2 = new StringTokenizer(namingUri, ":");
		if (tokenizer2.countTokens() == 1) {
			namingUri = namingUri + ":2809";
		}

		for (String uri : componentUriSet) {
			Component component = RTSystemBuilder.createComponent(uri);
			if (component != null) {
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
	public static Set<Connector> getConnectorSet(Set<Component> componentSet)
			throws Exception {
		Set<Connector> connectorSet = new HashSet<Connector>();

		for (Component component : componentSet) {
			RTObject sourceRTObject = RTSystemBuilder.getComponent(component);

			RTC.PortService[] portServices = sourceRTObject.get_ports();
			for (RTC.PortService portService : portServices) {
				RTC.ConnectorProfile[] connectorProfiles = portService
						.get_connector_profiles();
				for (RTC.ConnectorProfile connectorProfile : connectorProfiles) {
					connectorSet.add(RTSystemBuilder.createConnector(
							componentSet, connectorProfile));
				}
			}
		}
		return connectorSet;
	}

	/**
	 * 
	 * buildRTNamingContext <div lang="ja">
	 * 
	 * @param binding
	 * @param namingContext
	 * @return
	 * @throws Exception
	 *             </div> <div lang="en">
	 * 
	 * @param binding
	 * @param namingContext
	 * @return
	 * @throws Exception
	 *             </div>
	 */
	protected static RTNamingContext buildRTNamingContext(Binding binding,
			NamingContext namingContext) throws Exception {
		RTNamingContext rtNamingContext = new RTNamingContext(
				binding.binding_name[0].id, binding.binding_name[0].kind);

		
		BindingListHolder bl = new BindingListHolder();
		BindingIteratorHolder bi = new BindingIteratorHolder();
		namingContext.list(30, bl, bi);
		for (Binding childBinding : bl.value) {
			if (childBinding.binding_type == BindingType.ncontext) {

				rtNamingContext.add(buildRTNamingContext(childBinding,
						(NamingContext) namingContext
								.resolve(childBinding.binding_name)));
			} else {

				rtNamingContext.add(new RTNamingContext(
						childBinding.binding_name[0].id,
						childBinding.binding_name[0].kind));
			}
		}
		return rtNamingContext;
	}

	/**
	 * 
	 * buildRTNamingContext <div lang="ja">
	 * 
	 * @param nameServerUri
	 * @return
	 * @throws CorbaNamingConnectionException
	 *             </div> <div lang="en">
	 * 
	 * @param nameServerUri
	 * @return
	 * @throws CorbaNamingConnectionException
	 *             </div>
	 */
	public static RTNamingContext buildRTNamingContext(String nameServerUri)
			throws CorbaNamingConnectionException {
		RTNamingContext rootContext = new RTNamingContext(nameServerUri);

		StringTokenizer tokenizer2 = new StringTokenizer(nameServerUri, ":");
		if (tokenizer2.countTokens() == 1) {
			nameServerUri = nameServerUri + ":2809";
		}
		CorbaNaming corbaNaming;
		try {
			corbaNaming = new CorbaNaming(ORBUtil.getOrb(), nameServerUri);
		} catch (Exception e) {
			e.printStackTrace();
			throw new CorbaNamingConnectionException();
		}

		NamingContext namingContext = corbaNaming.getRootContext();
		BindingListHolder bl = new BindingListHolder();
		BindingIteratorHolder bi = new BindingIteratorHolder();
		namingContext.list(30, bl, bi);

		for (Binding binding : bl.value) {
			try {
				if (binding.binding_type == BindingType.ncontext) {
					rootContext.add(buildRTNamingContext(binding,
							(NamingContext) namingContext
									.resolve(binding.binding_name)));
				} else {
					rootContext.add(new RTNamingContext(
							binding.binding_name[0].id,
							binding.binding_name[0].kind));
				}
			} catch (Exception e) {
				e.printStackTrace();
				throw new CorbaNamingConnectionException();
			}
		}

		return rootContext;
	}
}
