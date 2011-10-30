/**
 * MainProperty.java
 *
 * @author Yuki Suga (ysuga.net)
 * @date 2011/10/26
 * @copyright 2011, ysuga.net allrights reserved.
 *
 */
package net.ysuga.rtsystem.profile;

import java.util.HashMap;

/**
 *
 * @author ysuga
 *
 */
public class MainProperty extends HashMap<String, String>{

	final static String RTM_ROOT = "config.rtm_root";
	final static String RTM_JAVA_ROOT = "config.rtm_java_root";
	final static String PYTHON_PATH = "config.python_path";
	
	static private MainProperty instance;
	static void init() {
		instance = new MainProperty();
	}
	
	static public MainProperty getInstance() {
		if(instance == null) init();
		return instance;
	}
	
	
	private void setDefaultSetting() {
		String rtm_root = System.getenv("RTM_ROOT");
		put(RTM_ROOT, rtm_root);
		String rtm_java_root = System.getenv("RTM_JAVA_ROOT");
		put(RTM_JAVA_ROOT, rtm_java_root);
		put(PYTHON_PATH, "C:/Python26");
	}
	
	private MainProperty() {
		super();
		setDefaultSetting();
	}
	
	
}
