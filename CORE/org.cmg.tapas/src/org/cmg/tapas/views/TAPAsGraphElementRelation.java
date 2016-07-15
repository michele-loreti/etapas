/**
 * 
 */
package org.cmg.tapas.views;

/**
 * @author loreti
 *
 */
public class TAPAsGraphElementRelation {

	private Object src;
	
	private Object action;
	
	private Object trg;

	public TAPAsGraphElementRelation(Object src, Object action, Object trg) {
		super();
		this.src = src;
		this.action = action;
		this.trg = trg;
	}

	/**
	 * @return the src
	 */
	public String getSrc() {
		return src.toString();
	}

	/**
	 * @return the action
	 */
	public String getAction() {
		return action.toString();
	}

	/**
	 * @return the trg
	 */
	public String getTrg() {
		return trg.toString();
	}
	
	
	
}
