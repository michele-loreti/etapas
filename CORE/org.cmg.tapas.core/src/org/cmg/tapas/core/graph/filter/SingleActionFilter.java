/**
 * 
 */
package org.cmg.tapas.core.graph.filter;


/**
 * @author loreti
 * @param <Y>
 *
 */
public class SingleActionFilter<Y> extends AbstractFilter<Y> {

	private Y action;
	
	public SingleActionFilter(Y action) {
		this.action = action;
	}

	public boolean check(Y t) {
		return action.equals(t);
	}
	
	public String toString() {
		return action.toString();
	}

	@Override
	public boolean equals(Object obj) {
		if(obj == null)
			return false;
		
		if (obj instanceof SingleActionFilter) {
			SingleActionFilter<Y> f = (SingleActionFilter<Y>) obj;
			return this.action.equals(f.action);
		}
		
		return false;
	}

}
