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
	
}
