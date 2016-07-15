package org.cmg.tapas.core.graph.algorithms;

import org.cmg.tapas.core.graph.ActionInterface;


/** 
 * EquivalenceSolver rappresenta l'interfaccia per 
 * le classi che implementano gli algoritmi per il calcolo
 * di equivalenze.
 * <p>
 * @param <S> tipo degli stati
 * @param <A> tipo delle azioni
 * 
 * @author Guzman Tierno
 **/
public interface EquivalenceSolver <
	S ,
	A extends ActionInterface
> extends EquivalenceChecker<S,A> {
	
	/**
     * Calcola l'equivalenza meno 
     * fine tra quelle pi� fini della partizione specificata. <p>
     * Il parametro <tt>initPartition</tt> rappresenta la partizione
     * iniziale imposta sugli stati, pu� essere null. <p>
     * Il vaolore di ritorno � un valutatore che assegna ad ogni
     * stato del grafo un valore di un certo tipo. 
     * Due stati sono nella stessa classe di equivalenza se e solo se
     * ad essi viene associato lo stesso valore (==).
     **/
	<V> Evaluator<S,?> computeEquivalence(
		Evaluator<S,V> initPartition
	);
	
	/**
     * Calcola la massima bisimulazione. E' equivalente a una chiamata  
     * <tt>computeEquivalence(null)</tt>. <p>
     * Il vaolore di ritorno � un valutatore che assegna ad ogni
     * stato del grafo un valore di un certo tipo. 
     * Due stati sono nella stessa classe di equivalenza se e solo se
     * ad essi viene associato lo stesso valore (==).
     **/
	Evaluator<S,?> computeEquivalence();
	
	/**
     * Controlla se gli stati specificati sono nella stessa 
     * classe di equivalenza del tipo specificato dalla sottoclasse e  
     * meno fine tra quelle pi� fini della partizione specificata.
     * Il metodo pu� risultare pi� veloce di una chiamata
     * a <tt>computeEquivalence(initPartition)</tt> seguita da un controllo.
     * Il parametro <tt>initPartition</tt> rappresenta la partizione
     * iniziale imposta sugli stati, pu� essere null. <p>
     **/
	<V> boolean checkEquivalence(
		Evaluator<S,V> initPartition, S state1, S state2
	);
	
	
	/**
     * Controlla se gli stati specificati sono nella 
     * stessa classe di equivalenza.
     * Il metodo pu� risultare pi� veloce di una chiamata
     * a <tt>computeEquivalence()</tt> seguita da un controllo. <p>
     **/
	boolean checkEquivalence(S state1, S state2);



}














