package org.cmg.tapas.core.graph.algorithms.bisimulation.ksopt;



import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.cmg.tapas.core.graph.algorithms.Evaluator;
import org.cmg.tapas.core.graph.algorithms.bisimulation.BisimulationCheckerI;
import org.cmg.tapas.core.graph.ActionInterface;
import org.cmg.tapas.core.graph.GraphData;

/** 
 * KSOChecker implementa i metodi per il calcolo della bisimulazione
 * poggiando sull'algoritmo di Kannelakis-Smolka 
 * e ottimizzandone le prestazioni.
 * <p>
 * @param <S> tipo degli stati
 * @param <A> tipo delle azioni
 * 
 * @author Guzman Tierno
 **/
public class KSOChecker < 
	S ,
	A extends ActionInterface	
> implements BisimulationCheckerI<S,A> {
	
	/**
	 * Grafo da analizzare.
	 **/
	protected GraphData<S,A> graph;

	/**
     * Azioni del grafo.
     **/
	protected Set<A> actions;

	/**
     * Mappa che tiene traccia della suddivisione in classi 
     * dei vari stati del grafo. 
     **/
    protected HashMap<S, KSOClazz<S>> partitionMap = null;
	
// _____________________________________________________________________________	

	/** Costruttore. **/
	public KSOChecker(GraphData<S,A> graph) {
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
	public <V> KSOQueue<S> initQueueAndPartitionMap(
		Evaluator<S,V> evaluator
	) {
		HashMap<V,KSOClazz<S>> map = new HashMap<V,KSOClazz<S>>();
		KSOQueue<S> queue = new KSOQueue<S>();
	
		S state;
		KSOClazz<S> decorated;
		V value;
		Iterator<S> iter = graph.getStatesIterator();
		while( iter.hasNext() ) {
			state = iter.next();
			value = evaluator.eval(state);
			decorated = map.get( value );
			if( decorated==null ) {
				decorated = new KSOClazz<S>();
				map.put( value, decorated );	
				queue.put(decorated);
			}							
			decorated.add(state);
			partitionMap.put( state, decorated );
		}
		
		return queue;
	}

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

	protected <V> HashMap<S, KSOClazz<S>> computeEquivalence(
		Evaluator<S,V> initPartition, S state1, S state2
	) {		
		// reset maps
		partitionMap = new HashMap<S, KSOClazz<S>>(
			(int) (graph.getNumberOfStates()/0.75) + 1
		);
		
		// initialize queue, decorated clazzes and partition map
		KSOQueue<S> queue;
		if( initPartition==null ) 
			queue = initQueueAndPartitionMap(Evaluator.trivialEvaluator(state1));
		else		
			queue = initQueueAndPartitionMap(initPartition);
		
		// main looop	
		List<S> elements;
		List<KSOClazz<S>> clazzes;
		KSOClazz<S> splitter;
		while( !queue.isEmpty() ){
			splitter = queue.get();							 
			elements = splitter.toList();			 
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
	protected List<KSOClazz<S>> split(
		A action, 
		List<S> elements
	) {
		List<KSOClazz<S>> clazzes = new LinkedList<KSOClazz<S>>();
		Set<S> preset;
		KSOClazz<S> clazz;
		KSOClazz<S> clazz1;
		for( S state: elements ) {							  
			preset = graph.getPreset(state, action);		
			if( preset==null )			
				continue;				
			for( S preState: preset ) {	 	
				clazz = partitionMap.get(preState);
				if( clazz.getFlag() )						
					continue;										
				if( clazz.getSibling()==null && clazz.size()==1 )
					continue;
										
				clazz1 = clazz.getSibling();
				if( clazz1==null ) {
					clazzes.add(clazz);								
					clazz1 = new KSOClazz<S>();
					clazz.setSibling(clazz1);					
					clazz1.setFlag(true);
				}
				clazz.remove(preState);								
				clazz1.add(preState);								
				partitionMap.put(preState, clazz1);					
			}
		}

		return clazzes;
	}

	/** Aggiorna la coda degli splitter in base agli splitting avvenuti **/
	protected void updateSplitters(
		KSOQueue<S> queue,
		List<KSOClazz<S>> clazzes
	) {
		KSOClazz<S> sibling;
		for( KSOClazz<S> clazz: clazzes ) {					
			sibling = clazz.getSibling();			
			sibling.setFlag(false);
			if( clazz.isEmpty() ){
				if( queue.remove(clazz) )
					queue.put( sibling );				
			} else {
				if( clazz.getQueue()!=queue )
					queue.put( clazz );
				queue.put( sibling );
			}
			clazz.detachFromSiblings();
		}
	}

	public HashMap<S, ? extends HashSet<S>> getPartitionMap() {
		return partitionMap;
	}
	

	

}












