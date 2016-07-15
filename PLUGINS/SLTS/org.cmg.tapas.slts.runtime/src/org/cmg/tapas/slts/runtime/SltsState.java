/**
 * 
 */
package org.cmg.tapas.slts.runtime;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.cmg.tapas.pa.Process;

/**
 * @author loreti
 *
 */
public class SltsState implements Process<SltsState, SltsAction> {

	private HashMap<SltsAction, Set<SltsState>> next;
	
	private boolean isDeadlock;
	
	private String name;
	
	private Set<String> labels;
	
	public SltsState( String name ) {
		this.name = name;
		this.next = new HashMap<SltsAction,Set<SltsState>>();
		this.isDeadlock = true;
		this.labels = new HashSet<String>();
	}

	public void addNext( SltsAction action , SltsState s ) {
		Set<SltsState> nextSet = next.get(action);
		if (nextSet == null) {
			nextSet = new HashSet<SltsState>();
			next.put(action, nextSet);
			isDeadlock = false;
		}
		nextSet.add(s);
	}
	
	@Override
	public HashMap<SltsAction, Set<SltsState>> getNext() {
		return next;
	}

	@Override
	public boolean isDeadlock() {
		return isDeadlock;
	}

	public String getName() {
		return name;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return name.hashCode();
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof SltsState) {
			return name.equals(((SltsState) obj).name );
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return name;
	}
	
	public void addLabel(String label){
		labels.add(label);
	}
	
	public Set<String> getLabels(){
		return labels;
	}
}
