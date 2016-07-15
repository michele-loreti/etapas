package org.cmg.tapas.core.graph.algorithms;


import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;

import org.cmg.tapas.core.graph.ActionInterface;
import org.cmg.tapas.core.graph.Graph;
import org.cmg.tapas.core.graph.GraphData;
import org.cmg.tapas.core.graph.GraphInterface;
import org.cmg.tapas.core.graph.StateInterface;

/**
 * Classe che si incarica della costruzione dei grafi
 * associati ad un dato grafo. Per mezzo di tali 
 * grafi associati � possibile controllare equivalenze
 * varie usando la bisimulazione. 
 * Si veda <code>DecoratedTraceChecker</code>.
 **/ 
public class AcceptanceBuilder<
	S ,
	A extends ActionInterface
> {
	// Bit masks per la definizione delle equivalenze:
	// 1	closure
	// 2	convergenceFlag
	// 3	finalSensitive
	// 4	acceptanceFlag
	// 5	convergenceHistory
	// 6	convergenceCut
	
	/** Flag per le equivalenze deboli. **/
	public static final int WEAK = 1<<0;
	/** Flag per le equivalenze sensibili alla convergenza. **/
	public static final int CONV = 1<<1;
	/** Flag per le equivalenze sensibili agli stati finali. **/
	public static final int FINL = 1<<2;
	/** Flag per le equivalenze sensibili agli insiemi di accettazione. **/
	public static final int ACPT = 1<<3;
	/** Flag per le equivalenze con storia della divergenza. **/
	public static final int HIST = 1<<4;
	/** Flag per le equivalenze che tagliano i rami divergenti. **/
	public static final int CUTC = 1<<5;

	// analizzatore di gradi
	private GraphAnalyzer<S,A> analyzer;
	// Grafo di cui calcolare il grafo associato
	private GraphData<S,A> graph;
	// Stati iniziali del grafo da analizzare
	private S initState1;
	private S initState2;
	// Insiemi di equivalenza degli stati iniziali
	private Set<S> initSet1;
	private Set<S> initSet2;
	// Azioni del grafo da analizzare
	private Set<A> actionSet;
	// Azione tau, null se non presente nel grafo
	private A tauAction;		
	// LoopInfo per il grafo da analizzare
	private LoopInfo<S,A> loopInfo;
	
	// Flag che definiscono il grafo da costruire
	private boolean closureFlag;
	private boolean finalSensitive;
	private boolean convergenceFlag;
	private boolean acceptanceFlag;
	private boolean convergenceCut;
	private boolean convergenceHistory;
	
	// Nuovo grafo in costruzione
	private GraphInterface<AcceptanceState<A>,A> newGraph;
	// Stati iniziali del nuovo grafo
	private AcceptanceState<A> newInitState1;
	private AcceptanceState<A> newInitState2;
	// mappa dei nuovi stati gi� creati
	private BuildMap<AcceptanceState<A>> map = 
		new BuildMap<AcceptanceState<A>>();
	
	//Mappa tre i nuovi stati e le vecchie partizioni
	private HashMap<AcceptanceState<A>, HashSet<S>> partitionMap = 
		new HashMap<AcceptanceState<A>, HashSet<S>>();
    
	// mappa che memorizza la convergenza di insiemi di stati
	// @SuppressWarnings("unused")
	// private HashMap<Set<S>,Boolean> convMap = new HashMap<Set<S>,Boolean>();
	
	/**
	 * Costruisce un nuovo AcceptanceBuilder per 
	 * il grafo dato con i due stati iniziali specificati
	 * e per l'equivalenza specificata.
	 **/
	public AcceptanceBuilder(
		GraphAnalyzer<S,A> analyzer,
		S initState1,
		S initState2,
		int equivalence
	) {
		this.analyzer = analyzer;
		this.graph = analyzer.getGraph();
		this.actionSet = graph.getActions();
		this.initState1 = initState1;
		this.initState2 = initState2;
		computeFlags(equivalence);
		for(A action: actionSet) {
			if(action.isTau()) {
				tauAction = action;					
				break;
			}					
		}
		if( tauAction==null && closureFlag) {
			closureFlag = false;
			equivalence -= WEAK;
		}				
		
		// init states
		if( initState1!=null ) {
			initSet1 = new FastSet<S>();					// using FastSet
			initSet1.add( initState1 );	
			if( closureFlag )
				analyzer.computeClosure(initSet1, tauAction);
		}
		if( initState2!=null ) {
			initSet2 = new FastSet<S>();					// using FastSet
			initSet2.add( initState2 );		
			if( closureFlag )
				analyzer.computeClosure(initSet2, tauAction);
				// Funziona solo se fra le azioni vi � 
				// una sola azione silente
		}

		if( convergenceFlag && tauAction!=null )
			loopInfo = analyzer.computeComponents(analyzer.TAU_FILTER, null);
	}
	
	// Compute flags from equivalence 
	private void computeFlags(int equivalence) {
		closureFlag = (equivalence & WEAK) > 0;
		finalSensitive = (equivalence & FINL) > 0;
		convergenceFlag = (equivalence & CONV) > 0;
		acceptanceFlag = (equivalence & ACPT) > 0;
		convergenceCut = (equivalence & CUTC) > 0;
		convergenceHistory = (equivalence & HIST) > 0;
	}
	
	/** 
	 * Costruisce il grafo associato al grafo specificato nel
	 * costruttore secondo le opzioni specificate nel costruttore.
	 **/
	public void buildGraph() {
		newGraph = new Graph<AcceptanceState<A>,A>();
		
		if( initState1==null )
			return;

		boolean convergent1 = true;
		if( convergenceFlag )	
			convergent1 = internalIsConvergent( initSet1 );		
		newInitState1 = buildAssociatedGraph2( initSet1, convergent1 );//using 2
		
		if( initState2==null )
			return;

		boolean convergent2 = true;
		if( convergenceFlag )	
			convergent2 = internalIsConvergent( initSet2 );		
		newInitState2 = buildAssociatedGraph2( initSet2, convergent2 );//using 2
	}
	
	/** Rende il nuovo grafo costruito con <code>buildGraph()</code>. **/
	public GraphInterface<AcceptanceState<A>,A> getNewGraph() {
		return newGraph;
	}
	
	/** 
	 * Rende le informazioni sulla convergenza del grafo.
	 * Le informazioni sono effettivamente calcolate solo per le 
	 * equivalenze che lo richiedono e solo nel caso che
	 * il grafo contenga azioni silenti.
	 **/
	public LoopInfo<S,A> getLoopInfo() {
		return loopInfo;
	}
	
	/** 
	 * Rende il primo stato iniziale del nuovo grafo
	 * costruito con <code>buildGraph()</code>.
	 **/ 
	public AcceptanceState<A> getInitState1() {
		return newInitState1;			
	}

	/** 
	 * Rende il secondo stato iniziale del nuovo grafo
	 * costruito con <code>buildGraph()</code>.
	 **/ 
	public AcceptanceState<A> getInitState2() {
		return newInitState2;			
	}
	
	public BuildMap<AcceptanceState<A>> getBuildMap(){
		return map;
	}

	/** 
	 * Dice se gli stati dell'insieme specificato sono
	 * tutti convergenti. In altre parole rende true se nessuno
	 * stato nell'insieme specificato pu� generare una sequenza
	 * infinita di azioni.
	 * Se loopInfo � null rende true.
	 **/
	private boolean internalIsConvergent(
		Collection<S> set 
	) {
		if( loopInfo==null )
			return true;
			
		for( S state: set )
			if( !loopInfo.isConvergent(state) )
				return false;
		
		return true;		
	}

	/** 
	 * Dice se fra gli stati dell'insieme specificato 
	 * ce n'� almeno uno finale, ossia almeno uno senza 
	 * archi uscenti. 
	 **/
	private boolean isFinal(
		Collection<S> set
	) {
		for( S state: set )
			if( graph.isFinal(state) )
				return true;

		return false;		
	}

	/**
	 * Costruisce ricorsivamente un nuovo grafo 
	 * in accordo con i flag globali ed associato al grafo scelto.
	 **/ 
	private AcceptanceState<A> buildAssociatedGraph(
		Set<S> set, boolean convergent
	) {
		// Check for the couple (set,conv) in map
		AcceptanceState<A> state = map.get( set, convergent );
		if( state!=null ) 
			return state;
		
		// acceptance set			
		AcceptanceSet<A> acceptance = null;
		if( convergent && acceptanceFlag )
			acceptance = analyzer.acceptanceSet(set, tauAction );		
						
		// flags
		Boolean finale = finalSensitive ? Boolean.valueOf(isFinal(set)) : null;			
		Boolean conv = convergenceFlag ? Boolean.valueOf(convergent) : null;
			
		// new state 
		AcceptanceState<A> newState = new AcceptanceState<A>(
			"S"+newGraph.getNumberOfStates() , acceptance, conv, finale 
		);	
		newGraph.addState( newState );			
		map.put(set, convergent, newState);
		
		// cut analysis if needed
		if( convergenceCut && !convergent ) 
			return newState;
		
		// build all transitions
		Set<S> image;
		for( A action: actionSet ) {
			if( closureFlag && action.isTau() )
				continue;
			image = new FastSet<S>(); 					// using FastSet
			analyzer.computeImage(set, image, action);
			if( image.isEmpty() )
				continue;
			if( closureFlag )
				analyzer.computeClosure(image, tauAction);				
			boolean convImage = true;
			if( convergenceFlag && (convergent || !convergenceHistory) ) {
				// convImage = convMap.get( image );	// not using convMap	
				// if( convImage==null )
				convImage = internalIsConvergent( image );
			}
														
			state = buildAssociatedGraph( image, convImage );
			newGraph.addEdge( newState, action, state );
		}

		return newState;
	}
	
	public HashMap<AcceptanceState<A>, HashSet<S>> getPartitionMap(){
		return partitionMap;
	}

	/** 
	 * Classe che contiene le informazioni su uno stato
	 * raccolte durante la costruzione non ricorsiva
	 * di un grafo associato.
	 **/
	private class BuildInfo {
		// Stato di cui si tengono le informazioni
		AcceptanceState<A> state;		
		// Insieme di stati che <tt>state</tt> rappresenta
		Set<S> set;		
		// iteratore delle azioni in uscita
		Iterator<A> actionIter;
		// insieme di stati convergente
		boolean convergent;
		// ultima azione usata
		A action;
		
		// costruttore
		BuildInfo( 
			Set<S> set, 
			boolean convergent,
			Iterator<A> actionIter
		) {
			this.set = set;		
			this.convergent = convergent;
			this.actionIter = actionIter;
		}
	}

	/**
	 * Costruisce un nuovo grafo 
	 * in accordo con i flag globali ed associato al grafo scelto.
	 * Non ricorsivo.
	 **/ 
	private AcceptanceState<A> buildAssociatedGraph2(
		Set<S> set, boolean convergent
	) {
		// Check for the couple (set,conv) in map
		AcceptanceState<A> retState = map.get( set, convergent );
		if( retState!=null ) 
			return retState;
		
		// initialize recursion stack and structures
		boolean enter = true;
		LinkedList<BuildInfo> recStack = new LinkedList<BuildInfo>();
		recStack.add( new BuildInfo(set, convergent, actionSet.iterator()) );
				
		// simulate recursion
		while( !recStack.isEmpty() ) {
			// get a state from recursion stack
			BuildInfo info = recStack.removeLast();
			
			if( enter ) {				// entering a new set: initialize
				// acceptance set
				AcceptanceSet<A> acceptance = null;
				if( info.convergent && acceptanceFlag )
					acceptance = analyzer.acceptanceSet(info.set, tauAction);
				
				// flags
				Boolean finale = finalSensitive 
					? Boolean.valueOf(isFinal(info.set)) : null;
				Boolean conv = convergenceFlag 
					? Boolean.valueOf(info.convergent) : null;

				// state
				info.state = new AcceptanceState<A>(
					"S"+newGraph.getNumberOfStates(), acceptance, conv, finale 
				);	
				newGraph.addState( info.state );	
				partitionMap.put(info.state, new HashSet<S>(set));
				map.put(info.set, info.convergent, info.state);
								
				// cut analysis if needed
				if( convergenceCut && !info.convergent ) {
					retState = info.state;
					enter = false;		
					continue;
				}
			} else {					// returning from subcall 
				newGraph.addEdge( info.state, info.action, retState );
			}
						
			
			set = new FastSet<S>(); 	// using FastSet
			while( true ) {
				info.action = null;		// get next action
				if( !info.actionIter.hasNext() )
					break;
				info.action = info.actionIter.next();
				if( closureFlag && info.action.isTau() )
					continue;
				analyzer.computeImage(info.set, set, info.action);
				if( !set.isEmpty() )
					break;				
			}
							
			if( info.action!=null ) {	// if action not null
				recStack.add( info );	// push father
				if( closureFlag )
					analyzer.computeClosure(set, tauAction);
				convergent = true;
				if(convergenceFlag && (info.convergent || !convergenceHistory)) {
					convergent = internalIsConvergent( set );
				}		
				retState = map.get( set, convergent );
				if( retState==null ) {	// (set,convergent) is unknown
					recStack.add( new BuildInfo(
						set, convergent, actionSet.iterator()
					));					// create info
					enter = true;		// push it and treat it
				} else {				// (set,convergent) is known
					enter = false;		// return
				}
			} else {					// no more actions
				retState = info.state;
				enter = false;
			}
		}
		
		return retState;
	}


	/** 
	 * Classe per l'associazione tra insiemi di stati e stati
	 * dei nuovi grafi in costruzione.
	 **/
	private class BuildMap<T> { 
		private HashMap<Set<S>, HashMap<Boolean, T>> buildMap =
			new HashMap<Set<S>, HashMap<Boolean, T>>();
			
		T get(Set<S> set, boolean flag) {
			HashMap<Boolean, T> map1 = buildMap.get(set);
			if( map1==null )
				return null;
				
			return map1.get(flag);
		}
		
		void put(Set<S> set, boolean flag, T state) {
			HashMap<Boolean, T> map1 = buildMap.get(set);
			if( map1==null ) {
				map1 = new HashMap<Boolean, T>();
                buildMap.put(set, map1);
			}
			
			map1.put(flag, state);
		}
		
		@Override
		public String toString() {
			return map.toString();
		}
	}

}

