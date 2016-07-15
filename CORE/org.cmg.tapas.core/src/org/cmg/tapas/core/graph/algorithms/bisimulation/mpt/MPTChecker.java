package org.cmg.tapas.core.graph.algorithms.bisimulation.mpt;

// graph

// graph.algorithms

// java.util
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

import org.cmg.tapas.core.graph.algorithms.Evaluator;
import org.cmg.tapas.core.graph.algorithms.bisimulation.BisimulationCheckerI;
import org.cmg.tapas.core.graph.ActionInterface;
import org.cmg.tapas.core.graph.GraphData;


/** 
 * MPTChecker implementa i metodi per il calcolo della bisimulazione
 * poggiando sull'algoritmo di Paige-Tarjan multiplo.
 * <p>
 * @param <S> tipo degli stati
 * @param <A> tipo delle azioni
 * 
 * @author Guzman Tierno
 **/
public class MPTChecker < 
	S ,
	A extends ActionInterface	
> implements BisimulationCheckerI<S,A> {
	
    /** Numero massimo di figli di uno splitter. **/
    public final int CHILDREN;
    /** Numero massimo di classi sorelle. **/
    public final int SIBLINGS;

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
	private MPTHitCountMap<S,A> hitCountMap = null;
	
	/**
     * Mappa che tiene traccia della suddivisione in classi 
     * dei vari stati del grafo. 
     **/
    private HashMap<S, MPTClazz<S>> partitionMap = null;
	
// _____________________________________________________________________________	
// Constructor and setters

	/** Costruttore. **/
	public MPTChecker(int CHILDREN, GraphData<S,A> graph) {
		this.CHILDREN = CHILDREN;
		this.SIBLINGS = (1<<CHILDREN)-1;
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
		hitCountMap = new MPTHitCountMap<S,A>();
		partitionMap = new HashMap<S, MPTClazz<S>>( 
			(int) (graph.getNumberOfStates()/0.75) + 1
		);	
	}	

	/** Inizializes the splitters' queue and the partition map. **/
	public <V> MPTQueue<S> initQueueAndPartitionMap(
		Evaluator<S,V> evaluator
	) {
		HashMap<V,MPTClazz<S>> map = new HashMap<V,MPTClazz<S>>();
		MPTSplitter<S> splitter;
		MPTQueue<S> queue = new MPTQueue<S>();
	
		S state;
		MPTClazz<S> decorated;
		V value;
		Iterator<S> iter = graph.getStatesIterator();
		while( iter.hasNext() ) {
			state = iter.next();
			value = evaluator.eval(state);
			decorated = map.get( value );
			if( decorated==null ) {
				decorated = new MPTClazz<S>(SIBLINGS);
				map.put( value, decorated );	
				splitter = new MPTSplitter<S>(CHILDREN, decorated);
				queue.put(splitter);
			}							
			decorated.add(state);
			decorated.getSplitter().addToSize(1);
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

	private <V> HashMap<S, MPTClazz<S>> computeEquivalence(
		Evaluator<S,V> initPartition, S state1, S state2
	) {		
		// reset maps
		resetMaps();
		
		// initialize queue, decorated clazzes and partition map
		MPTQueue<S> queue;
		if( initPartition==null ) 
			queue = initQueueAndPartitionMap(Evaluator.trivialEvaluator(state1));
		else		
			queue = initQueueAndPartitionMap(initPartition);
		
		// main looop	
        List<S> elements = null;
        List<List<S>> elements2 = null;
		List<MPTClazz<S>> clazzes;
		MPTSplitter<S> splitter;
		boolean simple;
		
		// while there are splitters
		while( !queue.isEmpty() ){
			splitter = queue.get();							  
			simple = !splitter.hasSons();
			
			// elements			
			if( simple ) {
				elements = splitter.listElements();			
            } else {
                elements2 = new LinkedList<List<S>>();	// last excluded
                Iterator<MPTSplitter<S>> iter = splitter.getChildren().iterator();                
                for( int i=0; i<splitter.getChildren().size()-1; ++i  )                	
                    elements2.add( iter.next().listElements() );
            }
			
			// for each action
			for( A action: actions ) {			  				
				// fast check
				if( 
					state1!=null && 
					partitionMap.get(state1)!=partitionMap.get(state2) 
				)
					return partitionMap;
                
				// hit counts
                if( simple )
                    computeSimpleHitCounts(action, splitter, elements );
                else 
                    computeCompoundHitCounts(action, splitter, elements2 );
                
				// split				
				if( simple )	
					clazzes = simpleSplit(action, elements);  
				else
					clazzes = compoundSplit(action, splitter, elements2);
                
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
     **/
    private void computeSimpleHitCounts(
        A action, 
        MPTSplitter<S> splitter, 
        List<S> elements
    ) {        
        for( S state: elements ) {      
            Set<S> list = graph.getPreset(state, action);
            if( list==null )
                continue;
            for( S preState: list ) {   
                hitCountMap.increaseHitCount(preState, action, splitter);                           
            }
        }   
    }   
    
    /**
     * Nel caso di splitter composto
     * calcola i hitCount per l'azione specificata e per i figli 
     * (escluso il pi� grande)
     * dello splitter specificato assumendo che 
     * gli elementi dei figli (escluso il pi� grande) siano quelli specificati.
     **/
    private void computeCompoundHitCounts(
        A action, 
        MPTSplitter<S> splitter, 
        List<List<S>> elements2
    ) {
        Iterator<MPTSplitter<S>> iter = splitter.getChildren().iterator();
        for( List<S> elements: elements2 ) {        	
            MPTSplitter<S> child = iter.next();            
            for( S state: elements ) {      
                Set<S> list = graph.getPreset(state, action);
                if( list==null )
                    continue;
                for( S preState: list ) {   
                    hitCountMap.increaseHitCount(preState, action, child);                           
                }
            }   
        }
    }   
    
// _____________________________________________________________________________	
// splitting

	/** 
	 * MPTSplitter semplice.
	 * Esegue lo splitting della partizione attuale rispetto
	 * all'azione specificata ed allo splitter semplice specificato
	 * assumendo che i suoi elementi siano quelli specificati.
	 **/
	private List<MPTClazz<S>> simpleSplit(
		A action, 
		List<S> elements
	) {
		HashSet<S> movedStates = new HashSet<S>();		
		List<MPTClazz<S>> clazzes = new LinkedList<MPTClazz<S>>();
		Set<S> preset;
		MPTClazz<S> clazz;
		MPTClazz<S> clazz1;
		
		for( S state: elements ) {					
			preset = graph.getPreset(state, action);
			if( preset==null )			
				continue;				
			for( S preState: preset ) {	
				clazz = partitionMap.get(preState);
				if( clazz.getSiblingsCount()==0 && clazz.size()==1 )
					continue;
				if( !movedStates.contains(preState) ) {
					clazz1 = clazz.getSibling(0);
					if( clazz1==null ) {
						clazzes.add(clazz);								
						clazz1 = new MPTClazz<S>(SIBLINGS);
						clazz.addSibling(clazz1, 0);
					}
					clazz.remove(preState);								
					clazz1.add(preState);		
					clazz.updateSiblingPosition( clazz1 );		
					partitionMap.put(preState, clazz1);	
					movedStates.add(preState);
				}				
			}
		}

		return clazzes;
	}

	/** 
	 * MPTSplitter composto.
	 * Esegue lo splitting della partizione attuale rispetto
	 * all'azione specificata e ai figli (escluso il pi� grande) 
	 * dello splitter specificato
	 * assumendo che gli elementi di tale figli siano quelli specificati.
	 **/
	private List<MPTClazz<S>> compoundSplit(
		A action, 
		MPTSplitter<S> splitter,
		List<List<S>> elements2
	) {
		// splitter children
		int childCount = splitter.getChildCount();
		// set of states moved by the splitting
		HashSet<S> movedStates = new HashSet<S>();	
		// list of clazzes interested by the splitting
		List<MPTClazz<S>> clazzes = new LinkedList<MPTClazz<S>>();
		
		// hit counts for the children of the splitter
		int hit;
		int lastHit;
				
		for( List<S> elements: elements2 ) {
			for( S state: elements ) {
				Set<S> preset = graph.getPreset(state, action);
				if( preset==null )			
					continue;				
                for( S preState: preset ) {	// for each state in the preset 
					MPTClazz<S> clazz = partitionMap.get(preState);
					if( clazz.getSiblingsCount()==0 && clazz.size()==1 )
						continue;
					if( !movedStates.contains(preState) ) { // if not moved
						// get hit counts for children
						lastHit = hitCountMap.getHitCount(
							preState, action, splitter
						);                        
						int siblingIndex = 0;	
						for(int i=0; i<childCount-1; ++i ) {
							hit = hitCountMap.getHitCount(
								preState, action, splitter.getChild(i)
							);
							if( hit>0 )
								siblingIndex += 1<<i;
							lastHit -= hit;		
							if( lastHit==0 )
								break;
						}
						if( lastHit>0 )
							siblingIndex += 1 << (childCount-1); 
						movedStates.add(preState);

						// set lastHit as being the hitCount for the father
						hitCountMap.setHitCount(
							preState, action, splitter, lastHit 
						);

						// get clazz of the preState, create it if necessary 
						MPTClazz<S> sibling = clazz.getSibling(siblingIndex-1);
						if( sibling==null ) {
							if( clazz.getSiblingsCount()==0 )
								clazzes.add(clazz);	
							sibling = new MPTClazz<S>(SIBLINGS);
							clazz.addSibling(sibling, siblingIndex-1);
						}

						clazz.remove( preState );
						sibling.add( preState );
						clazz.updateSiblingPosition( sibling );		
						partitionMap.put(preState, sibling);							
					}				
				}
			}
		}
		
		return clazzes;
	}

// _____________________________________________________________________________	
// updating

	/** Aggiorna la coda degli splitter in base agli splitting avvenuti **/
	private void updateSplitters(
		MPTQueue<S> queue,
		List<MPTClazz<S>> clazzes
	) {
		for( MPTClazz<S> clazz: clazzes ) {						
			if( clazz.getSiblingsCount()==0 )
				continue;

			MPTSplitter<S> owner = clazz.getSplitter();	
			if( clazz.isEmpty() && clazz.getSiblingsCount()==1 ) {
				owner.setClazz( clazz.getSiblings().get(0) );
				continue; 
			}
							
			// convert clazz and siblings in a tree of splitters rooted at owner
			toSplitterTree(clazz, owner);
			
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
	private void toSplitterTree(MPTClazz<S> clazz, MPTSplitter<S> owner ) {
		if( !clazz.isEmpty() ) {
			clazz.addSibling(clazz, SIBLINGS-1);
			clazz.updateSiblingPosition( clazz );		
		}		

		int count = clazz.getSiblingsCount();					
		int floors = (int) java.lang.Math.ceil( (count-1.0)/(CHILDREN-1) );
		int lastFloor = (count-1) % (CHILDREN-1);
		if( lastFloor==0 )
			lastFloor=CHILDREN-1;
		lastFloor += 1;
		
		int remaining = owner.getSize();
		owner.setClazz(null);
		
		ListIterator<MPTClazz<S>> iter = clazz.getSiblings().listIterator(count);
		MPTSplitter<S> child;
		MPTSplitter<S> father;
		MPTClazz<S> current;
		MPTSplitter<S> previousFather = null;
		for( int i=0; i<floors; ++i ) {
			father = i==0? owner : new MPTSplitter<S>(CHILDREN);
			if( previousFather!=null )
				previousFather.addChild( father );
			for( int j=0; j<CHILDREN-1; ++j ) {
				if( !iter.hasPrevious() )
					break;
				current = iter.previous();
				child = new MPTSplitter<S>(CHILDREN, current);
				father.addChild( child );				
				remaining -= child.getSize();
			}			
			if( i==floors-1 && iter.hasPrevious() ) {
				current = iter.previous();
				child = new MPTSplitter<S>(CHILDREN, current);
				father.addChild( child );
			} else {
				father.addToSize( remaining );
			}
			previousFather = father;
		}
		
		clazz.detachSiblings();		
	}

	/** 
	 * Mette le foglie dello splitter specificato nella coda,
	 * staccando le foglie dall'albero.
	 * Assume che lo splitter specificato abbia dei figli.
	 **/
	private void putLeavesInQueue(
		MPTSplitter<S> splitter, 
		MPTQueue<S> queue
	) {
		if( splitter.getChildCount()==0 ) {
			queue.put(splitter);
			return;
		}
		
		for( MPTSplitter<S> child: splitter.getChildren() ) {
			putLeavesInQueue( child, queue );
		}

		splitter.removeChildren();
	}
		
	/**
	 * Sostituisce il figlio maggiore di uno splitter con lo splitter
	 * stesso. Questo aggiusta i hitCount memorizzati durante split().
	 **/
	private void adjustSplitter(MPTSplitter<S> splitter) {
		MPTSplitter<S> splitterLast = splitter.getLastChild();
		splitter.removeChildren();
		if( !splitterLast.hasSons() ) {
			splitter.setClazz( splitterLast.getClazz() );			
		} else {
			for( MPTSplitter<S> grandSon: splitterLast.getChildren() )
				splitter.addChild( grandSon );
		}
		MPTQueue<S> queue = splitterLast.getQueue();
		if( queue!=null ) {			
			queue.remove(splitterLast);
			queue.put(splitter);
		}
	}

	public HashMap<S, ? extends HashSet<S>> getPartitionMap() {
		return partitionMap;
	}

}












