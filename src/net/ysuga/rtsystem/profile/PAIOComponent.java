/**
 * PAIOComponent.java
 *
 * @author Yuki Suga (ysuga.net)
 * @date 2011/10/07
 * @copyright 2011, ysuga.net allrights reserved.
 *
 */
package net.ysuga.rtsystem.profile;

import java.io.IOException;

import org.w3c.dom.Node;

/**
 * 
 * @author ysuga
 * 
 */
public class PAIOComponent extends Component {

	/**
	 * Constructor
	 * 
	 * @throws IOException
	 */
	public PAIOComponent() throws IOException {
		super();
	}

	/**
	 * Constructor
	 * 
	 * @param instanceName
	 * @param pathUri
	 * @param Id
	 * @param activeConfigurationSet
	 * @param required
	 * @param compositeType
	 * @throws IOException
	 */
	public PAIOComponent(String instanceName, String pathUri, String Id)
			throws IOException {
		super(instanceName, pathUri, Id, "default", true, "None");
		this.put("PAIO", "true");
	}

	/**
	 * Constructor
	 * 
	 * @param node
	 * @throws IOException
	 */
	public PAIOComponent(Node node) throws IOException {
		super(node);
	}

}
