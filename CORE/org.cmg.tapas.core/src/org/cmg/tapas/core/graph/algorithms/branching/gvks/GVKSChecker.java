package org.cmg.tapas.core.graph.algorithms.branching.gvks;



import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.cmg.tapas.core.graph.algorithms.DepthVisitor;
import org.cmg.tapas.core.graph.algorithms.EquivalenceSolver;
import org.cmg.tapas.core.graph.algorithms.Evaluator;
import org.cmg.tapas.core.graph.algorithms.GraphAnalyzer;
import org.cmg.tapas.core.graph.algorithms.LoopInfo;
import org.cmg.tapas.core.graph.algorithms.StateInfo;
import org.cmg.tapas.core.graph.algorithms.bisimulation.BisimulationChecker;
import org.cmg.tapas.core.graph.ActionInterface;
import org.cmg.tapas.core.graph.EdgeFilter;
import org.cmg.tapas.core.graph.Graph;
import org.cmg.tapas.core.graph.GraphComposition;
import org.cmg.tapas.core.graph.GraphData;
import org.cmg.tapas.core.graph.GraphInterface;

/** 
 * GVKSChecker offre i metodi per il calcolo
 * della bisimulazione branching su grafi poggiando
 * sull'algortimo di Kannelakis-Smolka-Groote-Vaandrager.
 * <p>
 * @param <S> tipo degli stati
 * @param <A> tipo delle azioni
 * 
 * @author Guzman Tierno
 **/
public class GVKSChecker <
	S ,
	A extends ActionInterface	
> implements EquivalenceSolver<S,A> {	
	// TODO: BranchingChecker
	// TODO: Facade

	// Dati
	/** Grafo da analizzare. **/
	private GraphData<S,A> graph;
	/** Grafo trasformato. **/
	private GraphInterface<BState<S,A>,A> newGraph;
	/** Mappa tra le componenti del grafo originale e i nuovi stati. **/ 
	private HashMap<Integer,BState<S,A>> map;
	/** Mappa che associa ad ogni stato la sua componente. **/
	private LoopInfo<S,A> loopInfo;
	/** Blocchi che rappresentano possibili splitters. **/
	private BQueue<S,A> working;
	/** Blocchi rispetto ai quali la partizione attuale � stabile. **/
	private BQueue<S,A> stable;

	/** Azioni del grafo. **/
	private Set<A> actions; 
	/** Azione tau del grafo. Si assume che A abbia una sola azione silente. **/
	private A tauAction; 

	// Costruttori
	/** 
	 * Costruisce un GVSChecker per il grafo specificato. 
	 **/
	public GVKSChecker(
		GraphData<S,A> graph
	) {
		this.graph = graph;
	}	
	
	/**
	 * Costruisce un GVKSChecker per il grafo 
	 * ottenuto dalla composizione dei grafi specificati.
	 **/
	public GVKSChecker(
		GraphData<S,A> graph1, 
		GraphData<S,A> graph2
	) {
		this( new GraphComposition<S,A>(graph1, graph2) );
	}

	// Getters per i dati del nuovo grafo
	/**
	 * Rende il nuovo grafo costruito per il calcolo della
	 * bisimulazione branching. 
	 * Il nuovo grafo viene costruito nella chiamata computeEquivalence(...).
	 * Il nuovo grafo non viene costruito se il grafo originale
	 * non contiene tau.
	 * Il grafo � ottenuto collassando i loop di tau e 
	 * rimuovendo i selfloop di tau.
	 **/
	public GraphInterface<?,A> getNewGraph() {
		return newGraph;	
	}
	
	/**
	 * Rende la mappa che ad ogni componente del vecchio grafo
	 * associa uno stato del nuovo grafo.
	 * Il nuovo grafo viene costruito nella chiamata computeEquivalence(...).
	 * Il nuovo grafo NON viene costruito se il grafo originale
	 * non contiene tau.
	 * Il grafo � ottenuto collassando i loop di tau e 
	 * rimuovendo i selfloop di tau.
	 **/
	public HashMap<Integer,BState<S,A>> getComponentMap() {
		return map;
	}
	
	/**
	 * Rende le informazioni sui i loop e le componenti fortemente
	 * connesse del grafo da analizzare.
	 * Le informazioni vengono calcolate nella chiamata computeEquivalence(...).
	 * Le informazioni NON vengono calcolate  se il grafo originale
	 * non contiene tau.
	 **/
	public LoopInfo<S,A> getLoopInfo() {
		return loopInfo;
	}
	
	// Compute equivalence
	/**
     * Calcola la massima bisimulazione branching.
     **/
	public Evaluator<S,?> computeEquivalence() {
		return computeEquivalence(null);
	}
	
	/**
     * Controlla se gli stati specificati sono nella stessa 
     * classe di equivalenza rispetto alla bisimulazione branching.
     * Il metodo pu� risultare pi� veloce di una chiamata
     * a <tt>computeEquivalence()</tt> seguita da un controllo.
     **/
	public boolean checkEquivalence(S state1, S state2) {
		return checkEquivalence(null, state1, state2);
	}

	/**
     * Controlla se gli stati specificati sono nella stessa 
     * classe di equivalenza rispetto alla bisimulazione branching
     * meno fine tra quelle pi� fini della partizione specificata.
     * Il metodo pu� risultare pi� veloce di una chiamata
     * a <tt>computeEquivalence()</tt> seguita da un controllo.
     * Il parametro <tt>initPartition</tt> rappresenta la partizione
     * iniziale imposta sugli stati, pu� essere null.
     **/
	public <V> boolean checkEquivalence(
		Evaluator<S,V> initPartition, S state1, S state2
	) {
		initTauAction();
		if(tauAction==null) {
			return new BisimulationChecker<S,A>(graph).checkEquivalence(
				initPartition, state1, state2
			);	
		}

		computeEquivalence(initPartition, state1, state2);
		return 
			map.get( loopInfo.getComponent(state1) ) ==
			map.get( loopInfo.getComponent(state2) );
	}
	
	/**
     * Calcola la bisimulazione branching meno fine tra quelle pi� fini
     * della partizione specificata.
     * La partizione iniziale pu� essere null
     * per inidicare la partizione composta da una sola classe.
     **/ 
	public <V> Evaluator<S,?> computeEquivalence(
		Evaluator<S,V> initPartition
	) {		
		initTauAction();
		if(tauAction==null) {		
			return new BisimulationChecker<S,A>(graph).computeEquivalence(
				initPartition
			);	
		}

		return computeEquivalence(initPartition, null, null);
	}
	
	// Implementazione

	private void initTauAction() {	
		tauAction=null;
		actions = graph.getActions();
		for( A action: actions ) {
			if( action.isTau() ) {
				tauAction = action;
				break;
			}
		}
	}

	private void resetMaps() {
		this.map = new HashMap<Integer,BState<S,A>>(
			(int)(graph.getNumberOfStates()/0.75) + 1 
		);
		this.working = new BQueue<S,A>();
		this.stable = new BQueue<S,A>();
		
		this.newGraph = new Graph<BState<S,A>,A>();
	}

	/** usato per i test **/ 
	// rimuovere a fine test
	public static double collapseTime;

	/**
	 * Implementa le diverse varianti di computeEquivalence.
	 **/
	private <V> Evaluator<S,BClazz<S,A>> computeEquivalence(
		Evaluator<S,V> initPartition, S state1, S state2
	) {			
		// init
		double start = System.currentTimeMillis();
		resetMaps();
	
		// collapse loops
		if( initPartition!=null )
			initializeBlocks(initPartition);
		else 
			initializeBlocks(Evaluator.trivialEvaluator(state1));
		collapseTime = System.currentTimeMillis() - start;
			
		// refine
		BState<S,A> bstate1 = null;
		BState<S,A> bstate2 = null;		
		if( state1!=null ) {
			bstate1 = map.get( loopInfo.getComponent( state1 ) );
			bstate2 = map.get( loopInfo.getComponent( state2 ) );
		}		
		refine( bstate1, bstate2 );
		
		// return
		return new Evaluator<S,BClazz<S,A>>(){
            @Override public BClazz<S,A> eval(S state) {
				return map.get( loopInfo.getComponent(state) ).getClazz();
			}
		};
	}	
	
	/** 
	 * Collassa i loop interni alle classi ed inizializza le BClazz's.
	 **/
	private <V> void initializeBlocks( 
		Evaluator<S,V> evaluator 
	) {		
		GraphAnalyzer<S,A> analyzer = new GraphAnalyzer<S,A>(graph);
		// collapse loops
		loopInfo = analyzer.computeComponents( 
			new LoopFilter<V>(evaluator), new CollapseVisitor<V>(evaluator)
		);
		// create edges
		analyzer.depthVisit(
			analyzer.TRANSITIONS_ALL, new EdgeVisitor<V>(), true
		);
	}
	
	/** 
	 * Realizza il vero � proprio algoritmo di calcolo 
	 * dell'equivalenza branching. 
	 **/
	private void refine(BState<S,A> state1, BState<S,A> state2) {
		boolean lastChanged;
		boolean splitted;
		LinkedList<BClazz<S,A>> sourceBlocks;
		BClazz<S,A> sibling;
		BQueue<S,A> queue;
	
		// Proceed until working queue is empty
		while( !working.isEmpty() ) {
			lastChanged = false;		
			splitted = false;
			BClazz<S,A> splitter = working.iterator().next();
			List<BState<S,A>> elements = splitter.toList();
			boolean neededSplitting;
			
			// for every action 
			for( A action: actions ) {
				sourceBlocks = new LinkedList<BClazz<S,A>>();
				markLastStates(elements, action);
				lastChanged |= split(action, elements, sourceBlocks);

				// update clazzes
				splitted = false;			
				int splittedBlocks = 0;
				for( BClazz<S,A> clazz: sourceBlocks ) {					
					neededSplitting = clazz.needsSplitting();					
					clazz.resetLastMarkedCount();
					clazz.setFlag(false);
					if( !neededSplitting )
						continue;
					splittedBlocks++;
					sibling = clazz.getSibling();			
					clazz.detachFromSiblings();
					sibling.setFlag(false);			
					queue = clazz.getQueue();
					if( queue!=working ) {
						queue.remove(clazz);
						working.add(clazz);
					}
					working.add( sibling );
					splitted |= splitter==clazz;
				}				
				
				resetMarkedStates(elements, action);
				if( state1!=null && state1.getClazz()!=state2.getClazz() )
					return;				
				if( splitted )
					break;
			} 
			
			if( lastChanged ) {
				working.addAll( stable );
				stable.clear();
			} else if( !splitted ) {
				splitter.moveToQueue(stable);
			}
		}
	}
	
	
	private void markLastStates(
		List<BState<S,A>> splitterElements, 
		A action
	)  {		
		// mark last states hitting the splitter with the action
		Set<BState<S,A>> preset;
		for( BState<S,A> state : splitterElements ) {
			preset = newGraph.getPreset(state, action);
			if( preset==null )
				continue;
			for( BState<S,A> preState: preset )
				preState.markIfLast();
		}
	}

	private void resetMarkedStates(
		List<BState<S,A>> splitterElements, 
		A action
	)  {		
		Set<BState<S,A>> preset;
		for( BState<S,A> state : splitterElements ) {
			preset = newGraph.getPreset(state, action);
			if( preset==null )
				continue;				
			for( BState<S,A> preState: preset ) {
				preState.setMarked(false);
			}
		}
	}

	
	// esegue lo splitting dei blocchi che colpiscono lo splitter
	private boolean split( 
		A action,
		List<BState<S,A>> elements,
		List<BClazz<S,A>> clazzes
	) {
		Set<BState<S,A>> preset;
		BClazz<S,A> clazz;
		BClazz<S,A> clazz1;
		boolean lastChanged = false;
				
		// scan1
		for( BState<S,A> state: elements ) {							  
			preset = newGraph.getPreset(state, action);		
			if( preset==null )			
				continue;				
			for( BState<S,A> preState: preset ) {	 	
				clazz = preState.getClazz();
				if( !clazz.needsSplitting() ) {
					if( !clazz.getFlag() ) {
						clazz.setFlag(true);
						clazzes.add(clazz);
					}
					continue;
				}				
				if( clazz.getFlag() )
					continue;										
				if( action.isTau() && clazz==state.getClazz() )
					continue;										
				if( clazz.getSibling()==null && clazz.size()==1 )
					continue;
										
				clazz1 = clazz.getSibling();
				if( clazz1==null ) {
					clazzes.add(clazz);
					clazz1 = new BClazz<S,A>();
					clazz.setSibling(clazz1);					
					clazz1.setFlag(true);
				}
				pullPreClosure(clazz, clazz1, preState); 
			}
		}		
		
		// scan2
		Iterator<BState<S,A>> iter;
		BState<S,A> inert;
		boolean nonlast1;
		boolean nonlast2;		
		for( BClazz<S,A> touched: clazzes ) {
			if( !touched.needsSplitting() ) 
				continue;

			for( BState<S,A> state: touched.getSibling() ) {							  
				iter = state.getInert().iterator();
				nonlast1 = iter.hasNext();
				nonlast2 = false;
				while( iter.hasNext() ) {
					inert = iter.next();		
					if( state.getClazz()==inert.getClazz() )
						nonlast2 = true;
					else {
						iter.remove();
						inert.removePreInert(state);
					}
				}
				if( nonlast1!=nonlast2 ) {
					lastChanged = true;
					state.getClazz().increaseLastCount();
				}
			}
		}

		return lastChanged;
	}
	

//	// sposta la preimmagine di state da clazz a clazz1
//	private void pullPreClosure(
//		BClazz<S,A> clazz,
//		BClazz<S,A> clazz1,
//		BState<S,A> state
//	) {
//		clazz.remove( state );
//		clazz1.add(state);
//		Set<BState<S,A>> preset = newGraph.getPreset(state, tauAction);
//		if( preset==null )
//			return;
//		for( BState<S,A> preState: preset )
//			if( preState.getClazz()==clazz )
//				pullPreClosure(clazz, clazz1, preState);
//	}
	
	private void pullPreClosure(
		BClazz<S,A> clazz,
		BClazz<S,A> clazz1,
		BState<S,A> state
	) {
		clazz.remove( state );
		clazz1.add(state);
		for( BState<S,A> preState: state.getPreInert() )
			if( preState.getClazz()==clazz )
				pullPreClosure(clazz, clazz1, preState);
	}
	
	// Collassatori
	
	/** filtro per calcolare i loop interni alle classi di una partizione **/
	private class LoopFilter<V> extends EdgeFilter<S,A> {
		private Evaluator<S,V> evaluator;
		
		LoopFilter(Evaluator<S,V> evaluator) {
			this.evaluator = evaluator;
		}
		
		@Override 
		public boolean check(S src, A action, S dest) {
			return 
				action.isTau() && 
				evaluator.eval(src).equals( evaluator.eval(dest) );
		}
	}
	
	/** 
	 * Visitatore per la prima visita di inizializzazione:
	 * collapse, blocks, inert.
	 **/
	private class CollapseVisitor<V>
	implements GraphAnalyzer.CompsVisitor<S,A> {
		/** Partizione iniziale **/
		private Evaluator<S,V> evaluator;
		/** Mappa di corrispondenza tra i valori della partizione e blocchi. **/
		private HashMap<V,BClazz<S,A>> blockMap;
		/** Elementi della componente in fase di creazione. **/
		private List<StateInfo<S>> component;
		
		/** Costruttore. **/
		CollapseVisitor(Evaluator<S,V> evaluator) {
			this.evaluator = evaluator;
			this.blockMap = new HashMap<V,BClazz<S,A>>();
		}
		
		/** Chiamato quando viene creata una componente. **/
		public void openComponent(
			int compIndex, Map<S, StateInfo<S>> infoMap
		) {
			component = new LinkedList<StateInfo<S>>();
		}
		
		/** Chiamato quando viene completata una componente. **/
		public void closeComponent(
			int compIndex, Map<S, StateInfo<S>> infoMap
		) {			
			// inert
			for( StateInfo<S> stateInfo: component ) {
				S state = stateInfo.getState();
				Set<S> postset = graph.getPostset(state, tauAction);
				if( postset!=null ) {
					for( S post: postset ) {						
						if( evaluator.eval(post) != evaluator.eval( state ) )
							continue;
						int postComp = infoMap.get(post).getComponent();
						if( postComp == compIndex )
							continue; 
						map.get(compIndex).addInert( map.get(postComp) ); 
						map.get(postComp).addPreInert( map.get(compIndex) ); 
					}			
				}
			}				
		}
		
		/** Chiamato quando viene aggiunto un elemento a una componente. **/
		public void addToComponent(
			StateInfo<S> stateInfo, int compIndex, Map<S, StateInfo<S>> infoMap
		) {
			S state = stateInfo.getState();
			
			// block, working
			BClazz<S,A> block = blockMap.get( evaluator.eval(state) );
			if( block==null ) {
				block = new BClazz<S,A>();
				blockMap.put( evaluator.eval(state), block );
				working.add(block);
			}
			
			// new state
			BState<S,A> newState = map.get(compIndex);
			if( newState==null ) {
				newState = new BState<S,A>(state);
				newGraph.addState(newState); 
				map.put(compIndex, newState);
				block.add(newState);				
			}
				
			// list
			component.add(stateInfo);
		}
	}



	/** 
	 * Visitatore per la seconda visita di inizializzazione:
	 * edges of collapsed graph, in edges of blocks.
	 **/
	private class EdgeVisitor<V>
	implements DepthVisitor<S,A> {
		/** Gestisce la visita di un arco. **/
		private void treat(S src, A action, S dest) {
			BState<S,A> newSrc = map.get( loopInfo.getComponent(src) );
			BState<S,A> newDest = map.get( loopInfo.getComponent(dest) );
			if( newSrc==newDest && action.isTau() )
				return;
			
			newGraph.addEdge( newSrc, action, newDest );
		}		
		
		/** 
		 * Chiamato quando la visita scende in profondit�. 
		 **/
		public boolean goingTo(
			int fromDepth, S from, int fromIndex, A action, S to, int toIndex
		) {
			if( fromIndex==-1 )
				return true;
			
			treat(from, action, to);
			return true;
		}
		
		/** 
		 * Chiamato quando la visita ritorna sui suoi passi. 
		 **/
		public boolean comingBack(
			int toDepth, S to, int toIndex, A action, S from, int fromIndex
		) {
			return true;
		}
		
		/** 
		 * Chiamato quando la visita colpisce uno stato gi� visitato.
		 **/
		public boolean hitMarked(
			int fromDepth, S from, int fromIndex, A action, S hit, int hitIndex
		) {
			treat(from, action, hit);
			return true;
		}
	}
	
}	





