package org.cmg.tapas.core.graph.algorithms;

import org.cmg.tapas.core.graph.ActionInterface;
import org.cmg.tapas.core.graph.LabelledState;

/**
 * La classe AcceptanceState rappresenta uno stato
 * di un grafo di accettazione.
 * 
 * @author Guzman Tierno
 */
public class AcceptanceState<A extends ActionInterface> 
extends LabelledState<AcceptanceLabel<A>> {
	/** Costruisce un AcceptanceState con l'etichetta specificata. **/
	public AcceptanceState(String name, AcceptanceLabel<A> label) {
		super(name, label);
	}
	
	/** 
	 * Costruisce un AcceptanceState con una etichetta avente come
	 * contenuto i dati specificati.
	 **/
	public AcceptanceState(
		String name,
		AcceptanceSet<A> acceptance, 
		Boolean convergent,
		Boolean finale
	) {
		super(
			name,
			new AcceptanceLabel<A>(acceptance, convergent, finale)
		);
	}
	
	public boolean isConvergent(){		 
		Boolean res = getLabel().getConvergenceFlag();
		 if(res == null)
			 return false;
		 
		 return res.booleanValue();
	}
	
	public boolean isFinal(){	 
		Boolean res = getLabel().getFinalFlag();
		 if(res == null)
			 return false;
		 
		return res.booleanValue();
	}

}




















