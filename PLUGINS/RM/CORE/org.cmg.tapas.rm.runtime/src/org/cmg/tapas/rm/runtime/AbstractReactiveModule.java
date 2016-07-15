/**
 * 
 */
package org.cmg.tapas.rm.runtime;

import java.util.HashMap;
import java.util.Set;

import org.cmg.tapas.core.graph.Graph;
import org.cmg.tapas.core.graph.filter.Filter;

/**
 * @author loreti
 *
 */
public abstract class AbstractReactiveModule {
	
	
	private StateEnumerator enumerator;
	private String name;
	private Filter<State> initialPredicate;
	
	
	public AbstractReactiveModule( StateEnumerator enumerator ) {
		this( null , enumerator );
	}

	public AbstractReactiveModule( String name , StateEnumerator enumerator ) {
		this.name = name;
		this.enumerator = enumerator;
	}

	public abstract HashMap<ModuleAction, Set<State>> getNext(State state);

	public abstract Set<State> getNext(State state , ModuleAction act );

	public String stringOfState( State state ) {
		return enumerator.stringOfState( state.state );
	}

	public State getState( int stateIndex ) {
		return new State( this , stateIndex );
	}
	
	public State getState( int[] values ) {
		return new State( this , enumerator.enumerate( values ));
	}

	public String getName( ) {
		return name;
	}
	
	public void setName( String name ) {
		this.name = name;
	}
	
	public int getStateSpaceSize() {
		return this.enumerator.size();
	}
	 
	public Graph<State,ModuleAction> getGraph() {
		Graph<State, ModuleAction> graph = new Graph<State, ModuleAction>();
		for (State s : enumerator.states(this)) {
			graph.addState(s);
		}
		for (State s : enumerator.states(this)) {
			HashMap<ModuleAction, Set<State>> next = s.getNext();
			for (ModuleAction act : next.keySet()) {
				Set<State> nextStates = next.get(act);
				if (nextStates != null) {
					for (State s2 : nextStates) {
						graph.addEdge(s, act, s2);
					}
				}
			}
		}		
		return graph;
	}
	
	public Iterable<State> states() {
		return enumerator.states(this);
	}
}
