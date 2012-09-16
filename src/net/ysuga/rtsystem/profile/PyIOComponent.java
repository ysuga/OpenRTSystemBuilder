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
public class PyIOComponent extends RTComponent {

	public static String indent = "  ";

	/**
	 * 
	 */
	private static final String ON_DEACTIVATED_DEFAULT_CODE = "def onDeactivated(self, ec_id):\n" +
			indent + "#Here You can add your own cleanup code.\n" +
			indent + "\n" +
			indent + "return RTC.RTC_OK\n";

	/**
	 * 
	 */
	private static final String ON_ACTIVATED_DEFAULT_CODE = "def onActivated(self, ec_id):\n" +
			indent + "#Here You can add your own initialization code.\n" +
			indent + "\n" +
			indent + "return RTC.RTC_OK\n";

	/**
	 * 
	 */
	private static final String ON_EXECUTE_DEFAULT_CODE = "def onExecute(self, ec_id):\n" +
			indent + "#Here you can add your routine.\n" +
			indent + "#This function is automatically called Periodically.\n" +
			indent + "\n"+
			indent + "return RTC.RTC_OK\n";

	/**
	 * 
	 */
	public static final String PYIO = "PyIO";
	
	private String namingContext;
	private String onExecuteCode;
	private String onActivatedCode;
	private String onDeactivatedCode;

	/**
	 * Constructor
	 * 
	 * @throws IOException
	 */
	public PyIOComponent() throws IOException {
		super();
		onExecuteCode = ON_EXECUTE_DEFAULT_CODE;
		onActivatedCode = ON_ACTIVATED_DEFAULT_CODE;
		onDeactivatedCode = ON_DEACTIVATED_DEFAULT_CODE;
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
	public PyIOComponent(String instanceName, String pathUri, String Id)
			throws IOException {
		super(instanceName, pathUri, Id, "default", true, "None");
		this.put(PYIO, "true");
		onExecuteCode = ON_EXECUTE_DEFAULT_CODE;
		onActivatedCode = ON_ACTIVATED_DEFAULT_CODE;
		onDeactivatedCode = ON_DEACTIVATED_DEFAULT_CODE;

	}

	/**
	 * Constructor
	 * 
	 * @param node
	 * @throws IOException
	 */
	public PyIOComponent(Node node) throws IOException {
		super(node);
		onExecuteCode = ON_EXECUTE_DEFAULT_CODE;
		onActivatedCode = ON_ACTIVATED_DEFAULT_CODE;
		onDeactivatedCode = ON_DEACTIVATED_DEFAULT_CODE;
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
		String Id = get(RTComponent.ID);
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
		String pathUri = get(RTComponent.PATH_URI);
		return RTSystemBuilder.getNamingUri(pathUri);
	}

	/**
	 * getCategory
	 *
	 * @return
	 */
	public String getCategory() {
		String Id = get(RTComponent.ID);
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
		String Id = get(RTComponent.ID);
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
		String Id = get(RTComponent.ID);
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

	private PythonRTCLauncher launcher;
	/**
	 * setLauncher
	 *
	 * @param launcher
	 */
	public void setLauncher(PythonRTCLauncher launcher) {
		this.launcher = launcher;
	}
	
	public PythonRTCLauncher getLauncher() {
		return launcher;
	}

	public boolean isLaunched() {
		if(launcher != null) {
			return true;
		}
		return false;
	}
	
	String periodicRate;
	/**
	 * setPeriodicRate
	 *
	 * @param text
	 */
	public void setPeriodicRate(String text) {
		this.periodicRate = text;
	}
	
	public String getPeriodicRate() {
		return periodicRate;
	}

	private String nameServers;
	/**
	 * getNameServers
	 *
	 * @return
	 */
	public String getNameServers() {
		// TODO 自動生成されたメソッド・スタブ
		return nameServers;
	}
	
	public void setNameServers(String nameServers) {
		this.nameServers = nameServers;
	}
	

}
