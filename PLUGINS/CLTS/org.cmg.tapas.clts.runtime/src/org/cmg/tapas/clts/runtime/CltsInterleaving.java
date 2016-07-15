package org.cmg.tapas.clts.runtime;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class CltsInterleaving extends CltsProcess{
	
	protected CltsProcess left;
	protected CltsProcess right;
	
	public CltsInterleaving(CltsProcess left, CltsProcess right){
		this.left = left;
		this.right = right;
	}
	
	@Override
	protected HashMap<CltsAction, Set<CltsProcess>> _getNext( ) {
		HashMap<CltsAction, Set<CltsProcess>> next = new HashMap<CltsAction, Set<CltsProcess>>();
		HashMap<CltsAction, Set<CltsProcess>> leftNext = left.getNext();
		HashMap<CltsAction, Set<CltsProcess>> rightNext = right.getNext();
		for(CltsAction a : leftNext.keySet()){
			Set<CltsProcess> nextAfterA = leftNext.get(a);
			updateNext( next, a, doParallel(nextAfterA, right));
		}
		for(CltsAction a : rightNext.keySet()){
			Set<CltsProcess> nextAfterA =  rightNext.get(a);
			updateNext( next, a, doParallel(left, nextAfterA));
		}
		return next;
	}
	

	@Override
	public ProcessType getProcessType() {
		return ProcessType.INTERLEAVING;
	}

	@Override
	protected String _toString() {
		return left.toString()+" | "+right.toString();
	}

	@Override
	protected Integer _hashcode() {
		return (left.hashCode()<<16)^right.hashCode();
	}

	@Override
	protected boolean _equals(CltsProcess q) {
		try {
			CltsInterleaving pq = (CltsInterleaving) q;
			return left.equals(pq.left)&&right.equals(pq.right);
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
