/**
 * 
 */
package org.cmg.tapas.core.graph.filter;


/**
 * @author loreti
 *
 */
public class NotFilter<T> extends AbstractFilter<T> {

	private Filter<T> filter;

	public NotFilter(Filter<T> filter) {
		this.filter = filter;
	}

	/* (non-Javadoc)
	 * @see graph.Filter#check(java.lang.Object)
	 */
	public boolean check(T t) {
		return !filter.check(t);
	}
	
	public String toString() {
		return "-"+filter.toString();
	}

	@Override
	public boolean equals(Object obj) {
		if(obj == null)
			return false;
		
		if (obj instanceof NotFilter) {
			NotFilter<T> f = (NotFilter<T>) obj;
			
			return filter.equals(f.filter);
		}
		
		return false;
	}

}
