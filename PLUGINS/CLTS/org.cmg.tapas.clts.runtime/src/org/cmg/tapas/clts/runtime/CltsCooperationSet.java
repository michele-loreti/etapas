package org.cmg.tapas.clts.runtime;

import java.util.HashSet;

import org.cmg.tapas.core.graph.filter.Filter;

public class CltsCooperationSet implements Filter<CltsAction> {
	
	protected HashSet<CltsAction> set; 
	
	public CltsCooperationSet( CltsAction ...actions ){
		this.set = new HashSet<CltsAction>();
		for (int i=0 ; i < actions.length; i++){
			this.set.add(actions[i]);
		}
	}
	
	@Override
	public boolean check(CltsAction t) {
		return ( (t!=null)&&(set.contains(t)) );
	}

	@Override
	public String toString() {
		return set.toString();
	}

	
}
