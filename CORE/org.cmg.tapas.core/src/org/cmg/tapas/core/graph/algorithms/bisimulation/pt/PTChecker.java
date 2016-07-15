package org.cmg.tapas.core.graph.algorithms.bisimulation.pt;

// graph

// graph.algorithms

// java.util
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
 * PTChecker implementa i metodi per il calcolo della bisimulazione
 * poggiando sull'algoritmo di Paige-Tarjan.
 * <p>
 * @param <S> tipo degli stati
 * @param <A> tipo delle azioni
 * 
 * @author Guzman Tierno
 **/
public class PTChecker < 
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
	 * Mappa che tiene traccia dei hitCount.
	 **/
	private HitCountMap<S,A> hitCountMap = null;
	
	/**
     * Mappa che tiene traccia della suddivisione in classi 
     * dei vari stati del grafo. 
     **/
    private HashMap<S, DecoratedClazz<S>> partitionMap = null;
	
// _____________________________________________________________________________	
// Constructor and setters

	/** Costruttore. **/
	public PTChecker(GraphData<S,A> graph) {
		setGraph(graph);
	}

	/**
	 * Setta il grafo da analizzare nel checker.
	 **/
	private void setGraph(GraphData<S,A> graph) {
		this.graph = graph;
		this.actions = graph.getActions();
	}
// _____________________________________________________________________________	
// initializers

	/** Resets HitCountMap and partitionMap **/
	private void resetMaps() {
		hitCountMap = new HitCountMap<S,A>();
		partitionMap = new HashMap<S, DecoratedClazz<S>>(
			(int) (graph.getNumberOfStates()/0.75) + 1
		);
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
// methods that computes bisimulation

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
		resetMaps();
		
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
		boolean simple;
		
		// while there are splitters
		while( !queue.isEmpty() ){
			splitter = queue.get();							  
			simple = !splitter.hasSons();
			
			// elements			
			if( simple )
				elements = splitter.listElements();			
			else
				elements = splitter.getSmallestChild().listElements();			
			
			// for each action
			for( A action: actions ) {			  				
				// fast check
				if( 
					state1!=null && 
					partitionMap.get(state1)!=partitionMap.get(state2) 
				)
					return partitionMap;
				// hit counts
				computeHitCounts(action, splitter, elements, simple); 
				// split				
				if( simple )	
					clazzes = simpleSplit(action, elements);  
				else
					clazzes = compoundSplit(action, splitter, elements);  					
				// update
				updateSplitters(queue, clazzes);			  
			}
			// adjust
			if( !simple )
				adjustSplitter(splitter);
		}

		// clean and return
		hitCountMap = null;		
		return partitionMap;		
	}


// _____________________________________________________________________________	
// compute hit counts

	/** 
	 * Nel caso di splitter semplice
	 * calcola i hitCount per l'azione specificata e per lo splitter 
	 * specificato assumendo che i suoi elementi siano quelli specificati.
	 * Nel caso di splitter composto
	 * calcola i hitCount per l'azione specificata e per il figlio piccolo
	 * dello splitter specificato assumendo che 
	 * gli elementi del figlio piccolo siano quelli specificati.
	 **/
	private void computeHitCounts(
		A action, 
		Splitter<S> splitter, 
		List<S> elements,
		boolean simple		
	) {
		Splitter<S> splitter1 = simple? splitter : splitter.getSmallestChild();
			
		for( S state: elements ) {		
			Set<S> list = graph.getPreset(state, action);
			if( list==null )
				continue;
			for( S preState: list )	{	
				hitCountMap.increaseHitCount(preState, action, splitter1);							
			}
		}	
	}	
	
// _____________________________________________________________________________	
// splitting

	/** 
	 * Splitter semplice.
	 * Esegue lo splitting della partizione attuale rispetto
	 * all'azione specificata ed allo splitter semplice specificato
	 * assumendo che i suoi elementi siano quelli specificati.
	 **/
	private List<DecoratedClazz<S>> simpleSplit(
		A action, 
		List<S> elements
	) {
		HashSet<S> movedStates = new HashSet<S>();		
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
				if( clazz.getSibling1()==null && clazz.size()==1 )
					continue;	
				if( !movedStates.contains(preState) ) {
					clazz1 = clazz.getSibling1();
					if( clazz1==null ) {
						clazzes.add(clazz);								
						clazz1 = new DecoratedClazz<S>();
						clazz.setSibling1(clazz1);
					}
					clazz.remove(preState);								
					clazz1.add(preState);		
					partitionMap.put(preState, clazz1);	
					movedStates.add(preState);
				}				
			}
		}

		return clazzes;
	}

	/** 
	 * Splitter composto.
	 * Esegue lo splitting della partizione attuale rispetto
	 * all'azione specificata ed al figlio piccolo dello splitter specificato
	 * assumendo che gli elementi di tale figlio siano quelli specificati.
	 **/
	private List<DecoratedClazz<S>> compoundSplit(
		A action, 
		Splitter<S> splitter,
		List<S> elements
	) {
		// smallest child of the splitter
		Splitter<S> splitter1 = splitter.getSmallestChild();
		// set of states moved by the splitting
		HashSet<S> movedStates = new HashSet<S>();	
		// list of clazzes interested by the splitting
		List<DecoratedClazz<S>> clazzes = new LinkedList<DecoratedClazz<S>>();
		// preset of the generic state in the splitter
		Set<S> preset;
		// clazz of a generic point in the preset
		DecoratedClazz<S> clazz;
		// siblings of the previous clazz
		DecoratedClazz<S> clazz1;
		DecoratedClazz<S> clazz2;		
		// hit counts for the children of the splitter
		int hit1;
		int hit2;
		
		for( S state: elements ) {		// for each element in the small child
			preset = graph.getPreset(state, action);
			if( preset==null )			
				continue;				
			for( S preState: preset ) {	// for each state in the preset 
				clazz = partitionMap.get(preState);
				if( clazz.getSibling1()==null && clazz.size()==1 )
					continue;
				if( !movedStates.contains(preState) ) { // if not moved
					// get hit counts for smallest child and brother
					hit1 = hitCountMap.getHitCount(preState, action, splitter1 );							
					hit2 = hitCountMap.getHitCount(preState, action, splitter );		
					hit2 -= hit1;
					
					// get clazz of the preState and its siblings
					clazz1 = clazz.getSibling1();
					clazz2 = clazz.getSibling2();
					// create siblings if necessary and record clazz as touched
					if( clazz1==null ) {
						clazzes.add(clazz);								
						clazz1 = new DecoratedClazz<S>();
						clazz2 = new DecoratedClazz<S>();
						clazz.setSibling1(clazz1);
						clazz.setSibling2(clazz2);
					}
					
					// move preState to appropriate sibling
					if( hit2==0 ) {				// hits olny smallest splitter
						clazz.remove(preState);								
						clazz1.add(preState);		
						partitionMap.put(preState, clazz1);	
					} else {					// hits both splitters
						clazz.remove(preState);								
						clazz2.add(preState);		
						partitionMap.put(preState, clazz2);	
					}
					movedStates.add(preState);
										
					// set hit2 as being the hitCount for the father
					hitCountMap.setHitCount(
						preState, action, splitter, hit2 
					);
				}				
			}
		}
		
		return clazzes;
	}

// _____________________________________________________________________________	
// updating

	/** Aggiorna la coda degli splitter in base agli splitting avvenuti **/
	private void updateSplitters(
		SplitterQueue<S> queue,
		List<DecoratedClazz<S>> clazzes
	) {
		Splitter<S> owner;
		for( DecoratedClazz<S> clazz: clazzes ) {						
			// get the splitter owning the clazz, check its simplicity
			// and get the root of the tree to which owner belongs. 
			owner = clazz.getSplitter();
			
			// convert clazz and siblings in a tree of splitter 
			// rooted at owner
			toSplitterTree(clazz);
			
			// if owner is still simple no splitting happened
			if( !owner.hasSons() ) 
				continue;
				
			// if owner was not in the splitters' queue: put it in
			if( owner.getFather()==null && owner.getQueue()==null ) {
				queue.put( owner );
			}
			// if owner was simple and it was in the splitters' queue:
			// remove it and add its leaves to the queue
			else if( 
				owner.getFather()==null && 
				owner.getQueue()!=null &&
				owner.hasSons()
			) {						
				//hitCountMap.removeCounts(owner);
				queue.remove(owner);
				putLeavesInQueue(owner, queue);
			}
			// if owner was a leaf of a tree in the splitters' queue:
			// it already got extended
		}
	}

	/** Converte la classe e le sorelle in un albero di splitter. **/
	private void toSplitterTree(DecoratedClazz<S> clazz) {
		DecoratedClazz<S> clazz1 = clazz.getSibling1();
		DecoratedClazz<S> clazz2 = clazz.getSibling2();
		List<DecoratedClazz<S>> nonEmpty = new LinkedList<DecoratedClazz<S>>();
		if(!clazz.isEmpty() )
			nonEmpty.add(clazz);
		if(!clazz1.isEmpty() )
			nonEmpty.add(clazz1);
		if(clazz2!=null && !clazz2.isEmpty() )
			nonEmpty.add(clazz2);
			
		int size = nonEmpty.size();
		if( size==1 )
			toSplitterTree1( clazz.getSplitter(), nonEmpty );
		else if( size==2 )
			toSplitterTree2( clazz.getSplitter(), nonEmpty );
		else // size==3
			toSplitterTree3( clazz.getSplitter(), nonEmpty );
		
		clazz.detachFromSiblings();
	}

	private void toSplitterTree1(
		Splitter<S> splitter,
		List<DecoratedClazz<S>> nonEmpty
	) {
		splitter.setClazz(nonEmpty.get(0));				
	}

	private void toSplitterTree2(
		Splitter<S> splitter,
		List<DecoratedClazz<S>> nonEmpty
	) {
		splitter.setClazz( null );
		Iterator<DecoratedClazz<S>> iterator = nonEmpty.iterator();
		Splitter<S> splitter1 = new Splitter<S>( iterator.next() );
		Splitter<S> splitter2 = new Splitter<S>( iterator.next() );
		splitter.setChildren( splitter1, splitter2 ); 
	}

	private void toSplitterTree3(
		Splitter<S> splitter,
		List<DecoratedClazz<S>> nonEmpty
	) {
		splitter.setClazz( null );
		Iterator<DecoratedClazz<S>> iterator = nonEmpty.iterator();
		Splitter<S> splitter1 = new Splitter<S>( iterator.next() );
		Splitter<S> splitter2 = new Splitter<S>( iterator.next() );
		Splitter<S> splitter3 = new Splitter<S>( iterator.next() );
		Splitter<S> splitterM = new Splitter<S>( );
		splitterM.setChildren( splitter2, splitter3 );
		splitter.setChildren( splitter1, splitterM );
	}
	
	/** 
	 * Mette le foglie dello splitter specificato nella coda,
	 * staccando le foglie dall'albero.
	 * Assume che lo splitter specificato abbia dei figli.
	 **/
	private void putLeavesInQueue(
		Splitter<S> splitter, 
		SplitterQueue<S> queue
	) {
		// OPTION: evitare putLeavesInQueue quando possibile
		Splitter<S> child1 = splitter.getChild1();
		Splitter<S> child2 = splitter.getChild2();
		if( child1!=null ) { // in this case, also child2 is non null
			splitter.setChildren(null, null);
			putLeavesInQueue( child1, queue );
			putLeavesInQueue( child2, queue );
		} else {
			queue.put(splitter);
		}
	}
		
	/**
	 * Sostituisce il figlio maggiore di uno splitter con lo splitter
	 * stesso. Questo aggiusta i hitCount memorizzati durante split().
	 **/
	private void adjustSplitter(Splitter<S> splitter) {
		Splitter<S> splitter2 = splitter.getBiggestChild();
		splitter.setChildren( null, null );
		if( !splitter2.hasSons() ) {
			splitter.setClazz( splitter2.getClazz() );			
		} else {
			splitter.setChildren(splitter2.getChild1(), splitter2.getChild2());
		}
		SplitterQueue<S> queue = splitter2.getQueue();
		if( queue!=null ) {			
			queue.remove(splitter2);
			queue.put(splitter);
		}
	}

	public HashMap<S, ? extends HashSet<S>> getPartitionMap() {
		return partitionMap;
	}
	
	

}












