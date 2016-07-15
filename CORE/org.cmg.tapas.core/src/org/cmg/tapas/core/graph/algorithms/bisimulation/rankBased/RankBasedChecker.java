package org.cmg.tapas.core.graph.algorithms.bisimulation.rankBased;

// graph

// graph.algorithms

// java.util
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import org.cmg.tapas.core.graph.algorithms.Evaluator;
import org.cmg.tapas.core.graph.algorithms.GraphAnalyzer;
import org.cmg.tapas.core.graph.algorithms.RankInfo;
import org.cmg.tapas.core.graph.algorithms.bisimulation.BisimulationCheckerI;
import org.cmg.tapas.core.graph.algorithms.bisimulation.DecoratedClazz;
import org.cmg.tapas.core.graph.algorithms.bisimulation.Splitter;
import org.cmg.tapas.core.graph.algorithms.bisimulation.SplitterQueue;
import org.cmg.tapas.core.graph.ActionInterface;
import org.cmg.tapas.core.graph.GraphData;


/** 
 * RankBasedChecker implementa i metodi per il calcolo della bisimulazione
 * poggiando sull'algoritmo di Kannelakis-Smolka
 * e sull'ottimizzazione basata sul rank.
 * <p>
 * @param <S> tipo degli stati
 * @param <A> tipo delle azioni
 * 
 * @author Guzman Tierno
 **/
public class RankBasedChecker < 
	S ,
	A extends ActionInterface	
> implements BisimulationCheckerI<S,A> {
	
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
	
	/** Partition. **/
	private Vector<HashSet<DecoratedClazz<S>>> clazzes;	
	
	/** Rank info. **/
	private RankInfo<S,A> rankInfo;
    private int globalMaxRank;

	
// _____________________________________________________________________________	

	/** Costruttore. **/
	public RankBasedChecker(GraphData<S,A> graph) {
		setGraph(graph);
	}

	/**
	 * Setta il grafo da analizzare nel checker.
	 **/
	private void setGraph(GraphData<S,A> graph) {
		this.graph = graph;
		actions = graph.getActions();
	}
		
	// Classe che rappresenta le coppie
	private class Pair<U,V> {
		U u;
		V v;
		Pair(U u,V v) {
			this.u = u;
			this.v = v;
		}			
		@Override 
		public boolean equals(Object o) {
			if( o==null || !(o instanceof Pair))
				return false;
			Pair p = (Pair)o;
			return u.equals(p.u) && v.equals(p.v);
		}	
		@Override 
		public int hashCode() {
			return u.hashCode() + v.hashCode();
		}
	}
	
	/** Calcola la partizione in base al rank ed alla partizione iniziale. **/
	private <V> void computeRankPartition(Evaluator<S,V> evaluator) {		
		HashMap<Pair<Integer,V>,DecoratedClazz<S>> map = 
			new HashMap<Pair<Integer,V>,DecoratedClazz<S>>();
		clazzes = new Vector<HashSet<DecoratedClazz<S>>>( globalMaxRank+2 );	
		for( int i=0; i<=globalMaxRank+1; ++i )
			clazzes.add( new HashSet<DecoratedClazz<S>>() );

		S state;
		DecoratedClazz<S> decorated;
		Iterator<S> iter = graph.getStatesIterator();
		Pair<Integer,V> pair;
		int rank;
		while( iter.hasNext() ) {
			state = iter.next();
			rank = rankInfo.getRank(state);
			pair = new Pair<Integer,V>(rank, evaluator.eval(state));
			decorated = map.get( pair );
			if( decorated==null ) {
				decorated = new DecoratedClazz<S>();
				map.put( pair, decorated );	
				clazzes.get( rank+1 ).add( decorated );
			}							
			decorated.add(state);
			partitionMap.put( state, decorated );
		}
	}

	/** Inserisce i blocchi con rank 'i' i una coda di splitters. **/
	private SplitterQueue<S> initQueue(int rank) {
		SplitterQueue<S> queue = new SplitterQueue<S>();
		for( DecoratedClazz<S> clazz: clazzes.get(rank+1) )
			queue.put( new Splitter<S>(clazz) );
		
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

	private <V> HashMap<S,DecoratedClazz<S>> computeEquivalence(
		Evaluator<S,V> initPartition, S state1, S state2
	) {		
		// init
		partitionMap = new HashMap<S, DecoratedClazz<S>>(
			(int) (graph.getNumberOfStates()/0.75) + 1
		);
		
		// rank
		GraphAnalyzer<S,A> analyzer = new GraphAnalyzer<S,A>(graph);		
		rankInfo = analyzer.computeRank( analyzer.TRANSITIONS_ALL );				
        globalMaxRank = rankInfo.getMaxRank();
		if( initPartition==null ) 
			computeRankPartition(Evaluator.trivialEvaluator(state1));
		else		
			computeRankPartition(initPartition);
		
		List<S> elements;
		List<DecoratedClazz<S>> modifiedClazzes;
		for( int i=-1; i<=globalMaxRank; ++i ) {
			refine(i);
			if( i==globalMaxRank )
				continue;
			for( DecoratedClazz<S> splitter: clazzes.get(i+1) ) {
				elements = splitter.toList();				  
				for( A action: actions ) {
					if( 
						state1!=null && 
						partitionMap.get(state1)!=partitionMap.get(state2) 
					)
						return partitionMap;
					modifiedClazzes = split(i+1, globalMaxRank, action, elements);  
					updateClazzes(modifiedClazzes);			  	
				}
			}				
		}
		
		return partitionMap;
	}


	/** Raffina la partizione al livello di rank specificato. **/
	private void refine(int rank) {
		SplitterQueue<S> queue	= initQueue(rank);
		
		// main looop	
		List<S> elements;
		List<DecoratedClazz<S>> modifiedClazzes;
		Splitter<S> splitter;
		while( !queue.isEmpty() ){
			splitter = queue.get();							  
			elements = splitter.listElements();				  
			for( A action: actions ) {			  			  
				modifiedClazzes = split(rank, rank, action, elements);  
				updateSplittersAndClazzes(queue, modifiedClazzes);			  
			}
		}
	}

	/** 
	 * Esegue lo splitting della partizione tra 
	 * i rank specificati rispetto all'azione specificata 
	 * ed allo splitter i cui elementi sono quelli specificati.
	 **/
	private List<DecoratedClazz<S>> split(
		int minRank,
		int maxRank,
		A action, 
		List<S> elements
	) {
		List<DecoratedClazz<S>> modifiedClazzes = 
			new LinkedList<DecoratedClazz<S>>();
		Set<S> preset;
		DecoratedClazz<S> clazz;
		DecoratedClazz<S> clazz1;
		int rank;
		for( S state: elements ) {
			preset = graph.getPreset(state, action);
			if( preset==null )			
				continue;				
			for( S preState: preset ) {
				rank = rankInfo.getRank(preState);
				if( rank<minRank || rank>maxRank )				
					continue;
					
				clazz = partitionMap.get(preState);
				if( clazz.getSplitter()==null )						
					continue;										
				if( clazz.getSibling1()==null && clazz.size()==1 )	
					continue;										
					
				clazz1 = clazz.getSibling1();						
				if( clazz1==null ) {								
					modifiedClazzes.add(clazz);								
					clazz1 = new DecoratedClazz<S>();				
					clazz.setSibling1(clazz1);						
				}													
				clazz.remove(preState);								
				clazz1.add(preState);								
				partitionMap.put(preState, clazz1);					

			}
		}

		return modifiedClazzes;
	}

	/** Aggiorna la coda degli splitter in base agli splitting avvenuti **/
	private void updateSplittersAndClazzes(
		SplitterQueue<S> queue,
		List<DecoratedClazz<S>> modifiedClazzes
	) {
		DecoratedClazz<S> sibling;
		int rank;
		for( DecoratedClazz<S> clazz: modifiedClazzes ) {
			sibling = clazz.getSibling1();			
			rank = rankInfo.getRank( sibling.iterator().next() );
			if( clazz.isEmpty() ){
				clazz.getSplitter().setClazz( sibling );				
				clazzes.get(rank+1).remove( clazz );
			} else {
				if( clazz.getSplitter().getQueue()!=queue )
					queue.put(clazz.getSplitter());
				queue.put( new Splitter<S>(sibling) );
			}
			clazzes.get(rank+1).add( sibling );
			clazz.detachFromSiblings();
		}
	}

	/** Aggiorna le classi in base agli splitting avvenuti **/
	private void updateClazzes(
		List<DecoratedClazz<S>> modifiedClazzes
	) {
		DecoratedClazz<S> sibling;
		int rank;
		for( DecoratedClazz<S> clazz: modifiedClazzes ) {
			sibling = clazz.getSibling1();			
			rank = rankInfo.getRank( sibling.iterator().next() );
			if( clazz.isEmpty() )
				clazzes.get(rank+1).remove( clazz );

			clazzes.get(rank+1).add( sibling );
			clazz.detachFromSiblings();
		}
	}

	public HashMap<S, ? extends HashSet<S>> getPartitionMap() {
		return partitionMap;
	}

}












