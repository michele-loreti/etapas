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
public class CCSPCooperation extends CCSPProcess {
	
	protected CCSPProcess left;
	protected Filter<CCSPAction> syncset;
	protected CCSPProcess right;
	
	public CCSPCooperation( CCSPProcess left , Filter<CCSPAction> syncset , CCSPProcess right ) {
		this.left = left;
		this.syncset = syncset;
		this.right = right;
	}

	@Override
	protected HashMap<CCSPAction, Set<CCSPProcess>> _getNext() {
		HashMap<CCSPAction, Set<CCSPProcess>> next = new HashMap<CCSPAction, Set<CCSPProcess>>();
		HashMap<CCSPAction, Set<CCSPProcess>> leftNext = left.getNext();
		HashMap<CCSPAction, Set<CCSPProcess>> rightNext = right.getNext();
		for (CCSPAction a : leftNext.keySet()) {
			Set<CCSPProcess> nextAfterA = leftNext.get(a);
			if (syncset.check(a)) {
				updateNext( next , a , doCooperate(nextAfterA, syncset, rightNext.get(a)));								
			} else {
				updateNext( next , a , doCooperate(nextAfterA, syncset, right));				
			}
		}
		for (CCSPAction a : rightNext.keySet()) {
			Set<CCSPProcess> nextAfterA = rightNext.get(a);
			if (!syncset.check(a)) {
				updateNext( next , a , doCooperate(left, syncset, nextAfterA));				
			}			
		}		
		return next;
	}

	@Override
	public ProcessType getProcessType() {
		return ProcessType.COOPERATION;
	}

	@Override
	protected boolean _equals(CCSPProcess q) {
		try {
			CCSPCooperation pq = (CCSPCooperation) q;
			return left.equals(pq.left)&&right.equals(pq.right);
		} catch (ClassCastException e) {
			return false;
		}
	}

	@Override
	protected Integer _hashcode() {
		return (left.hashCode()<<16)^right.hashCode();
	}

	@Override
	protected String _toString() {
		return left.toString()+"|"+syncset.toString()+"|"+right.toString();
	}

}
