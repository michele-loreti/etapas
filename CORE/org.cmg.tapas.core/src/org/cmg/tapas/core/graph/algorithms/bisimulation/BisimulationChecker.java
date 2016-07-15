package org.cmg.tapas.core.graph.algorithms.bisimulation;


import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.cmg.tapas.core.graph.algorithms.Evaluator;
import org.cmg.tapas.core.graph.algorithms.bisimulation.ks.KSChecker;
import org.cmg.tapas.core.graph.algorithms.bisimulation.ksopt.KSOChecker;
import org.cmg.tapas.core.graph.algorithms.bisimulation.ksopt.KSOCheckerForTrace;
import org.cmg.tapas.core.graph.algorithms.bisimulation.mpt.MPTChecker;
import org.cmg.tapas.core.graph.algorithms.bisimulation.pt.PTChecker;
import org.cmg.tapas.core.graph.algorithms.bisimulation.rankBased.RankBasedChecker;
import org.cmg.tapas.core.graph.ActionInterface;
import org.cmg.tapas.core.graph.GraphComposition;
import org.cmg.tapas.core.graph.GraphData;

/** 
 * BisimulationChecker offre i metodi per il calcolo
 * della bisimulazione su grafi.
 * <p>
 * @param <S> tipo degli stati
 * @param <A> tipo delle azioni
 * 
 * @author Guzman Tierno
 **/
public class BisimulationChecker <
	S ,
	A extends ActionInterface	
> implements BisimulationCheckerI<S,A> {
	
	// Motore
	private BisimulationCheckerI<S,A> checker;
	
	// Motori disponibili
	/** 
	 * Algoritmo di Paige-Tarjan. <p> 
     * Costo: medio O(m*log(n)), peggiore O(m*log(n)). <p>
     * Risulta pi� veloce di Kannelakis-Smolka per grafi
     * con varie migliaia di nodi e senza troppe azioni.
	 **/
	public static final int PT = 0;	
	/** 
	 * Algoritmo di Kannelakis-Smolka. <p>
     * Costo: medio O(m*log(n)), peggiore O(m*n). <p>
     * E' l'algoritmo default.
	 **/
	public static final int KS = 1;	
    /** 
     * Algoritmo di Kannelakis-Smolka seconda variante. <p> 
     * Costo: medio O(m*log(n)), peggiore O(m*n). <p>
     * Si tratta di una ottimizzazione dell'algoritmo
     * di Kannelakis-Smolka. <p>
     * E' l'algoritmo default.
     **/
    public static final int KS2 = 2;  
    /** 
     * Algoritmo di Kannelakis-Smolka seconda variante. <p> 
     * Costo: medio O(m*log(n)), peggiore O(m*n). <p>
     * Si tratta di una ottimizzazione dell'algoritmo
     * di Kannelakis-Smolka. <p>
     * SERVE PER IL CALCOLO DEL CONTRO ESEMPIO SULLE TRACCE.
     **/
    public static final int KS2T = 22;   
	/** 
	 * Algoritmo basato sul Rank. <p>
     * Costo: medio O(m*log(n)), peggiore O(m*n). <p>
	 * E' un algoritmo sperimentale che � risultato 
     * averer prestazioni mediocri.
	 **/
	public static final int RANK_BASED = 3;	
	/**
	 * Algoritmo di Paige-Tarjan con splitting multiplo. <p>
     * Costo: medio O(m*log(n)), peggiore O(m*log(n)). <p>
     * E' esperimento che risulta essere molto peggiore di PT. 
	 **/
	public static final int MPT = 4;	

	// Default checker
	private static final int DEFAULT_CHECKER = KS2;
	
	/** 
	 * Costruisce un BisimulationChecker per il grafo specificato.
	 **/
	public BisimulationChecker(GraphData<S,A> graph) {
		this(graph, DEFAULT_CHECKER);
	}
	
	/** 
	 * Costruisce un BisimulationChecker per il grafo specificato
	 * che usa l'algoritmo specificato. Gli algoritmi disponibili sono:
	 * <ul>
	 * 
     * <li>
	 * <tt>PT</tt>: (Paige-Tarjan) <p> 
     * Costo: medio O(m*log(n)), peggiore O(m*log(n)). <p>
     * Risulta pi� veloce di Kannelakis-Smolka per grafi
     * con varie migliaia di nodi e senza troppe azioni.
	 * </li>
	 * 
     * <tt>MPT</tt>: (Paige-Tarjan multiplo) 
     * Algoritmo di Paige-Tarjan con splitting multiplo. <p>
     * Costo: medio O(m*log(n)), peggiore O(m*log(n)). <p>
     * E' esperimento che risulta essere molto peggiore di PT. 
	 * </li>
     * 
	 * <li>
	 * <tt>KS2</tt>: (Kannelakis-Smolka 2)
     * Algoritmo di Kannelakis-Smolka seconda variante. <p> 
     * Costo: medio O(m*log(n)), peggiore O(m*n). <p>
     * Era pensato per ottimizzare KS ma le sue
     * prestazioni sono del tutto simili a quelle di KS. 
	 * </li>
     * 
	 * <li>
     * Algoritmo di Kannelakis-Smolka. <p>
     * Costo: medio O(m*log(n)), peggiore O(m*n). <p>
     * E' l'algoritmo default.
	 * </li>
     * 
	 * <li>
	 * <tt>RANK_BASED</tt>: (RankBased Checker)
     * Costo: medio O(m*log(n)), peggiore O(m*n). <p>
     * E' un algoritmo sperimentale che � risultato 
     * averer prestazioni mediocri.
	 * </li> 
	 * </ul>
	 **/
	public BisimulationChecker(GraphData<S,A> graph, int algorithm) {
		switch(algorithm) {
			case KS2: 
				checker = new KSOChecker<S,A>(graph); 
				break;
			case KS2T: 
				checker = new KSOCheckerForTrace<S,A>(graph); 
				break;
			case KS: 
				checker = new KSChecker<S,A>(graph); 
				break;
			case RANK_BASED: 
				checker = new RankBasedChecker<S,A>(graph); 
				break;
			case MPT: 
				checker = new MPTChecker<S,A>(3, graph);  
				break;
            case PT: 
				checker = new PTChecker<S,A>(graph); 
				break;
		}
	}	

	/**
	 * Costruisce un BisimulationChecker per il grafo 
	 * ottenuto dalla composizione dei grafi specificati.
	 * Usa l'algoritmo di default.
	 **/
	public BisimulationChecker(
		GraphData<S,A> graph1, 
		GraphData<S,A> graph2		
	) {
		this( new GraphComposition<S,A>(graph1, graph2), DEFAULT_CHECKER );
	}

	
	/**
     * Calcola la massima bisimulazione.
     **/
	public Evaluator<S, ? extends Set<S>> computeEquivalence() {
		return checker.computeEquivalence();
	}
	

	/**
     * Calcola la bisimulazione meno fine tra quelle pi� fini
     * della partizione specificata.
     * La partizione iniziale pu� essere null
     * per inidicare la partizione composta da una sola classe.
     **/ 
	public <V> Evaluator<S, ? extends Set<S>> computeEquivalence(
		Evaluator<S,V> initPartition
	) {		
		return checker.computeEquivalence(initPartition);
	}

	/**
     * Controlla se gli stati specificati sono nella stessa 
     * classe di equivalenza rispetto alla bisimulazione.
     * Il metodo pu� risultare pi� veloce di una chiamata
     * a <tt>computeEquivalence()</tt> seguita da un controllo.
     **/
	public boolean checkEquivalence(S state1, S state2) {
		if( state1==state2 )
			return true;

		return checker.checkEquivalence(state1, state2);
	}

	/**
     * Controlla se gli stati specificati sono nella stessa 
     * classe di equivalenza rispetto alla bisimulazione 
     * meno fine tra quelle pi� fini della partizione specificata.
     * Il metodo pu� risultare pi� veloce di una chiamata
     * a <tt>computeEquivalence()</tt> seguita da un controllo.
     * Il parametro <tt>initPartition</tt> rappresenta la partizione
     * iniziale imposta sugli stati, pu� essere null.
     **/
	public <V> boolean checkEquivalence(
		Evaluator<S,V> initPartition, S state1, S state2
	) {
		return checker.checkEquivalence(initPartition, state1, state2);
	}

	public HashMap<S, ? extends HashSet<S>> getPartitionMap() {
		return checker.getPartitionMap();
	}
	

}














