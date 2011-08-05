/**
 * RTCCondition.java
 *
 * @author Yuki Suga (ysuga.net)
 * @date 2011/07/31
 * @copyright 2011, ysuga.net allrights reserved.
 *
 */
package net.ysuga.rtsbuilder;

/**
 * 
 * @author ysuga
 *
 */
public class RTCCondition {
 

	final public static RTCCondition ACTIVE = new RTCCondition(RTCCondition.ACTIVE_STATE);
	final public static RTCCondition INACTIVE = new RTCCondition(RTCCondition.INACTIVE_STATE);
	final public static RTCCondition CREATED = new RTCCondition(RTCCondition.CREATED_STATE);
	final public static RTCCondition ERROR = new RTCCondition(RTCCondition.ERROR_STATE);
	final public static RTCCondition ANY = new RTCCondition(RTCCondition.ANY_STATE);
	final public static RTCCondition NONE = new RTCCondition(RTCCondition.NONE_STATE);

	final private static int ACTIVE_STATE = 0;
	final private static int INACTIVE_STATE = 1;
	final private static int CREATED_STATE = 2;
	final private static int ERROR_STATE = 3;
	final private static int ANY_STATE = 4;
	final private static int NONE_STATE = 5;
	
	final private static String ACTIVE_STR = "RTC_ACTIVE";
	final private static String INACTIVE_STR = "RTC_INACTIVE";
	final private static String CREATED_STR = "RTC_CREATED";
	final private static String ERROR_STR = "RTC_ERROR";
	final private static String ANY_STR = "RTC_ANY";
	final private static String NONE_STR = "RTC_NONE";

	final private int state;
	private RTCCondition(int state) {
		this.state = state;
	}
	
	/**
	 * 
	 * <div lang="ja">
	 * @return
	 * </div>
	 * <div lang="en">
	 * @return
	 * </div>
	 */
	final public String toString() {
		switch(state) {
		case ACTIVE_STATE:
			return ACTIVE_STR;
		case INACTIVE_STATE:
			return INACTIVE_STR;
		case CREATED_STATE:
			return CREATED_STR;
		case ERROR_STATE:
			return ERROR_STR;
		case ANY_STATE:
			return ANY_STR;
		default:
			return NONE_STR;
		}
	}
	
	/**
	 * 
	 * <div lang="ja">
	 *
	 * @param conditionStr
	 * @return
	 * </div>
	 * <div lang="en">
	 *
	 * @param conditionStr
	 * @return
	 * </div>
	 */
	final static public RTCCondition parseString(String conditionStr) {
		if(conditionStr.equals(ACTIVE_STR)) {
			return ACTIVE;
		} else if(conditionStr.equals(INACTIVE_STR)) {
			return INACTIVE;
		} else if(conditionStr.equals(ERROR_STR)) {
			return ERROR;
		} else if(conditionStr.equals(CREATED_STR)) {
			return CREATED;
		} else if(conditionStr.equals(ANY_STR)) {
			return ANY;
		}		
		return NONE;
	}
	
	@Override
	/**
	 * 
	 * <div lang="ja">
	 * @param object
	 * @return
	 * </div>
	 * <div lang="en">
	 * @param object
	 * @return
	 * </div>
	 */
	public boolean equals(Object object) {
		if(((RTCCondition)object).state == ANY_STATE || this.state == ANY_STATE) {
			return true;
		}
		return ((RTCCondition)object).state == this.state;
	}
	
}
