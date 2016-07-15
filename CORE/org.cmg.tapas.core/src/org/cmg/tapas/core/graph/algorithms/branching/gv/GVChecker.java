package org.cmg.tapas.core.graph.algorithms.branching.gv;



//import testGraph.LtsAction;

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
 * GVChecker offre i metodi per il calcolo
 * della bisimulazione branching su grafi poggiando
 * sull'algortimo di Groote-Vaandrager.
 * <p>
 * @param <S> tipo degli stati
 * @param <A> tipo delle azioni
 * 
 * @author Guzman Tierno
 **/
public class GVChecker <
	S ,
	A extends ActionInterface	
> implements EquivalenceSolver<S,A> {	

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
		
	/** Azione tau del grafo. Si assume che A abbia una sola azione silente. **/
	private A tauAction; 

	// Costruttori	
	/** 
	 * Costruisce un GVChecker per il grafo specificato. 
	 **/
	public GVChecker(
		GraphData<S,A> graph
	) {
		this.graph = graph;
	}	
	
	/**
	 * Costruisce un GVChecker per il grafo 
	 * ottenuto dalla composizione dei grafi specificati.
	 **/
	public GVChecker(
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
		for( A action: graph.getActions() ) {
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
	public static double collapseTime;

	/**
	 * Implementa le diverse varianti di computeEquivalence.
	 **/
	private <V> Evaluator<S,BBlock<S,A>> computeEquivalence(
		Evaluator<S,V> initPartition, S state1, S state2
	) {		
		double start = System.currentTimeMillis();
		resetMaps();
		if( initPartition!=null )
			initializeBlocks(initPartition);
		else 
			initializeBlocks(Evaluator.trivialEvaluator(state1));
		collapseTime = System.currentTimeMillis() - start;
			
		BState<S,A> bstate1 = null;
		BState<S,A> bstate2 = null;		
		if( state1!=null ) {
			bstate1 = map.get( loopInfo.getComponent( state1 ) );
			bstate2 = map.get( loopInfo.getComponent( state2 ) );
		}
		
		refine( bstate1, bstate2 );

		return new Evaluator<S,BBlock<S,A>>(){
            @Override
			public BBlock<S,A> eval(S state) {
				return map.get( loopInfo.getComponent(state) ).getBlock();
			}
		};
	}	
	
	/** 
	 * Collassa i loop interni alle classi ed inizializza
	 * i BBlock's.
	 **/
	private <V> void initializeBlocks( 
		Evaluator<S,V> evaluator 
	) {
		GraphAnalyzer<S,A> analyzer = new GraphAnalyzer<S,A>(graph);
		loopInfo = analyzer.computeComponents( 
			new LoopFilter<V>(evaluator), new CollapseVisitor<V>(evaluator)
		);
		analyzer.depthVisit(
			analyzer.TRANSITIONS_ALL, new EdgeVisitor<V>(), true
		);
	}
	
	/** 
	 * Realizza il vero � proprio algoritmo di calcolo 
	 * dell'equivalenza branching. Groote-Vaandrager.
	 **/
	private void refine(BState<S,A> state1, BState<S,A> state2) {
		boolean lastChanged;
		boolean splitted;
		int flags;
		LinkedList<BBlock<S,A>> sourceBlocks;

		// Proceed until working queue is empty
		while( !working.isEmpty() ) {
			lastChanged = false;		
			splitted = false;
			BBlock<S,A> splitter = working.iterator().next();
			Set<A> keySet = splitter.getIn().keySet();
			
			// for every action entering the splitter
			Iterator<A> keyIter = keySet.iterator();
			while( keyIter.hasNext() ) {
				A action = keyIter.next();
				sourceBlocks = markBlocksAndStates(splitter, action);
				flags = split(splitter, sourceBlocks);
				resetStateMarks(splitter, action);
				splitted = (flags & 1) >0;
				lastChanged |= (flags & 2) >0;
				if( state1!=null && state1.getBlock()!=state2.getBlock() )
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
	
	private LinkedList<BBlock<S,A>> markBlocksAndStates(
		BBlock<S,A> splitter, 
		A action
	)  {		
		LinkedList<BBlock<S,A>> sourceBlocks = new LinkedList<BBlock<S,A>>();
		
		// mark states and blocks hitting the splitter
		for( BEdge<S,A> t : splitter.getIn().get(action) ) {
			BBlock<S,A> sourceBlock = t.getSource().getBlock();
			sourceBlock.markState(t.getSource());
			if( !sourceBlock.isMarked() ) {
				sourceBlock.setMarked(true);
				sourceBlocks.add( sourceBlock );
			}	
		}
		
		return sourceBlocks;
	}

	private void resetStateMarks(
		BBlock<S,A> splitter, 
		A action
	)  {		
		for( BEdge<S,A> t : splitter.getIn().get(action) )
			t.getSource().setMarked(false);
	}

	
	// esegue lo splitting dei blocchi che colpiscono lo splitter
	// rende 1 se vi � stato un autosplitting.
	// rende 2 se sono cambiati gli stati ultimi
	// pu� rendere 3 = 2 + 1.
	private int split( 
		BBlock<S,A> splitter,
		List<BBlock<S,A>> sourceBlocks
	) {
		BBlock<S,A> block1;
		BBlock<S,A> block2;
		boolean splitted = false;
		boolean lastChanged = false;
		for( BBlock<S,A> block: sourceBlocks ) {
			block.setMarked(false);
			int lastMarked = block.resetLastMarkedCount();
								
			// if all last states hit the splitter, no splitting needed
			if( lastMarked==block.getLastCount() )
				continue;	
			
			if( block==splitter )
				splitted=true;
				
			block1 = new BBlock<S,A>();
			block2 = new BBlock<S,A>();
			block.removeFromQueue();
			working.add( block1 );
			working.add( block2 );
			
			// for every last state in the block hitting the splitter
			Iterator<BState<S,A>> iter = block.getLast().iterator();
			while( iter.hasNext() ) {
				BState<S,A> bstate = iter.next();
				(bstate.isMarked() ? block1 : block2).addLast(bstate);						
			}
	
			// for every non last state in the block hitting the splitter
			iter = block.getNonLast().iterator();
			boolean hit1;
			LinkedList<BState<S,A>> hit2;
			while( iter.hasNext() ) {
				BState<S,A> bstate = iter.next();
				hit1 = bstate.isMarked();						
				hit2 = new LinkedList<BState<S,A>>();						
				for( BState<S,A> dest: bstate.getInert() ) {
					if( block1.contains( dest ) )
						hit1 = true;
					else
						hit2.add(dest);
				}
				if( !hit1 ) {
					block2.addNonLast(bstate);
					continue;
				}
				block1.addNonLast( bstate );
				lastChanged |= bstate.removeInert( hit2 );
				block2.addIn(bstate, tauAction, hit2);
			} 
			
			// for every edge entering the block 
			for( A a: block.getIn().keySet() ) {
				for( BEdge<S,A> t: block.getIn().get(a) ) {
					if( block1.contains( t.getDest() ) )
						block1.addIn( t );
					else
						block2.addIn( t );
				}
			}
		} 
		
		return (splitted ? 1 : 0) + (lastChanged ? 2 : 0);
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
	 * collapse, blocks, last, inert, non last.
	 **/
	private class CollapseVisitor<V>
	implements GraphAnalyzer.CompsVisitor<S,A> {
		/** Partizione iniziale **/
		private Evaluator<S,V> evaluator;
		/** Mappa di corrispondenza tra i valori della partizione e blocchi. **/
		private HashMap<V,BBlock<S,A>> blockMap;
		/** Elementi della componente in fase di creazione. **/
		private List<StateInfo<S>> component;
		
		/** Costruttore. **/
		CollapseVisitor(Evaluator<S,V> evaluator) {
			this.evaluator = evaluator;
			this.blockMap = new HashMap<V,BBlock<S,A>>();
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
			// inert, non last
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
			BBlock<S,A> block = blockMap.get( evaluator.eval(state) );
			if( block==null ) {
				block = new BBlock<S,A>();
				blockMap.put( evaluator.eval(state), block );
				working.add(block);
			}

			// new state
			BState<S,A> newState = map.get(compIndex);
			if( newState==null ) {
				newState = new BState<S,A>(state);
				newGraph.addState(newState); 
				map.put(compIndex, newState);
				block.addLast(newState);				
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
			
			if( newGraph.addEdge( newSrc, action, newDest ) ) {
				if( !action.isTau() || newSrc.getBlock()!=newDest.getBlock() )
					newDest.getBlock().addIn( newSrc, action, newDest );
			}
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





