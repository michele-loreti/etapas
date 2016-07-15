package org.cmg.tapas.core.graph.algorithms.bisimulation.ks;



import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.cmg.tapas.core.graph.algorithms.Evaluator;
import org.cmg.tapas.core.graph.algorithms.bisimulation.BisimulationCheckerI;
import org.cmg.tapas.core.graph.algorithms.bisimulation.DecoratedClazz;
import org.cmg.tapas.core.graph.algorithms.bisimulation.Splitter;
import org.cmg.tapas.core.graph.algorithms.bisimulation.SplitterQueue;
import org.cmg.tapas.core.graph.ActionInterface;
import org.cmg.tapas.core.graph.GraphData;

/** 
 * KSChecker implementa i metodi per il calcolo della bisimulazione
 * poggiando sull'algoritmo di Kannelakis-Smolka.
 * <p>
 * @param <S> tipo degli stati
 * @param <A> tipo delle azioni
 * 
 * @author Guzman Tierno
 **/
public class KSChecker < 
	S,
	A extends ActionInterface> implements BisimulationCheckerI<S,A> {
	/**
	 * Grafo da analizzare.
	 **/
	private GraphData<S,A> graph;

	/**
     * Azioni del grafo.
     **/
	private Set<A> actions;

	/**
     * Mappa che tiene traccia della suddivisione in classi 
     * dei vari stati del grafo. 
     **/
    private HashMap<S, DecoratedClazz<S>> partitionMap = null;
	
// _____________________________________________________________________________	

	/** Costruttore. **/
	public KSChecker(GraphData<S,A> graph) {
		setGraph(graph);
	}

	/**
	 * Setta il grafo da analizzare nel checker.
	 **/
	private void setGraph(GraphData<S,A> graph) {
		this.graph = graph;
		actions = graph.getActions();
	}
		
	/** Inizializes the splitters' queue and the partition map. **/
	public <V> SplitterQueue<S> initQueueAndPartitionMap(
		Evaluator<S,V> evaluator
	) {
		HashMap<V,DecoratedClazz<S>> map = new HashMap<V,DecoratedClazz<S>>();
		Splitter<S> splitter;
		SplitterQueue<S> queue = new SplitterQueue<S>();
	
		S state;
		DecoratedClazz<S> decorated;
		V value;
		Iterator<S> iter = graph.getStatesIterator();
		while( iter.hasNext() ) {
			state = iter.next();
			value = evaluator.eval(state);
			decorated = map.get( value );
			if( decorated==null ) {
				decorated = new DecoratedClazz<S>();
				map.put( value, decorated );	
				splitter = new Splitter<S>(decorated);
				queue.put(splitter);
			}							
			decorated.add(state);
			partitionMap.put( state, decorated );
		}
		
		return queue;
	}

// _____________________________________________________________________________	

	/**
     * Calcola la massima bisimulazione.
     **/
	public Evaluator<S,? extends Set<S>> computeEquivalence() {
		return computeEquivalence(null);
	}
	
	/**
     * Controlla se gli stati specificati sono nella stessa 
     * classe di equivalenza rispetto alla bisimulazione.
     * Il metodo pu� risultare pi� veloce di una chiamata
     * a <tt>computeEquivalence()</tt> seguita da un controllo.
     **/
	public boolean checkEquivalence(S state1, S state2) {
		return checkEquivalence(null, state1, state2);
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
		computeEquivalence(initPartition, state1, state2);
		return partitionMap.get(state1)==partitionMap.get(state2);
	}
	
	/**
     * Calcola la bisimulazione meno fine tra quelle pi� fini
     * della partizione specificata.
     * La partizione iniziale pu� essere null
     * per inidicare la partizione composta da una sola classe.
     **/ 
	public <V> Evaluator<S,? extends Set<S>> computeEquivalence(
		Evaluator<S,V> initPartition
	) {		
		return Evaluator.fromMap( 
			computeEquivalence(initPartition, null, null)
		);
	}

	private <V> HashMap<S,DecoratedClazz<S>> computeEquivalence(
		Evaluator<S,V> initPartition, S state1, S state2
	) {		
		// reset maps	
		partitionMap = new HashMap<S, DecoratedClazz<S>>(
			(int) (graph.getNumberOfStates()/0.75) + 1
		);
		
		// initialize queue, decorated clazzes and partition map
		SplitterQueue<S> queue;
		if( initPartition==null ) 
			queue = initQueueAndPartitionMap(Evaluator.trivialEvaluator(state1));
		else		
			queue = initQueueAndPartitionMap(initPartition);
		
		// main looop	
		List<S> elements;
		List<DecoratedClazz<S>> clazzes;
		Splitter<S> splitter;
		while( !queue.isEmpty() ){
			splitter = queue.get();
			elements = splitter.listElements();		
			for( A action: actions ) {			  			  
				if( 
					state1!=null && 
					partitionMap.get(state1)!=partitionMap.get(state2) 
				)
					return partitionMap;
				clazzes = split(action, elements); 			  
				updateSplitters(queue, clazzes);			  
			}
		}

		return partitionMap;
	}

	/** 
	 * Esegue lo splitting della partizione attuale rispetto
	 * all'azione specificata ed allo splitter specificato
	 * assumendo che i suoi elementi siano quelli specificati.
	 **/
	private List<DecoratedClazz<S>> split(
		A action, 
		List<S> elements
	) {
		List<DecoratedClazz<S>> clazzes = new LinkedList<DecoratedClazz<S>>();
		Set<S> preset;
		DecoratedClazz<S> clazz;
		DecoratedClazz<S> clazz1;
		for( S state: elements ) {							  
			preset = graph.getPreset(state, action);		
			if( preset==null )			
				continue;				
			for( S preState: preset ) {	 		
				clazz = partitionMap.get(preState);
				if( clazz.getSplitter()==null )						
					continue;										
				if( clazz.getSibling1()==null && clazz.size()==1 )	
					continue;										
					
				clazz1 = clazz.getSibling1();						
				if( clazz1==null ) {								
					clazzes.add(clazz);								
					clazz1 = new DecoratedClazz<S>();				
					clazz.setSibling1(clazz1);						
				}													
				clazz.remove(preState);								
				clazz1.add(preState);								
				partitionMap.put(preState, clazz1);					
			}
		}

		return clazzes;
	}

	/** Aggiorna la coda degli splitter in base agli splitting avvenuti **/
	private void updateSplitters(
		SplitterQueue<S> queue,
		List<DecoratedClazz<S>> clazzes
	) {
		DecoratedClazz<S> sibling;
		for( DecoratedClazz<S> clazz: clazzes ) {						
			sibling = clazz.getSibling1();			
			if( clazz.isEmpty() ){
				clazz.getSplitter().setClazz( sibling );				
			} else {
				if( clazz.getSplitter().getQueue()!=queue )
					queue.put(clazz.getSplitter());
				queue.put( new Splitter<S>(sibling) );
			}
			clazz.detachFromSiblings();
		}
	}

	public HashMap<S, ? extends HashSet<S>> getPartitionMap() {
		return partitionMap;
	}
	
}












