package test;
import java.util.Set;

import net.ysuga.rtsbuilder.CorbaNamingParser;
import net.ysuga.rtsystem.profile.Component;

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
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

}
