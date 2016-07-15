package org.cmg.tapas.core.graph.algorithms.bisimulation;



import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.cmg.tapas.core.graph.algorithms.EquivalenceSolver;
import org.cmg.tapas.core.graph.algorithms.Evaluator;
import org.cmg.tapas.core.graph.ActionInterface;

/** 
 * BisimulationCheckerI rappresenta l'interfaccia per 
 * le classi che implementano gli algoritmi per il calcolo
 * della bisimulazione.
 * <p>
 * @param <S> tipo degli stati
 * @param <A> tipo delle azioni
 * 
 * @author Guzman Tierno, Michele Loreti
 **/
public interface BisimulationCheckerI <
	S ,
	A extends ActionInterface> extends EquivalenceSolver<S,A> {
	
	/**
     * Calcola la bisimulazione meno 
     * fine tra quelle pi� fini della partizione specificata. <p>
     * Il parametro <tt>initPartition</tt> rappresenta la partizione
     * iniziale imposta sugli stati, pu� essere null. <p>
     **/
	<V> Evaluator<S,? extends Set<S>> computeEquivalence(
		Evaluator<S,V> initPartition
	);
	
	/**
     * Calcola la massima bisimulazione. 
     * E' equivalente a una chiamata  
     * <tt>computeEquivalence(null)</tt>. <p>
     **/
	Evaluator<S,? extends Set<S>> computeEquivalence();
	
	/**
     * Controlla se gli stati specificati sono nella stessa 
     * classe di equivalenza rispetto alla bisimulazione 
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
     * Controlla se gli stati specificati sono bisimili.
     * Il metodo pu� risultare pi� veloce di una chiamata
     * a <tt>computeEquivalence()</tt> seguita da un controllo. <p>
     **/
	boolean checkEquivalence(S state1, S state2);

	
	/**
	 * 
	 * @return
	 */
	//TODO: Verificare l'approccio
	HashMap<S, ? extends HashSet<S>> getPartitionMap();
	
}














