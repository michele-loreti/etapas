package org.cmg.tapas.core.graph.filter;

import org.cmg.tapas.core.graph.ActionInterface;

public class WeakFilter<X extends ActionInterface> extends AbstractFilter<X> {
	
	private Filter<X> f;
	
	public WeakFilter(Filter<X> f){
		this.f = f;
	}
	
	public boolean check(X t) {
		
		if(t.isTau() | f.check(t))
			return true;
		
		return false;
	}

	public String toString(){
		if(f == null)
			return "tau";
		else return f.toString();
	}

	@Override
	public boolean equals(Object obj) {
		if(obj == null)
			return false;
		
		if (obj instanceof WeakFilter) {
			WeakFilter<X> f = (WeakFilter<X>) obj;
			
			return f.equals(f.f);
		}
		
		return false;
	}
}
