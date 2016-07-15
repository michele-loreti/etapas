/**
 * 
 */
package org.cmg.tapas.rm.runtime;

import java.util.HashSet;
import java.util.Set;

import org.cmg.tapas.core.graph.filter.Filter;


/**
 * @author loreti
 *
 */
public class StateLabel {
	
	private Filter<State> predicate;
	
	public StateLabel( Filter<State> predicate ) {
		this.predicate = predicate;
	}
	
	public Set<State> eval( AbstractReactiveModule module ) {
		HashSet<State> satSet = new HashSet<State>();
		for (State s : module.states()) {
			if (predicate.check(s)) {
				satSet.add(s);
			}
		}
		return satSet;
	}
	
}
