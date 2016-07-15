/**
 * 
 */
package org.cmg.tapas.ccsp.runtime;

import java.util.HashMap;
import java.util.Set;

/**
 * @author loreti
 *
 */
public class CCSPParallel extends CCSPProcess {
	
	protected CCSPProcess left;
	protected CCSPProcess right;

	public CCSPParallel(CCSPProcess left, CCSPProcess right) {
		this.left = left;
		this.right = right;
	}

	@Override
	protected HashMap<CCSPAction, Set<CCSPProcess>> _getNext() {
		HashMap<CCSPAction, Set<CCSPProcess>> next = new HashMap<CCSPAction, Set<CCSPProcess>>();
		HashMap<CCSPAction, Set<CCSPProcess>> leftNext = left.getNext();
		HashMap<CCSPAction, Set<CCSPProcess>> rightNext = right.getNext();
		for (CCSPAction a : leftNext.keySet()) {
			Set<CCSPProcess> nextAfterA = leftNext.get(a);
			updateNext( next , a , doParallel(nextAfterA,right));
			CCSPAction coa = a.getComplementaryAction();
			if (coa != null) {
				doSync(next , nextAfterA , rightNext.get(coa));
			}			
		}
		for (CCSPAction a : rightNext.keySet()) {
			Set<CCSPProcess> nextAfterA = rightNext.get(a);
			updateNext( next , a , doParallel(left,nextAfterA));
		}		
		return next;
	}

	@Override
	public ProcessType getProcessType() {
		return ProcessType.PARALLEL;
	}

	@Override
	protected boolean _equals(CCSPProcess q) {
		try {
			CCSPParallel pq = (CCSPParallel) q;
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
		return left.toString()+"|"+right.toString();
	}

}
