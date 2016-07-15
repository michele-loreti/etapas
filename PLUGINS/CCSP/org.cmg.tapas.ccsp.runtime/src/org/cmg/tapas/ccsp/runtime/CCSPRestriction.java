/**
 * 
 */
package org.cmg.tapas.ccsp.runtime;

import java.util.HashMap;
import java.util.Set;

import org.cmg.tapas.core.graph.filter.Filter;

/**
 * @author loreti
 *
 */
public class CCSPRestriction extends CCSPProcess {
	
	private CCSPProcess process;
	private Filter<CCSPAction> actionSet;
	

	/**
	 * @param process
	 * @param actionSet
	 */
	public CCSPRestriction(CCSPProcess process, Filter<CCSPAction> actionSet) {
		super();
		this.process = process;
		this.actionSet = actionSet;
	}

	@Override
	protected HashMap<CCSPAction, Set<CCSPProcess>> _getNext() {
		HashMap<CCSPAction, Set<CCSPProcess>> internalNext = process.getNext();
		HashMap<CCSPAction, Set<CCSPProcess>> next = new HashMap<CCSPAction, Set<CCSPProcess>>();
		for (CCSPAction a : internalNext.keySet()) {
			if (!actionSet.check(a)) {
				next.put(a, doRestrict(internalNext.get(a),actionSet));
			}
		}
		return next;
	}

	@Override
	public ProcessType getProcessType() {
		return ProcessType.RESTRICTION;
	}

	@Override
	protected boolean _equals(CCSPProcess q) {
		try {
			CCSPRestriction rq = (CCSPRestriction) q;
			return process.equals(rq.process)&&(actionSet.equals(rq.actionSet));
		} catch (ClassCastException e) {
			return false;
		}
	}

	@Override
	protected Integer _hashcode() {
		return process.hashCode()^actionSet.hashCode();
	}

	@Override
	protected String _toString() {
		return process.toString()+"\\{"+actionSet.toString()+"}";
	}

}
