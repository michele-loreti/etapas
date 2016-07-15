package org.cmg.tapas.core.graph.algorithms;


import java.util.Iterator;

import org.cmg.tapas.core.graph.ActionInterface;

/**
 * La classe AcceptanceSet rappresenta l'insieme
 * di accettazione di uno stato di un grafo
 * di accettazione.
 *
 * Invariante:  
 *	nessun elemento (ActionSet) contiene un altro elemento.  
 * 
 * @author Guzman Tierno
 */
public class AcceptanceSet<A extends ActionInterface> 
extends FastTreeSet<ActionSet<A>> {		
	private static final long serialVersionUID = 1;
	
	/** 
	 * Costruisce un AcceptanceSet con, come unico elemento,
	 * un insieme di azioni vuoto.
	 * Se addEmptySet � falso costruisce un AcceptanceSet vuoto. 
	 **/
	public AcceptanceSet(boolean addEmptySet) {
		if( addEmptySet )
			super.add(new ActionSet<A>());
	} 
	
	/** Costruisce un AcceptanceSet vuoto. **/
	public AcceptanceSet() { 
        // empty set
	} 
	
	/** 
	 * Aggiunge un nuovo insieme di azioni.
	 * L'insieme viene aggiunto solo se non contiene
	 * un altro insieme presente.
	 * Se viene aggiutno gli alti insiemi
	 * presenti pi� grandi di quello dato vengono rimossi.
	 **/
	@Override
	public boolean add(ActionSet<A> actionSet) {		
		Iterator<ActionSet<A>> iter = iterator();
		int compare;
		while( iter.hasNext() ) {
			compare = ActionSet.bigger( iter.next(), actionSet );
			if( compare==0 || compare==2 )
				return false;
			if( compare==1 )
				iter.remove();			
		}		
		
		return super.add(actionSet);
	}

}













