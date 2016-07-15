/**
 * 
 */
package org.cmg.tapas.core.graph.filter;


/**
 * @author loreti
 *
 */
public class AndFilter<T> extends AbstractFilter<T> {
	
	private Filter<T> f1;
	private Filter<T> f2;

	public AndFilter(Filter<T> f1, Filter<T> f2) {
		this.f1 = f1;
		this.f2 = f2;
	}

	/* (non-Javadoc)
	 * @see graph.Filter#check(java.lang.Object)
	 */
	public boolean check(T t) {
		return f1.check(t)&&f2.check(t);
	}
	
	
	public String toString(){
		return "("+f1.toString()+" & "+f2.toString()+")";
	}

	@Override
	public boolean equals(Object obj) {
		if(obj == null)
			return false;
		
		if (obj instanceof AndFilter) {
			@SuppressWarnings("unchecked")
			AndFilter<T> f = (AndFilter<T>) obj;
			
			return f1.equals(f.f1) && f2.equals(f.f2);
		}
		
		return false;
	}

}
