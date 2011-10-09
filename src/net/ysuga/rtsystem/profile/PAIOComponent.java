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
import java.util.Iterator;
import java.util.StringTokenizer;

import net.ysuga.rtsbuilder.RTSystemBuilder;

import org.w3c.dom.Node;

/**
 * 
 * @author ysuga
 * 
 */
public class PAIOComponent extends Component {

	private String namingContext;
	private String onExecuteCode;
	private String onActivatedCode;
	private String onDeactivatedCode;

	/**
	 * Constructor
	 * 
	 * @throws IOException
	 */
	public PAIOComponent() throws IOException {
		super();
		onExecuteCode = "def onExecute(self, ec_id)\n\t\n\tpass\n";
		onActivatedCode = "def onActivated(self, ec_id)\n\t\n\tpass\n";
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
		onExecuteCode = "def onExecute(self, ec_id)\n\t\n\tpass\n";
		onActivatedCode = "def onActivated(self, ec_id)\n\t\n\tpass\n";
		onDeactivatedCode = "def onDeactivated(self, ec_id)\n\t\n\tpass\n";

	}

	/**
	 * Constructor
	 * 
	 * @param node
	 * @throws IOException
	 */
	public PAIOComponent(Node node) throws IOException {
		super(node);
		onExecuteCode = "def onExecute(self, ec_id)\n\t\n\tpass\n";
		onActivatedCode = "def onActivated(self, ec_id)\n\t\n\tpass\n";
	}

	/**
	 * getNamingContext
	 *
	 * @return
	 */
	public String getNamingContext() {
		// TODO 自動生成されたメソッド・スタブ
		return namingContext;
	}

	public void setNamingContext(String nc) {
		this.namingContext = nc;
	}
	
	
	/**
	 * getModuleName
	 *
	 * @return
	 */
	public String getModuleName() {
		String Id = get(Component.ID);
		StringTokenizer tokens = new StringTokenizer(Id, ":");
		String RTC = tokens.nextToken();
		String vendor = tokens.nextToken();
		String category = tokens.nextToken();
		String moduleName = tokens.nextToken();
		String version = tokens.nextToken();
		return moduleName;
	}

	/**
	 * getNameServer
	 *
	 * @return
	 */
	public String getNameServer() {
		String pathUri = get(Component.PATH_URI);
		return RTSystemBuilder.getNamingUri(pathUri);
	}

	/**
	 * getCategory
	 *
	 * @return
	 */
	public String getCategory() {
		String Id = get(Component.ID);
		StringTokenizer tokens = new StringTokenizer(Id, ":");
		String RTC = tokens.nextToken();
		String vendor = tokens.nextToken();
		String category = tokens.nextToken();
		String moduleName = tokens.nextToken();
		String version = tokens.nextToken();
		return category;
	}

	/**
	 * getExecutionRate
	 *
	 * @return
	 */
	public String getExecutionRate() {
		Iterator<ExecutionContext> i = executionContextSet.iterator();
		if(i.hasNext()) {
			ExecutionContext ec = (ExecutionContext)i.next();
			String rate = ec.get(ExecutionContext.RTS_RATE);
			return rate;
		}
		return "1000";
	}

	/**
	 * getVersion
	 *
	 * @return
	 */
	public String getVersion() {
		String Id = get(Component.ID);
		StringTokenizer tokens = new StringTokenizer(Id, ":");
		String RTC = tokens.nextToken();
		String vendor = tokens.nextToken();
		String category = tokens.nextToken();
		String moduleName = tokens.nextToken();
		String version = tokens.nextToken();
		return version;
	}

	/**
	 * getVendor
	 *
	 * @return
	 */
	public String getVendor() {
		String Id = get(Component.ID);
		StringTokenizer tokens = new StringTokenizer(Id, ":");
		String RTC = tokens.nextToken();
		String vendor = tokens.nextToken();
		String category = tokens.nextToken();
		String moduleName = tokens.nextToken();
		String version = tokens.nextToken();
		return vendor;
	}

	/**
	 * @return onExecuteCode
	 */
	public final String getOnExecuteCode() {
		return onExecuteCode;
	}

	/**
	 * @param onExecuteCode set onExecuteCode
	 */
	public final void setOnExecuteCode(String onExecuteCode) {
		this.onExecuteCode = onExecuteCode;
	}

	/**
	 * @return onActivatedCode
	 */
	public final String getOnActivatedCode() {
		return onActivatedCode;
	}

	/**
	 * @param onActivatedCode set onActivatedCode
	 */
	public final void setOnActivatedCode(String onActivatedCode) {
		this.onActivatedCode = onActivatedCode;
	}

	/**
	 * @return onDeactivatedCode
	 */
	public final String getOnDeactivatedCode() {
		return onDeactivatedCode;
	}

	/**
	 * @param onDeactivatedCode set onDectivatedCode
	 */
	public final void setOnDeactivatedCode(String onDectivatedCode) {
		this.onDeactivatedCode = onDectivatedCode;
	}

}
