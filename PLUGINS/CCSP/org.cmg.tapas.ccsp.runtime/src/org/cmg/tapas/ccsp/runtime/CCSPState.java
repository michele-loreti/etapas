/**
 * 
 */
package org.cmg.tapas.ccsp.runtime;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;


/**
 * @author loreti
 *
 */
public abstract class CCSPState extends CCSPProcess {
	
	protected String processName;
	protected String stateName;
	protected int stateCode;
	protected HashMap<CCSPAction, Set<CCSPProcess>> _next;

	public CCSPState( String processName , String stateName , int stateCode ) {
		this.processName = processName;
		this.stateName = stateName;
		this.stateCode = stateCode;
		this._next = new HashMap<CCSPAction, Set<CCSPProcess>>();
	}
	
	@Override
	public ProcessType getProcessType() {
		return ProcessType.STATE;
	}

	@Override
	protected boolean _equals(CCSPProcess q) {
		try {
			CCSPState pq = (CCSPState) q;
			return (stateCode==pq.stateCode);
		} catch (ClassCastException e) {
			return false;
		}
	}

	@Override
	protected Integer _hashcode() {
		return stateCode;
	}

	@Override
	protected String _toString() {
		return processName+"["+stateName+"]";
	}

	protected abstract void initializeTransistionTable();
	
	
	
	public void addNext( CCSPAction a , CCSPProcess p ) {
		Set<CCSPProcess> nextSet = _next.get(a);
		if (nextSet == null) {
			nextSet = new HashSet<CCSPProcess>();
			_next.put(a, nextSet);
		}
		nextSet.add(p);
	}

	/* (non-Javadoc)
	 * @see org.cmg.tapas.ccsp.runtime.CCSPProcess#_getNext()
	 */
	@Override
	protected HashMap<CCSPAction, Set<CCSPProcess>> _getNext() {
		initializeTransistionTable();
		return _next;
	}
	
}
