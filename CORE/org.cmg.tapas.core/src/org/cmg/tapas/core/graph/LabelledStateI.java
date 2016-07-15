package org.cmg.tapas.core.graph;



/**
 * Interfaccia per gli stati etichettati, ossia 
 * gli stati con informazione aggiunta. 
 * 
 * @param <L> tipo per l'etichetta (informazione aggiuntiva) dello stato.
 *
 * @author Guzman Tierno
 */
public interface LabelledStateI<L>  
extends StateInterface {
	
	/** Rende l'etichetta di questo stato. **/
	L getLabel();
	 
}





























