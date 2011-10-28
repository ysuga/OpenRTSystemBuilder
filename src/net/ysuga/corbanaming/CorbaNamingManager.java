/**
 * CorbaNamingManager.java
 *
 * @author Yuki Suga (ysuga.net)
 * @date 2011/10/26
 * @copyright 2011, ysuga.net allrights reserved.
 *
 */
package net.ysuga.corbanaming;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import jp.go.aist.rtm.RTC.CorbaNaming;
import jp.go.aist.rtm.RTC.util.ORBUtil;
import net.ysuga.rtsystem.profile.RTComponent;

/**
 * CorbaNaming object Manager.
 * 
 * get method provides CorbaNaming access by using URI. The binded CorbaNaming
 * object will be automatically stored in this class's static object. The next
 * call will be much faster.
 * 
 * @author ysuga
 * 
 */
public class CorbaNamingManager {
	private static Map<String, CorbaNaming> corbaNamingMap;
	static {
		corbaNamingMap = new HashMap<String, CorbaNaming>();
	}

	/**
	 * Get Binded NameService's Addresses
	 * 
	 * @return Set of NamingService URI
	 */
	public static Set<String> getAddressSet() {
		return corbaNamingMap.keySet();
	}

	/**
	 * Get Binded NameService. The NameService will be automatically binded to
	 * this class static object. The next request will be faster.
	 * 
	 * @param address
	 *            Naming URI
	 * @return CorbaNaming Object.
	 * @throws CorbaNamingCannotFindException
	 */
	public static CorbaNaming get(String namingUri)
			throws CorbaNamingCannotFindException {
		try {
			StringTokenizer tokenizer2 = new StringTokenizer(namingUri, ":");
			if (tokenizer2.countTokens() == 1) {
				namingUri = namingUri + ":2809";
			}
			
			CorbaNaming cn = corbaNamingMap.get(namingUri);
			if (cn == null || !cn.isAlive()) {
				
				cn = new CorbaNaming(ORBUtil.getOrb(), namingUri);
				corbaNamingMap.put(namingUri, cn);
			}
			return cn;
		} catch (Exception e) {
			e.printStackTrace();
			throw new CorbaNamingCannotFindException();
		}
	}

	public static boolean find(String namingContext) {
		for (String address : getAddressSet()) {
			try {
				Set<RTComponent> compSet = CorbaNamingParser.getRegisteredComponentSet(address);
				for(RTComponent comp:compSet) {
					if(comp.get(RTComponent.PATH_URI).equals(namingContext)) {
						return true;
					}
				}
			} catch (Exception e) {

			}
		}
		return false;
	}

}
