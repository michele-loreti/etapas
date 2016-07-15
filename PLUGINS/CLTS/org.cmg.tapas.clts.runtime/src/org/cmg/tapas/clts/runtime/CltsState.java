package org.cmg.tapas.clts.runtime;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class CltsState extends CltsProcess{
	
	private HashMap<CltsAction, Set<CltsProcess>> next;
	
	private boolean isDeadlock;
	
	protected String name;
	
	protected String ltsName;
	
	private Set<String> labels;
	
	protected int statecode;
	
	public CltsState( String name , String ltsName , int statecode  ) {
		this.name = name;
		this.ltsName = ltsName;
		this.statecode = statecode;
		this.next = new HashMap<CltsAction, Set<CltsProcess>>();
		this.isDeadlock = true;
		this.labels = new HashSet<String>();
	}
	
	public void addNext( CltsAction action , CltsProcess s ) {
		Set<CltsProcess> nextSet = next.get(action);
		if (nextSet == null) {
			nextSet = new HashSet<CltsProcess>();
			next.put(action, nextSet);
			isDeadlock = false;
		}
		nextSet.add(s);
	}
	
	@Override
	public HashMap<CltsAction, Set<CltsProcess>> getNext() {
		return next;
	}
	
	@Override
	public ProcessType getProcessType() {
		return ProcessType.STATE;
	}
	
	@Override
	public boolean isDeadlock() {
		return isDeadlock;
	}

	public String getName() {
		return name;
	}
	
	public void addLabel(String label){
		labels.add(label);
	}
	
	public Set<String> getLabels(){
		return labels;
	}

	@Override
	protected HashMap<CltsAction, Set<CltsProcess>> _getNext( ) {
		return next;
	}

	@Override
	protected String _toString() {
		return ltsName+"["+name+"]";
	}

	@Override
	protected Integer _hashcode() {
		return statecode;
	}

	@Override
	protected boolean _equals(CltsProcess q) {
		try{
			CltsState pq = (CltsState) q;
			return (statecode == pq.statecode);
		}catch (ClassCastException e) {
			return false;
		}
	}

	@Override
	public boolean sat(Set<CltsState> set) {
		return (set != null)&&(set.contains(this));
	}
	
	
}
