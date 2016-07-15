package org.cmg.tapas.core.graph.algorithms;

import org.cmg.tapas.core.graph.ActionInterface;

/**
 * ActionLabel rappresenta una azione usata come etichetta.
 * Si ammette che l'azione possa essere null.
 * 
 * @param <A> tipo delle azioni
 * 
 * @author Guzman Tierno
 */
public class ActionLabel<A extends ActionInterface> {
	private A action;
	
	public ActionLabel(A action) {
		this.action = action;
	}
	
	public A getAction() {
		return action;
	}
	
	@Override
	public boolean equals(Object o) {
		if( o==null || !(o instanceof ActionLabel) )
			return false;
		
		ActionLabel other = (ActionLabel) o;
		if( action==null )
			return other.action==null;
			
		return action.equals( other.action );
	}

	@Override
	public String toString() {
		if( action==null )
			return " ";
			
		return action.toString(); 
	}
}
