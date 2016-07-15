package org.cmg.tapas.core.graph.filter;

import org.cmg.tapas.core.graph.ActionInterface;

public class OrFilter<X extends ActionInterface> extends AbstractFilter<X> {
	
	private Filter<X> f1;
	private Filter<X> f2;
	
	public OrFilter(Filter<X> f1, Filter<X> f2){
		this.f1 = f1;
		this.f2 = f2;
	}
	
	public boolean check(X t) {		
		if(f1.check(t) | f2.check(t))
			return true;
		
		return false;
	}
	
	public String toString(){
		return "("+f1.toString()+" | "+f2.toString()+")";
	}

	@Override
	public boolean equals(Object obj) {
		if(obj == null)
			return false;
		
		if (obj instanceof OrFilter) {
			OrFilter<X> f = (OrFilter<X>) obj;
			
			return f1.equals(f.f1) && f2.equals(f.f2);
		}
		
		return false;
	}
}
