/**
 * 
 */
package org.cmg.tapas.rm.runtime;

import java.util.HashMap;
import java.util.Set;

import org.cmg.tapas.pa.Process;

/**
 * @author loreti
 *
 */
public class State implements Process<State,ModuleAction> {
	
	protected int state;
	private AbstractReactiveModule module;
	
	public State( AbstractReactiveModule module, int state ) {
		this.module = module;
		this.state = state;
	}

	@Override
	public HashMap<ModuleAction, Set<State>> getNext() {
		return this.module.getNext(this);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		if ( obj instanceof State ) {
			State other = (State) obj;
			return this.state == other.state;
		}
		return false;
	}

	@Override
	public int hashCode() {
		return state;
	}

	@Override
	public String toString() {
		return module.stringOfState( this );
	}

	public State getState( int stateIndex ) {
		return module.getState( stateIndex );
	}

	@Override
	public boolean isDeadlock() {
		HashMap<ModuleAction, Set<State>> next = getNext();
		if (next == null) {
			return true;
		}
		if (next.size() == 0) {
			return true;
		}
		for (ModuleAction act : next.keySet()) {
			if (!next.get(act).isEmpty()) {
				return false;
			}
		}
		return true;
	}


}
