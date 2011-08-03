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
 * @author ysuga
 *
 */
public class RTCCondition {

	public static RTCCondition ACTIVE = new RTCCondition(RTCCondition.ACTIVE_STATE);
	public static RTCCondition INACTIVE = new RTCCondition(RTCCondition.INACTIVE_STATE);
	public static RTCCondition CREATED = new RTCCondition(RTCCondition.CREATED_STATE);
	public static RTCCondition ERROR = new RTCCondition(RTCCondition.ERROR_STATE);
	public static RTCCondition ANY = new RTCCondition(RTCCondition.ANY_STATE);
	public static RTCCondition NONE = new RTCCondition(RTCCondition.NONE_STATE);

	public static int ACTIVE_STATE = 0;
	public static int INACTIVE_STATE = 1;
	public static int CREATED_STATE = 2;
	public static int ERROR_STATE = 3;
	public static int ANY_STATE = 4;
	public static int NONE_STATE = 5;
	
	
	
	private int state;
	private RTCCondition(int state) {
		this.state = state;
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
