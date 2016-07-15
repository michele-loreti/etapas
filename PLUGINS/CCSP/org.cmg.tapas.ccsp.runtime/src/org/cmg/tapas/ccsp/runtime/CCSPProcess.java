/**
 * 
 */
package org.cmg.tapas.ccsp.runtime;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.cmg.tapas.core.graph.filter.Filter;
import org.cmg.tapas.pa.Process;

/**
 * @author loreti
 *
 */
public abstract class CCSPProcess implements Process<CCSPProcess,CCSPAction> {

	public enum ProcessType {
		STATE,
		NIL,
		PARALLEL,
		COOPERATION,
		RENAMING,
		RESTRICTION
	}
	
	public static CCSPProcess NIL = new NilProcess();
	
	private HashMap<CCSPAction, Set<CCSPProcess>> next;
	private Integer hashcode;
	private String str;
	
	public CCSPProcess() {}
	
	@Override
	public HashMap<CCSPAction, Set<CCSPProcess>> getNext() {
		if (next == null) {
			next = _getNext();
		}
		return next;
	}

	protected abstract HashMap<CCSPAction, Set<CCSPProcess>> _getNext();
	
	public abstract ProcessType getProcessType();

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object arg0) {
		if (!(arg0 instanceof CCSPProcess)) {
			return false;
		}
		CCSPProcess q = (CCSPProcess) arg0;
		if (q.getProcessType()!=getProcessType()) {
			return false;
		}
		return _equals(q);
	}

	protected abstract boolean _equals(CCSPProcess q);

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		if (hashcode == null) {
			hashcode = _hashcode();
		}
		return hashcode;
	}

	protected abstract Integer _hashcode();

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		if (str == null) {
			str = _toString();
		}
		return str;
	}

	protected abstract String _toString();
	
	protected void updateNext(HashMap<CCSPAction, Set<CCSPProcess>> next, CCSPAction a, Set<CCSPProcess> set) {
		if (set == null) {
			return ;
		}
		Set<CCSPProcess> current = next.get(a);
		if (current == null) {
			current = new HashSet<CCSPProcess>();
			next.put(a, current);
		}
		current.addAll(set);
	}

	protected Set<CCSPProcess> doRestrict(Set<CCSPProcess> set, Filter<CCSPAction> actionSet) {
		HashSet<CCSPProcess> result = new HashSet<CCSPProcess>();
		for (CCSPProcess p : set) {
			result.add(new CCSPRestriction(p, actionSet));
		}
		return result;
	}
	
	protected Set<CCSPProcess> doCooperate( Set<CCSPProcess> next , Filter<CCSPAction> syncset , CCSPProcess q ) {
		HashSet<CCSPProcess> set = new HashSet<CCSPProcess>();
		for (CCSPProcess p : next) {
			set.add(new CCSPCooperation(p, syncset, q));
		}
		return set;
	}

	protected Set<CCSPProcess> doCooperate( CCSPProcess p , Filter<CCSPAction> syncset , Set<CCSPProcess> next ) {
		HashSet<CCSPProcess> set = new HashSet<CCSPProcess>();
		for (CCSPProcess q : next) {
			set.add(new CCSPCooperation(p, syncset, q));
		}
		return set;
	}
	

	protected Set<CCSPProcess> doCooperate( Set<CCSPProcess> nextLeft , Filter<CCSPAction> syncset , Set<CCSPProcess> nextRight ) {
		if ((nextLeft == null)||(nextRight==null)) {
			return null;
		}
		HashSet<CCSPProcess> set = new HashSet<CCSPProcess>();
		for (CCSPProcess p : nextLeft) {
			for (CCSPProcess q : nextRight) {
				set.add(new CCSPCooperation(p, syncset, q));
			}
		}
		return set;
	}
	

	protected Set<CCSPProcess> doParallel(Set<CCSPProcess> next, CCSPProcess q) {
		HashSet<CCSPProcess> set = new HashSet<CCSPProcess>();
		for (CCSPProcess p : next) {
			set.add(new CCSPParallel(p, q));
		}
		return set;
	}

	protected Set<CCSPProcess> doParallel(CCSPProcess p, Set<CCSPProcess> next) {
		HashSet<CCSPProcess> set = new HashSet<CCSPProcess>();
		for (CCSPProcess q : next) {
			set.add(new CCSPParallel(p, q));
		}
		return set;
	}

	protected void doSync(HashMap<CCSPAction, Set<CCSPProcess>> next, Set<CCSPProcess> leftNext, Set<CCSPProcess> rightNext) {
		if ((leftNext==null)||(rightNext == null)) {
			return ;
		}
		HashSet<CCSPProcess> set = new HashSet<CCSPProcess>();
		for (CCSPProcess p: leftNext) {
			for(CCSPProcess q: rightNext) {
				set.add(new CCSPParallel(p,q));
			}
		}
		updateNext(next, CCSPAction.TAU, set);
	}

	protected Set<CCSPProcess> doRenaming(Set<CCSPProcess> set, Map<CCSPChannel, CCSPChannel> renaming) {
		HashSet<CCSPProcess> result = new HashSet<CCSPProcess>();
		for (CCSPProcess p : set) {
			result.add(new CCSPRenaming(p, renaming));
		}
		return result;
	}

	private static class NilProcess extends CCSPProcess {

		public static String NIL_STRING = "nil";
		
		@Override
		protected HashMap<CCSPAction, Set<CCSPProcess>> _getNext() {
			return new HashMap<CCSPAction, Set<CCSPProcess>>();
		}

		@Override
		public ProcessType getProcessType() {
			return ProcessType.NIL;
		}

		@Override
		protected boolean _equals(CCSPProcess q) {
			return true;
		}

		@Override
		protected Integer _hashcode() {
			return NIL_STRING.hashCode();
		}

		@Override
		protected String _toString() {
			return NIL_STRING;
		}
		
	}

	public boolean isDeadlock() {
		HashMap<CCSPAction, Set<CCSPProcess>> next = this.getNext();
		return (next == null)||(next.size()==0);
	}
	
}
