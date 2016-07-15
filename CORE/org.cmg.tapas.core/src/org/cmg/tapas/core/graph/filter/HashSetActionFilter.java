/**
 * 
 */
package org.cmg.tapas.core.graph.filter;


import java.util.Set;

/**
 * @author Michele Loreti
 *
 */
public class HashSetActionFilter<T> extends AbstractFilter<T> {

	private Set<T> actions;

	public HashSetActionFilter(Set<T> actions) {
		this.actions = actions;
	}

	/* (non-Javadoc)
	 * @see graph.Filter#check(java.lang.Object)
	 */
	public boolean check(Object t) {
		if (actions.isEmpty()) {
			return true;
		}
		return actions.contains(t);
	}
	
	public String toString() {
		String str_actions = "";
		if(actions.size() > 1)
			str_actions = "(";
		
		String op = ", ";
		boolean foo = false;
		for (T act: actions) {
			if(!foo){
				str_actions += act;
				foo = true;
			} else
				str_actions += op+act;
		}
		
		if(actions.size() > 1)
			str_actions += ")";
		
		return str_actions;
	}

	@Override
	public boolean equals(Object obj) {
		if(obj == null)
			return false;
		
		if (obj instanceof HashSetActionFilter) {
			@SuppressWarnings("unchecked")
			HashSetActionFilter<T> f = (HashSetActionFilter<T>) obj;
			
			return actions.containsAll(f.actions) && f.actions.containsAll(actions);
		}
		
		return false;
	}

}
