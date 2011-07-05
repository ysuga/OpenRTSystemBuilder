package test;
import java.io.File;
import java.util.Set;

import net.ysuga.rtsbuilder.CorbaNamingParser;
import net.ysuga.rtsystem.profile.Component;
import net.ysuga.rtsystem.profile.Connector;
import net.ysuga.rtsystem.profile.RTSystemProfile;

/**
 * CorbaNamingParserTest.java
 *
 * @author Yuki Suga (ysuga.net)
 * @date 2011/07/03
 * @copyright 2011, ysuga.net allrights reserved.
 *
 */

/**
 * @author ysuga
 *
 */
public class CorbaNamingParserTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			CorbaNamingParser parser = new CorbaNamingParser("127.0.0.1");
			Set<String> set = parser.getRTObjectPathUriSet();
			System.out.println("Set = " + set);
			
			Set<Component> cset = parser.getRegisteredComponentSet();
			System.out.println("Comp = " + cset);
			
			Set<Connector> conset = parser.getConnectorSet(cset);
			System.out.println("Comp[" + conset.size() + "] = " + conset);
			
			RTSystemProfile myProfile = new RTSystemProfile("TestSystem", "ysuga_net", "0.1");
			myProfile.componentSet.addAll(cset);
			myProfile.connectorSet.addAll(conset);
			File testFile = new File("TestSystem.xml");
			myProfile.save(testFile);

			
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		System.exit(0);
	}

}
