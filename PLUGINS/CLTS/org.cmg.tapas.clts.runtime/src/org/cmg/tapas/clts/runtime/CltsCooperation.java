package org.cmg.tapas.clts.runtime;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.cmg.tapas.core.graph.filter.Filter;

public class CltsCooperation extends CltsProcess{
	
	private CltsProcess left;
	private Filter<CltsAction> actionSet;
	private CltsProcess right;
	
	public CltsCooperation(CltsProcess left, Filter<CltsAction> actionSet, CltsProcess right){
		super();
		this.left = left;
		this.actionSet = actionSet;
		this.right = right;
	}
	
	@Override
	protected HashMap<CltsAction, Set<CltsProcess>> _getNext( ) {
		HashMap<CltsAction, Set<CltsProcess>> leftNext = left.getNext(  );
		HashMap<CltsAction, Set<CltsProcess>> rightNext = right.getNext(  );
		HashMap<CltsAction, Set<CltsProcess>> next = new HashMap<CltsAction, Set<CltsProcess>>();
		for (CltsAction a : leftNext.keySet()) {
			if (actionSet.check(a)) {
				next.put(a, doCooperate(leftNext.get(a),actionSet,rightNext.get(a)));
			} else {
				next.put(a, doCooperate(leftNext.get(a),actionSet,right));
			}
		}
		for (CltsAction a : rightNext.keySet()) {			
			if (!actionSet.check(a)) {
//				next.put(a, doCooperate(left, actionSet, rightNext.get(a)));
				updateNext(next, a, doCooperate(left, actionSet, rightNext.get(a)));
			}
		}
		return next;
	}

	@Override
	public ProcessType getProcessType() {
		return ProcessType.COMPOSITION;
	}

	@Override
	protected String _toString() {
		return left.toString()+" |"+actionSet.toString()+"| "+right.toString();
	}

	@Override
	protected Integer _hashcode() {
		return left.hashCode()^actionSet.hashCode();
	}

	@Override
	protected boolean _equals(CltsProcess q) {
		try {
			CltsCooperation rq = (CltsCooperation) q;
			return left.equals(rq.left)&&(actionSet.equals(rq.actionSet))&&(right.equals(rq.right));
		} catch (ClassCastException e) {
			return false;
		}
	}

	@Override
	public boolean sat(Set<CltsState> set) {
		return ( left.sat(set) || right.sat(set));
	}

	@Override
	public Set<String> getLabels() {
		HashSet<String> toReturn = new HashSet<String>();
		toReturn.addAll(left.getLabels());
		toReturn.addAll(right.getLabels());
		return toReturn;
	}

}
