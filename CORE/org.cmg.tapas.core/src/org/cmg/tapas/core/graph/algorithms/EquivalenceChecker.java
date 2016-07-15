package org.cmg.tapas.core.graph.algorithms;


/** 
 * EquivalenceChecker rappresenta l'interfaccia delle classi
 * che offrono i metodi per il calcolo di equivalenze.
 * <p>
 * @param <S> tipo degli stati
 * @param <A> tipo delle azioni
 * 
 * @author Guzman Tierno
 **/
public interface EquivalenceChecker <
	S ,
	A 
> {
	
	/**
     * Controlla se gli stati specificati sono nella stessa 
     * classe di equivalenza rispetto all'equivalenza, 
     * del tipo fissato dalla sottoclasse
     **/
	boolean checkEquivalence(S state1, S state2);
	
}














