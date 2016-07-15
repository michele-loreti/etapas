package org.cmg.tapas.core.graph.algorithms;


import java.util.Collection;

import org.cmg.tapas.core.graph.ActionInterface;


/**
 * ActionSet rappresenta un insieme di azioni.
 *
 * @author Guzman Tierno
 */
public class ActionSet<A extends ActionInterface> 
extends FastTreeSet<A> {		
	private static final long serialVersionUID = 1;

	/** Costruisce un ActionSet<A> vuoto. **/
	public ActionSet() {
        // empty set
	}

	/** Costruisce un ActionSet<A> a partire da un Set<A>. **/
	public ActionSet(Collection<? extends A> set) {
		super(set);
	}
				
}


















