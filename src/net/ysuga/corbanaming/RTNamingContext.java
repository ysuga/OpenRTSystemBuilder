/**
 * RTNamingContext.java
 *
 * @author Yuki Suga (ysuga.net)
 * @date 2011/09/02
 * @copyright 2011, ysuga.net allrights reserved.
 *
 */
package net.ysuga.corbanaming;

import java.util.ArrayList;

/**
 * <div lang="ja">
 *
 * </div>
 * <div lang="en">
 *
 * </div>
 * @author ysuga
 *
 */
public class RTNamingContext extends ArrayList<RTNamingContext> {
	
	private RTNamingContext parentContext;
	
	public RTNamingContext getParentContext() {
		return parentContext;
	}

	private String kind;
	
	public String getKind() {
		return kind;
	}
	
	private String id;
	
	public String getId() {
		return id;
	}
	/**
	 * <div lang="ja">
	 * コンストラクタ
	 * </div>
	 * <div lang="en">
	 * Constructor
	 * </div>
	 */
	public RTNamingContext(String id, String kind) {
		super();
		this.id = id;
		this.kind = kind;
	}
	
	/**
	 * <div lang="ja">
	 * コンストラクタ
	 * </div>
	 * <div lang="en">
	 * Constructor
	 * </div>
	 */
	public RTNamingContext(String id) {
		this(id, "");
	}
	
	@Override
	public boolean add(RTNamingContext child) {
		child.parentContext = this;
		return super.add(child);
	}
	
	public boolean remove(RTNamingContext child) {
		if(super.remove(child)) {
			child.parentContext = null;
			return true;
		}
		return false;
	}
	/**
	 * getName
	 * <div lang="ja">
	 * 
	 * @return
	 * </div>
	 * <div lang="en">
	 *
	 * @return
	 * </div>
	 */
	public String getName() {
		if(getKind().equals("")) {
			return getId();
		}
		StringBuilder sb = new StringBuilder();
		sb.append(getId());
		sb.append(".");
		sb.append(getKind());
		return sb.toString();
	}
	
	

}
