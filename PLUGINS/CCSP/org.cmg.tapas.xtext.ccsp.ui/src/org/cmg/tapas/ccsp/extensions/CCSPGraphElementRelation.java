/**
 * 
 */
package org.cmg.tapas.ccsp.extensions;

/**
 * @author loreti
 *
 */
public class CCSPGraphElementRelation {

	private String src;
	
	private String action;
	
	private String trg;

	public CCSPGraphElementRelation(String src, String action, String trg) {
		super();
		this.src = src;
		this.action = action;
		this.trg = trg;
	}

	/**
	 * @return the src
	 */
	public String getSrc() {
		return src;
	}

	/**
	 * @return the action
	 */
	public String getAction() {
		return action;
	}

	/**
	 * @return the trg
	 */
	public String getTrg() {
		return trg;
	}
	
	
	
}
