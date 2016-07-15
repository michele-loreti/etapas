/**
 * 
 */
package org.cmg.tapas.ccsp.extensions;

import org.cmg.tapas.ccsp.runtime.CCSPAction;
import org.cmg.tapas.ccsp.runtime.CCSPProcess;

/**
 * @author loreti
 *
 */
public class CCSPTransition {
	
	private CCSPProcess source;
	
	private CCSPAction action;
	
	private CCSPProcess target;
	
	public CCSPTransition(CCSPProcess source, CCSPAction action,
			CCSPProcess target) {
		super();
		this.source = source;
		this.action = action;
		this.target = target;
	}

	/**
	 * @return the source
	 */
	public CCSPProcess getSource() {
		return source;
	}

	/**
	 * @return the action
	 */
	public CCSPAction getAction() {
		return action;
	}

	/**
	 * @return the target
	 */
	public CCSPProcess getTarget() {
		return target;
	}


}
