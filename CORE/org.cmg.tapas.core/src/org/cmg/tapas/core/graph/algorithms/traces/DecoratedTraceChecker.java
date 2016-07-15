package org.cmg.tapas.core.graph.algorithms.traces;

import org.cmg.tapas.core.graph.algorithms.AcceptanceBuilder;
import org.cmg.tapas.core.graph.algorithms.AcceptanceState;
import org.cmg.tapas.core.graph.algorithms.EquivalenceChecker;
import org.cmg.tapas.core.graph.algorithms.Evaluator;
import org.cmg.tapas.core.graph.algorithms.GraphAnalyzer;
import org.cmg.tapas.core.graph.algorithms.bisimulation.BisimulationChecker;
import org.cmg.tapas.core.graph.ActionInterface;
import org.cmg.tapas.core.graph.GraphComposition;
import org.cmg.tapas.core.graph.GraphData;
import org.cmg.tapas.core.graph.GraphInterface;
import org.cmg.tapas.core.graph.PointedGraph;



/** 
 * DecoratedTraceChecker offre i metodi per verificare le seguenti  
 * equivalenze: <p>
 * <ul>
 *		<li> Tracce </li>
 *		<li> Tracce deboli (May) </li>
 *		<li> Tracce complete </li>
 *		<li> Tracce deboli complete </li>
 *		<li> Tracce sensibili alla divergenza </li>
 *		<li> Tracce complete sensibili alla divergenza </li>
 *		<li> Must </li>
 *		<li> Must forte </li>
 *		<li> Testing </li>
 *		<li> Testing forte </li>
 * </ul>
 * <p>
 * E' anche possibile testare alcune equivalenze esotiche
 * come Tracce forti sensibili alla divergenza ed altre ancora.
 * <p>
 * @param <S> tipo degli stati
 * @param <A> tipo delle azioni
 * 
 * @author Guzman Tierno
 **/
public class DecoratedTraceChecker <
	S ,
	A extends ActionInterface	
> implements EquivalenceChecker<S,A> {

	/** Flag per le equivalenze deboli. **/
	public static final int WEAK = AcceptanceBuilder.WEAK;
	/** Flag per le equivalenze sensibili alla convergenza. **/
	public static final int CONV = AcceptanceBuilder.CONV;
	/** Flag per le equivalenze sensibili agli stati finali. **/
	public static final int FINL = AcceptanceBuilder.FINL;
	/** Flag per le equivalenze sensibili agli insiemi di accettazione. **/
	public static final int ACPT = AcceptanceBuilder.ACPT;
	/** Flag per le equivalenze con storia della divergenza. **/
	public static final int HIST = AcceptanceBuilder.HIST;
	/** Flag per le equivalenze che tagliano i rami divergenti. **/
	public static final int CUTC = AcceptanceBuilder.CUTC;


	/** Tracce: nessun flags **/
	public static final int TRACE     = 0;
	/** Tracce deboli - May: <code>WEAK</code> **/
	public static final int TRACE_W   = WEAK;
	
	/** Tracce complete: <code>FINL</code> **/
	public static final int TRACE_C   = FINL;
	/** Tracce deboli complete: <code>WEAK + FINL</code> **/
	public static final int TRACE_WC  = FINL + WEAK;
	
	/** Tracce sensibili alla divergenza: <code>CONV</code> **/
	public static final int TRACE_D   = CONV;
	/** Tracce deboli sensibili alla divergenza: <code>WEAK + CONV</code> **/
	public static final int TRACE_WD  = CONV + WEAK;
	
	/** Tracce complete sensibili alla divergenza: <code>FINL + CONV</code> **/
	public static final int TRACE_CD  = FINL + CONV;
	/** 
	 * Tracce deboli complete sensibili alla divergenza:  
	 * <code>WEAK + FINL + CONV</code>. 
	 **/
	public static final int TRACE_WCD = FINL + CONV + WEAK;
	
	/** Must forte:  <code>ACPT</code> **/
	public static final int MUST      = ACPT;
	/** Must: <code>WEAK + CONV + ACPT + CUTC + HIST</code> **/
	public static final int MUST_W    = ACPT + CONV + HIST + CUTC + WEAK;
	
	/** Testing forte: <code>CONV + ACPT + HIST</code> **/
	public static final int TESTING   = CONV + ACPT + HIST;
	/** Testing:  <code>WEAK + CONV + ACPT + HIST</code> **/
	public static final int TESTING_W = CONV + ACPT + HIST + WEAK;
	
	
	// Private data
	// bit flags che definiscono l'equivalenza
	private int equivalence;
	// grafo da analizzare
	private GraphData<S,A> graph;


	/** 
	 * Costruisce un <code>DecoratedTraceChecker</code> per il grafo specificato
	 * e per l'equivalenza specificata.
	 * Le equivalenze specificate sono quelle indicate
	 * nelle costanti della classe.
	 **/
	public DecoratedTraceChecker(
		GraphData<S,A> graph, 
		int equivalence
	) {		
		this.graph = graph;
		this.equivalence = equivalence;
	}
	
	/** 
	 * Costruisce un <code>DecoratedTraceChecker</code> per i grafi specificati
	 * e per l'equivalenza specificata.
	 * Le equivalenze specificate sono quelle indicate
	 * nelle costanti della classe.
	 **/
	public DecoratedTraceChecker(
		GraphData<S,A> graph1, 
		GraphData<S,A> graph2, 
		int equivalence
	) {
		this(new GraphComposition<S,A>(graph1,graph2), equivalence);
	}
	
	/**
     * Controlla se gli stati specificati sono nella stessa 
     * classe di equivalenza rispetto all'equivalenza
     * del tipo fissato.
     **/
	public boolean checkEquivalence(S state1, S state2) {
		GraphAnalyzer<S,A> analyzer = new GraphAnalyzer<S,A>(graph);
		PointedGraph<AcceptanceState<A>, A> pointedGraph = 
			analyzer.buildAssociatedGraph( state1, state2, equivalence );
		
		GraphInterface<AcceptanceState<A>,A> newGraph = pointedGraph.getGraph();
		AcceptanceState<A> newState1 = pointedGraph.getState1();
		AcceptanceState<A> newState2 = pointedGraph.getState2();		
		
		BisimulationChecker<AcceptanceState<A>,A> bisim = 
			new BisimulationChecker<AcceptanceState<A>,A>(newGraph
					, BisimulationChecker.KS2T);
						
		return bisim.checkEquivalence(
			Evaluator.fromLabelledState(newState1), newState1, newState2
		);
	}

}










