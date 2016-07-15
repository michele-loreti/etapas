package org.cmg.tapas.formulae.ltl;

import org.cmg.tapas.core.graph.filter.Filter;

public class NotFilter<S> implements Filter<S> {
	
	private Filter<S> filter;
	
	public NotFilter (Filter<S> filter){
		this.filter = filter;
	}
	
	@Override
	public boolean check(S t) {
		return !(filter.check(t));
	}

	@Override
	public String toString() {
		return "!" + filter.toString();
	}
	
	
}
