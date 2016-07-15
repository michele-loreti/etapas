package org.cmg.tapas.core.graph.algorithms;


import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.cmg.tapas.core.graph.ActionInterface;
import org.cmg.tapas.core.graph.EdgeFilter;
import org.cmg.tapas.core.graph.ExpandedGraph;
import org.cmg.tapas.core.graph.Graph;
import org.cmg.tapas.core.graph.GraphData;
import org.cmg.tapas.core.graph.GraphInterface;
import org.cmg.tapas.core.graph.LabelledState;
import org.cmg.tapas.core.graph.PointedGraph;
import org.cmg.tapas.core.graph.StateInterface;

/** 
 * GraphAnalyzer offre vari metodi per analizzare grafi.
 * Sono messi a disposizione metodi per il calcolo della chiusura
 * di un insieme di stati, per il calcolo delle componenti
 * fortemente connesse, per il calcolo del rank e per la 
 * costruzione dei grafi associati che permettono di ridurre 
 * il controllo di varie equivalenze (come Trace) alla bisimulazione.
 * Per altri algoritmi che producono nuovi grafi a partire da un grafo
 * dato, si veda GraphAnalyzer2. <p>
 * La classe GraphAnalyzer non supporta la concorrenza nel senso
 * che se due thread chiamano simultaneamente uno o pi� metodi di uno
 * stesso GraphAnalyzer il risultato non � predicibile.
 * Se si devono effettuare pi� analisi parallelamente si usino
 * pi� GraphAnalyzer diversi.
 * <p>
 * @param <S> tipo degli stati
 * @param <A> tipo delle azioni
 * 
 * @author Guzman Tierno
 **/
public class GraphAnalyzer<
	S ,
	A extends ActionInterface 
> {
	// _________________________________________________________________________
	// static
	
	protected static int RECURSIVE = 0;	
	protected static int NON_RECURSIVE = 1;
	
	/** Rende un filtro che accetta tutte gli archi. **/
	public static <S , A > 
	EdgeFilter<S,A> getNoEdgeFilter() {
		return new EdgeFilter<S,A>() {
	        @Override
	        public boolean check(S unusedSrc, A unusedAction, S unusedDest) {
				return true;
			}
		};
	}

	/** Rende un filtro per le azioni tau. **/
	public static <S , A extends ActionInterface > 
	EdgeFilter<S,A> getTauFilter() {
		return new EdgeFilter<S,A>() {
	        @Override 
	        public boolean check(S src, A action, S dest) {
				return action.isTau();
			}
		};
	}
	
	// _________________________________________________________________________
	
	/** Il grafo da analizzare. **/	
	protected GraphData<S,A> graph;
	
	/** Filtro che accetta tutte gli archi. **/
	public final EdgeFilter<S,A> TRANSITIONS_ALL = 
        GraphAnalyzer.<S,A>getNoEdgeFilter();

	/** Filtro per le azioni tau. **/
	public final EdgeFilter<S,A> TAU_FILTER = 
        GraphAnalyzer.<S,A>getTauFilter();
	
	/** 
	 * Costruttore senza argomenti.	
	 **/
	public GraphAnalyzer(GraphData<S,A> graph) {
		this.graph = graph;
	}
	
	/** Rende il grafo da analizzare. **/	
	public GraphData<S,A> getGraph() {
		return graph;
	}


// _____________________________________________________________________________
// Image, closure, clazzes

	/** 
	 * Aggiunge a <code>image</code> gli elementi 
	 * dell'immagine dell'insieme specificato
	 * sotto l'azione specificata. 
	 * Assume che <tt>set</tt> e <tt>image</tt> non coincidano come oggetti.
	 **/
	public void computeImage(
		Collection<S> set, 
		Set<S> image, 
		A action
	) {
		Set<S> postSet;
		for( S element: set ) {					
			postSet = graph.getPostset( element, action );
			if( postSet==null )
				continue;
			for( S post: postSet ) {
				image.add( post );			
			}
		}
	}

	/**
	 * Chiude l'insieme specificato rispetto all'azione
	 * specificata.
	 **/
	public void computeClosure(
		Set<S> clazz, 
		A action
	) {
		LinkedList<S> queue = new LinkedList<S>(clazz);		
		Set<S> postset;
		while( !queue.isEmpty() ) {
			postset = graph.getPostset(queue.remove(), action);
			if( postset!=null )
				for( S post: postset )
					if( clazz.add(post) )
						queue.add(post);
		}
	}
	
	/**
	 * Calcola le classi di una partizione a partire da una mappa.
	 * (non testato)
	 **/ 
	public <V> List<? extends Set<S>> mapToClazzes(
		Evaluator<S,V> partitionMap
	) {
		LinkedList<HashSet<S>> list = new LinkedList<HashSet<S>>();
		HashMap<V,HashSet<S>> map = new HashMap<V,HashSet<S>>();
		
		S state;
		Iterator<S> iter = graph.getStatesIterator();
		V value;
		HashSet<S> clazz;
		while( iter.hasNext() ) {
			state = iter.next();
			value = partitionMap.eval(state);
			clazz = map.get(value);
			if( clazz==null ) {
				clazz = new HashSet<S>();
				map.put( value, clazz );				
				list.add( clazz );
			}
			clazz.add( state );			
		}
		
		return list;
	}
	
// _____________________________________________________________________________
// Components, convergence, rank

	// used for depth first search and rank
	private HashMap<S, Integer> dfsIndexMap;
	private HashMap<S, StateInfo<S>> infoMap;
	private int maxRank;
	private int globalDfs;
	private LinkedList<S> dfsStack;
	
	//Mappa tre i nuovi stati e le vecchie partizioni
//	private HashMap<AcceptanceState<A>, HashSet<S>> partitionMap = 
//		new HashMap<AcceptanceState<A>, HashSet<S>>();
	
	/** 
	 * Classe che contiene le informazioni su uno stato
	 * raccolte durante una visita in profondit� 
	 * (senza ricorsione) per la ricerca delle componenti del grafo.
	 **/
	private class CompsInfo {		
		// Stato di cui si tengono le informazioni
		S state;		
		// iteratore dell'immagine dello stato
		Iterator<Map.Entry<A,S>> postIter;
		// indice dello stato durante la visita in profondit�
		int dfs = -1;
		// radice della componente dello stato
		int root = -1;	
		// rank dello stato
		int rank = -2;	
		// dimensione dello stack nel momento dell'inserimento
		// dello stato, per l'algoritmo senza ricorsione
		int stackSize = 0;
		// flag di self-loops per lo stato
		boolean selfLoops = false;
		// flag di convergenza dello stato
		boolean convergent = true;
		// flag che indica se lo stato � una foglia
		boolean leaf = true;
		
		// costruttore
		CompsInfo( GraphData<S,A> graph, S state, EdgeFilter<S,A> filter ) {
			this.state = state;		
			this.postIter = graph.getImageIterator( state, true, filter );
		}
	}
	
	/**
	 * Calcola le componenti fortemente connesse 
	 * del sottografo raggiungibile dagli stati
	 * specificati e ristretto agli archi specificati dal filtro.
	 * <p>
	 * Il visitor pu� essere null se non si desidera effettuare
	 * nessun lavoro speciale durante l'analisi delle componenti.
	 * <p>
	 * Usa un algoritmo non ricorsivo.
	 **/	
	public LoopInfo<S,A> computeComponents(
		List<S> stateList,
		EdgeFilter<S,A> filter,
		CompsVisitor<S,A> visitor
	) {
		return computeComponents(
			stateList.iterator(), filter, visitor, NON_RECURSIVE
		);		
	}
	
//	public HashMap<AcceptanceState<A>, HashSet<S>> getPartitionMap(){
//		return partitionMap;
//	}

	/**
	 * Calcola le componenti fortemente connesse 
	 * del sottografo raggiungibile dagli stati
	 * specificati e ristretto agli archi specificati dal filtro.
	 * <p>
	 * Il visitor pu� essere null se non si desidera effettuare
	 * nessun lavoro speciale durante l'analisi delle componenti.
	 * <p>
	 * Usa un algoritmo ricorsivo.
	 **/	
	public LoopInfo<S,A> computeComponentsRec(
		List<S> stateList,
		EdgeFilter<S,A> filter,
		CompsVisitor<S,A> visitor
	) {
		return computeComponents(
			stateList.iterator(), filter, visitor, RECURSIVE
		);		
	}

	/**
	 * Calcola tutte le componenti fortemente connesse 
	 * del grafo ristretto agli archi specificati dal filtro.
	 * Calcola anche la convergenza degli stati (ossia la 
	 * possibilit� di generare sequenze infinite di azioni).
	 * Il valore di ritorno � un oggetto che contiene molte informazioni
	 * sugli stati del sistema.
	 * <p>
	 * Il visitor pu� essere null se non si desidera effettuare
	 * nessun lavoro speciale durante l'analisi delle componenti.
	 * <p>
	 * Usa un algoritmo non ricorsivo.
	 **/
	public LoopInfo<S,A> computeComponents(
		EdgeFilter<S,A> filter,
		CompsVisitor<S,A> visitor
	) {
		return computeComponents(
			graph.getStatesIterator(), filter, visitor, NON_RECURSIVE
		);		
	}

	/**
	 * Calcola tutte le componenti fortemente connesse 
	 * del grafo ristretto agli archi specificati dal filtro.
	 * Calcola anche la convergenza degli stati (ossia la 
	 * possibilit� di generare sequenze infinite di azioni).
	 * Il valore di ritorno � un oggetto che contiene molte informazioni
	 * sugli stati del sistema.
	 * <p>
	 * Il visitor pu� essere null se non si desidera effettuare
	 * nessun lavoro speciale durante l'analisi delle componenti.
	 * <p>
	 * Usa un algoritmo ricorsivo.
	 **/
	public LoopInfo<S,A> computeComponentsRec(
		EdgeFilter<S,A> filter,
		CompsVisitor<S,A> visitor
	) {
		return computeComponents(
			graph.getStatesIterator(), filter, visitor, RECURSIVE
		);		
	}

	/** 
	 * Visitor per la visita che analizza le componenti fortemente connesse. 
	 **/
	public static interface CompsVisitor<
		S1 ,
		A1 
	> {
		/** Chiamato quando viene creata una componente. **/
		void openComponent(
			int compIndex, Map<S1, StateInfo<S1>> infoMap
		);
		/** Chiamato quando viene completata una componente. **/
		void closeComponent(
			int compIndex, Map<S1, StateInfo<S1>> infoMap
		);
		/** Chiamato quando viene aggiunto un elemento a una componente. **/
		void addToComponent(
			StateInfo<S1> stateInfo, int compIndex, Map<S1, StateInfo<S1>> infoMap
		);
	}

	private LoopInfo<S,A> computeComponents(
		Iterator<S> iterator,
		EdgeFilter<S,A> filter,
		CompsVisitor<S,A> visitor,
		int recFlag
	) {
		// init maps
		dfsIndexMap = new HashMap<S, Integer>((int)(graph.getNumberOfStates()/0.75) + 1);
		infoMap = new HashMap<S, StateInfo<S>>((int)(graph.getNumberOfStates()/0.75) + 1);
        globalDfs = 0;
		dfsStack = new LinkedList<S>();
		
		// compute components of every required state 
		if( recFlag == RECURSIVE ) {
			while( iterator.hasNext() )
				dfsComponentsRec( iterator.next(), filter, visitor );
		} else {
			while( iterator.hasNext() )
				dfsComponents( iterator.next(), filter, visitor );
		}
			
		return new LoopInfo<S,A>(graph, dfsIndexMap, infoMap);		
	}

	/** 
	 * Calcola le componenti del sottografo associato allo stato specificato. 
	 * Calcola anche la convergenza degli stati (ossia la 
	 * possibilit� di generare sequenze infinite).
	 * <p>
	 * Il metodo � ricorsivo ed � basato sul classico
	 * algoritmo di Tarjan (si veda Sedgewick, Algorithms, strong components).
	 * <p>
	 * Il visitor pu� essere null se non si desidera effettuare
	 * nessun lavoro speciale durante l'analisi delle componenti.
	 **/
	private int dfsComponentsRec(
		S state, 
		EdgeFilter<S,A> filter,
		CompsVisitor<S,A> visitor
	) {
		// if state has already been treated, return its root
		Integer rootI = dfsIndexMap.get(state);
		if( rootI!=null )
			return rootI;

		// initialize state info			
		int myDfs = ++globalDfs;	
		boolean convergent = true;
		infoMap.put( state, new StateInfo<S>(state, myDfs, convergent) );		
		dfsIndexMap.put( state, myDfs );
		int root = myDfs;		
		int stackSize = dfsStack.size();
		dfsStack.add( state );			
		boolean leaf = true;
		
		// treat state children
		Map<A, ? extends Set<S>> postsetMap = graph.getPostsetMapping(state);
		boolean selfLoops = false;
		if( postsetMap!=null ) {
			for( A action: postsetMap.keySet() ) {
				for( S post: postsetMap.get(action) ) {
					if( !filter.check(state, action, post) )
						continue;
					leaf = false;
					rootI = dfsComponentsRec(post, filter, visitor);
					if( rootI < root )
						root = rootI;
					if( post==state ) {
						selfLoops = true;
						convergent = false;
					} else {
						convergent &= infoMap.get(post).isConvergent();
					}
				}				
			}
		}		
		
		// deduce state info from children info
		infoMap.get(state).setLeaf(leaf);
		
		if( root == myDfs ) {				// state is a root
			int cycleCount = dfsStack.size() - stackSize;
			int loop = cycleCount>1 ? cycleCount : selfLoops ? 1 : 0;
			convergent &= loop==0;
			int compIndex = graph.getNumberOfStates() + myDfs;
			if( visitor!=null )
				visitor.openComponent(compIndex, infoMap);
			for( int i=0; i<cycleCount; ++i ) {
				S item = dfsStack.removeLast();
				dfsIndexMap.put( item, compIndex );
				StateInfo<S> itemInfo = infoMap.get(item);
				itemInfo.setComponentSize(loop);
				itemInfo.setConvergent( convergent );
				itemInfo.setComponent( compIndex );								
				if( visitor!=null )
					visitor.addToComponent(itemInfo, compIndex, infoMap);
			} 
			if( visitor!=null )
				visitor.closeComponent(compIndex, infoMap);
		} else {							// state is not a root
			infoMap.get(state).setConvergent( convergent );
		}
		
		return root;
	}

	/** 
	 * Calcola le componenti del sottografo associato allo stato specificato
	 * senza usare la ricorsione. 
	 * Calcola anche la convergenza degli stati (ossia la 
	 * possibilit� di generare sequenze infinite).
	 * L'algoritmo � molto complesso e delicato perch� deve 
	 * simulare la ricorsione mettendo le informazioni in uno stack.
	 * Il metodo � la versione non ricorsiva del classico
	 * algoritmo di Tarjan (si veda Sedgewick, Algorithms, strong components).
	 **/	
	private void dfsComponents(
		S state, 
		EdgeFilter<S,A> filter,
		CompsVisitor<S,A> visitor
	) {
		// if state has already been treated, return its root
		Integer rootI = dfsIndexMap.get(state);
		if( rootI!=null )
			return;
		
		// variables
		Map.Entry<A,S> entry;
		S post;
		CompsInfo info;
		
		// initialize recursion stack and structures
		int root = -1;
		boolean convergent = true;
		boolean enter = true;
		LinkedList<CompsInfo> recStack = new LinkedList<CompsInfo>();
		recStack.add( new CompsInfo(graph, state, filter) );

		// simulate recursion
		while( !recStack.isEmpty() ) {
			// get a state from recursion stack
			info = recStack.removeLast();
			
			if( enter ) {				// entering a new state: initialize				
				dfsIndexMap.put( info.state, ++globalDfs );
				info.dfs = globalDfs;
				info.root = globalDfs;
				info.stackSize = dfsStack.size();				
				dfsStack.add( info.state );
				infoMap.put(info.state, new StateInfo<S>(info.state,info.dfs,true));		
			} else {					// returning from a child
				info.convergent &= convergent;
				if( root<info.root )
					info.root = root;
			} 
			
			post = null;				// get next child
			if( info.postIter.hasNext() ) {
				entry = info.postIter.next();
				post = entry.getValue();
				info.leaf = false;
			}
							
			if( post!=null ) {			// if child not null
				if( post==info.state ) {// check for selfloops
					info.selfLoops = true;
					info.convergent = false;
				}
				rootI = dfsIndexMap.get(post);
				recStack.add( info );	// push father
				if( rootI==null ) {		// child has not been treated yet
					recStack.add( new CompsInfo( graph, post, filter ) );
					enter = true;		// push it and treat it
				} else {				// child has already been treated
					root = rootI;
					convergent = infoMap.get(post).isConvergent();					
					enter = false;		// set return values and return
				}
			} else {					// no more children
				if( info.root == info.dfs ) {	// the treated state is a root
					int cycleCount = dfsStack.size() - info.stackSize;
					int loop = cycleCount>1? cycleCount : info.selfLoops? 1 : 0;
					info.convergent &= loop==0;
					int compIndex = graph.getNumberOfStates() + info.dfs;
					if( visitor!=null )
						visitor.openComponent(compIndex, infoMap);
					for( int i=0; i<cycleCount; ++i ) {						
						S item = dfsStack.removeLast();
						dfsIndexMap.put( item, compIndex );
						StateInfo<S> itemInfo = infoMap.get(item);
						itemInfo.setComponentSize(loop);
						itemInfo.setConvergent( info.convergent );
						itemInfo.setComponent( compIndex );
						if( visitor!=null )
							visitor.addToComponent(itemInfo, compIndex, infoMap);
					} 
					if( visitor!=null )
						visitor.closeComponent(compIndex, infoMap);
				} else {				// the treated state is not a root
					infoMap.get(state).setConvergent( info.convergent );
				}						
				root = info.root;		// set return values and return			
				convergent = info.convergent;
				enter = false;
				infoMap.get(info.state).setLeaf(info.leaf);
			}
		}
	}

	/**
	 * Calcola il rank dei punti del grafo. <p>
	 * Usa un algoritmo ricorsivo.
	 **/
	public RankInfo<S,A> computeRankRec(
		EdgeFilter<S,A> filter
	) {
		return computeRank(filter, RECURSIVE);
	}

	/**
	 * Calcola il rank dei punti del grafo. <p>
	 * Usa un algoritmo non ricorsivo.
	 **/
	public RankInfo<S,A> computeRank(
		EdgeFilter<S,A> filter
	) {
		return computeRank(filter, NON_RECURSIVE);
	}


	/**
	 * Calcola il rank dei punti del grafo.  
	 **/
	private RankInfo<S,A> computeRank(
		EdgeFilter<S,A> filter,
		int recFlag
	) {
		// init maps
		dfsIndexMap = new HashMap<S,Integer>((int)(graph.getNumberOfStates()/0.75) + 1);
		infoMap = new HashMap<S,StateInfo<S>>((int)(graph.getNumberOfStates()/0.75) + 1);

		// compute components
        globalDfs = 0;
		dfsStack = new LinkedList<S>();
		Iterator<S> iter = graph.getStatesIterator();
		if( recFlag == RECURSIVE ) {
			while( iter.hasNext() )
				dfsComponentsRec(iter.next(), filter, null );	
		} else {
			while( iter.hasNext() )
				dfsComponents(iter.next(), filter, null );	
		}
		
		// compute rank (using components)
        globalDfs = 0;
		maxRank = -1;
		iter = graph.getStatesIterator();
		if( recFlag == RECURSIVE ) {
			while( iter.hasNext() )
				computeRankRec(iter.next(), filter );
		} else {
			while( iter.hasNext() )
				computeRank(iter.next(), filter );
		}

		return new RankInfo<S,A>(graph, dfsIndexMap, infoMap, maxRank);		
	}


	/** 
	 * Calcola il rango degli elementi del sottografo 
	 * associato allo stato specificato. 
	 * Il metodo � ricorsivo.
	 **/	
	private int computeRankRec(
		S state, 
		EdgeFilter<S,A> filter
	) {
		StateInfo<S> stateInfo = infoMap.get(state);
		int rank = stateInfo.getRank();
		if( rank!=StateInfo.UNSET )
			return rank;
			
		int myDfs = ++globalDfs;	
		// -1 plays the role of -infinity
		// -2 plays the role of 'work in progress' for non-loop states
		rank = stateInfo.getComponentSize()==0 ? -2 : -1;
		stateInfo.setRank( rank );
		int stackSize = dfsStack.size();
		dfsStack.add( state );			
		
		Map<A, ? extends Set<S>> postsetMap = graph.getPostsetMapping(state);
		if( postsetMap!=null ) {
			for( A action: postsetMap.keySet() ) {
				for( S post: postsetMap.get(action) ) {
					if( !filter.check(state, action, post) )
						continue;
					int rankPost = infoMap.get(post).getRank();
					if( rankPost==StateInfo.UNSET )
						rankPost = computeRankRec(post, filter);
					if( infoMap.get(post).isConvergent() ) {
						if( rankPost>=rank ) {
							rank = rankPost + 1;
							stateInfo.setRank(rank);
						}
					} else if( rankPost>rank ) {
						rank = rankPost;								
						stateInfo.setRank(rank);
					}
				}										
			}
		}
		
		if( rank==-2 )
			rank=0;
		
		if( dfsIndexMap.get(state) == graph.getNumberOfStates() + myDfs ) {	// root
			int cycleCount = dfsStack.size() - stackSize;
			if( rank > maxRank )
				maxRank = rank;
			for( int i=0; i<cycleCount; ++i )
				infoMap.get( dfsStack.removeLast() ).setRank( rank );
		}
		
		return rank;
	}

	/** 
	 * Calcola il rango degli elementi del sottografo 
	 * associato allo stato specificato. Non usa la ricorsione.
	 * L'algoritmo � molto complesso e delicato perch� deve 
	 * simulare la ricorsione mettendo le informazioni in uno stack.
	 **/ 
	private void computeRank(
		S state, 
		EdgeFilter<S,A> filter
	) {
		int rank = infoMap.get(state).getRank();
		if( rank!=StateInfo.UNSET )
			return;
			
		Map.Entry<A,S> entry;
		S post;		
		CompsInfo info;		
		
		rank = -2;
		int rankPost;
		boolean postLoop = false;
		boolean enter = true;
		LinkedList<CompsInfo> recStack = new LinkedList<CompsInfo>();
		recStack.add( new CompsInfo(graph, state, filter) );

		while( !recStack.isEmpty() ) {
			info = recStack.removeLast();
			StateInfo<S> stateInfo = infoMap.get(info.state);
			if( enter ) {				// enter
				info.rank = stateInfo.getComponentSize()==0 ? -2 : -1;
				stateInfo.setRank( info.rank );
				info.dfs = ++globalDfs;
				info.stackSize = dfsStack.size();
				dfsStack.add( info.state );
			} else {					// exit
				if( !postLoop ) {
					if( rank>=info.rank ) {
						info.rank = rank + 1;
						stateInfo.setRank( info.rank );
					}
				} else if( rank>info.rank ) {
					info.rank = rank;								
					stateInfo.setRank( info.rank );
				}
			}			
			
			post = null;
			if( info.postIter.hasNext() ) {
				entry = info.postIter.next();
				post = entry.getValue();
			}
							
			if( post!=null ) {
				rankPost = infoMap.get(post).getRank();
				recStack.add( info );
				if( rankPost==StateInfo.UNSET ) {
					recStack.add( new CompsInfo(graph, post, filter) );
					enter = true;
				} else {
					rank = rankPost;
					postLoop = !infoMap.get(post).isConvergent();
					enter = false;					
				}
			} else {
				if( info.rank==-2 )
					info.rank=0;
				if( dfsIndexMap.get(info.state) == graph.getNumberOfStates()+info.dfs ) {
					int cycleCount = dfsStack.size() - info.stackSize;
					if( info.rank > maxRank )
						maxRank = info.rank;
					for( int i=0; i<cycleCount; ++i )
						infoMap.get(dfsStack.removeLast()).setRank( info.rank );
				}
				rank = info.rank;					
				enter = false;
				postLoop = !infoMap.get(info.state).isConvergent();
			}
		}
	}

		
// _____________________________________________________________________________
// Associated graph

	/** 
	 * Costruisce l'insieme di accettazione dell'inisieme 
	 * di stati specificato. 
	 **/
	public AcceptanceSet<A> acceptanceSet(
		Collection<S> set,
		A tauAction
	) {
		AcceptanceSet<A> acceptance = new AcceptanceSet<A>();
		for( S state: set ) {
			if( graph.getPostset(state, tauAction)!=null )
				continue;
			Map<A,? extends Set<S>> postsetMap = graph.getPostsetMapping(state);
			if( postsetMap!=null )
				acceptance.add( new ActionSet<A>(postsetMap.keySet()) );
			else 
				return new AcceptanceSet<A>(true);
		}
		
		return acceptance;
	}	

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

	/**
	 * Costruisce il grafo di accettazione associato al grafo specificato
	 * secondo i flag specificati in <code>equivalence</code>. 
	 * Il secondo stato iniziale pu� essere null.
	 **/
	public PointedGraph<AcceptanceState<A>, A> buildAssociatedGraph(
	 	S initState1,
	 	S initState2,
	 	int equivalence
	) {
		// options for building associated graph
		AcceptanceBuilder<S,A> builder = new AcceptanceBuilder<S,A>(
			this, initState1, initState2, equivalence 
		);
		
		// build associated graph
		builder.buildGraph();
		
//		partitionMap = builder.getPartitionMap();		
								
		// return PointedGraph
		return new PointedGraph<AcceptanceState<A>,A>( 
			builder.getNewGraph(), 
			builder.getInitState1(), 
			builder.getInitState2()
		);
	}


// _____________________________________________________________________________
// Visita generica non ricorsiva in profondit�
// IDEA: breadthFirstVisit

	/** 
	 * Visita generica in profondit� non ricorsiva 
	 * del grafo ristretto agli archi specificati dal filtro
	 * a partire dagli stati specificati. <p>
	 * Il DepthVisitor pu� esser null.
	 **/
	public void depthVisit(
		EdgeFilter<S,A> filter,
		DepthVisitor<S,A> visitor,
		boolean fwd
	) {
		depthVisit( filter, visitor, fwd, NON_RECURSIVE );
	}

	/** 
	 * Visita generica in profondit� ricorsiva 
	 * del grafo ristretto agli archi specificati dal filtro
	 * a partire dagli stati specificati. <p>
	 * Il DepthVisitor pu� esser null.
	 * (non testato)
	 **/
	public void depthVisitRec(
		EdgeFilter<S,A> filter,
		DepthVisitor<S,A> visitor,
		boolean fwd
	) {
		// TODO: depthVisitRec non testato
		depthVisit( filter, visitor, fwd, RECURSIVE );
	}
	
	/** 
	 * Visita generica in profondit� ricorsiva 
	 * del grafo ristretto agli archi specificati dal filtro.
	 * Il DepthVisitor pu� esser null.
	 **/
	private void depthVisit(
		EdgeFilter<S,A> filter,
		DepthVisitor<S,A> visitor,
		boolean fwd,
		int recFlag
	) {
		// init global variables
		dfsIndexMap = new HashMap<S, Integer>((int)(graph.getNumberOfStates()/0.75) + 1);
        globalDfs = 0;

		if( visitor==null )
			visitor=new DepthVisitorAdapter<S,A>();

		// visit graph
		Iterator<S> stateIter = graph.getStatesIterator();
		if( recFlag == RECURSIVE ) {
			while( stateIter.hasNext() ) {			
				if( !dfsVisitRec( 
					filter, visitor, fwd, null /*from*/, -1 /*fromIndex*/,
					null /*action*/, stateIter.next() /*to*/, -1 /*depth*/
				))
					break; 	
			}
		} else {
			while( stateIter.hasNext() )
				if(	!dfsVisit( stateIter.next(), filter, visitor, fwd) )
					break; 	
		}
	}

	/** 
	 * Visita generica in profondit� non ricorsiva
	 * a partire da un solo stato. <p>
	 * Il DepthVisitor pu� esser null.
	 **/
	public void depthVisit(
		S state,
		EdgeFilter<S,A> filter,
		DepthVisitor<S,A> visitor,
		boolean fwd
	) {
		// init global variables
		dfsIndexMap = new HashMap<S, Integer>((int)(graph.getNumberOfStates()/0.75) + 1);
        globalDfs = 0;		
		
		if( visitor==null )
			visitor=new DepthVisitorAdapter<S,A>();
		
		// visit subgraph of state
		dfsVisit( state, filter, visitor, fwd );
	}

	/** 
	 * Visita generica in profondit� ricorsiva
	 * a partire da un solo stato. <p>
	 * Il DepthVisitor pu� esser null.
	 * (non testato)
	 **/
	public void depthVisitRec(
		S state,
		EdgeFilter<S,A> filter,
		DepthVisitor<S,A> visitor,
		boolean fwd
	) {
		// init global variables
		dfsIndexMap = new HashMap<S, Integer>((int)(graph.getNumberOfStates()/0.75) + 1);
        globalDfs = 0;		
		
		if( visitor==null )
			visitor=new DepthVisitorAdapter<S,A>();
		
		// visit subgraph of state
		dfsVisitRec( 
			filter, visitor, fwd, null /*from*/, -1 /*fromIndex*/,
			null /*action*/, state /*to*/, -1 /*depth*/
		);
	}

	/** 
	 * Classe che contiene le informazioni su uno stato
	 * raccolte durante una visita in profondit� 
	 * (senza ricorsione) del grafo.
	 **/
	private class DfsInfo {		
		// Stato di cui si tengono le informazioni
		S state;		
		// iteratore dell'immagine dello stato
		Iterator<Map.Entry<A,S>> postIter;
		// indice dello stato durante la visita in profondit�
		int dfs = -1;
		// azione che ha portato allo stato
		A enterAction = null;
		
		// costruttore
		DfsInfo( 
			GraphData<S,A> graph, 
			S state, 
			A enterAction, 
			EdgeFilter<S,A> filter,
			boolean fwd
		) {
			this.state = state;		
			this.enterAction = enterAction;
			this.postIter = graph.getImageIterator( state, fwd, filter );
		}
	}

	/** 
	 * Visita generica in profondit� non ricorsiva. 
	 * Rende <tt>false</tt> se la visita si deve arrestare.
	 **/
	private boolean dfsVisit(
		S state, 
		EdgeFilter<S,A> filter,
		DepthVisitor<S,A> visitor,
		boolean fwd
	) {
		// if state has already been treated, return
		Integer dfsPost = dfsIndexMap.get(state);
		if( dfsPost!=null )
			return true;
		
		// variables
		Map.Entry<A,S> entry;
		S post;
		DfsInfo info;
		
		// initialize recursion stack and structures
		boolean enter = true;
		S from = null;
		int fromIndex = -1;
		int depth = -1;
		A action = null;
		
		LinkedList<DfsInfo> recStack = new LinkedList<DfsInfo>();
		recStack.add( new DfsInfo(graph, state, action, filter, fwd) );

		boolean go;
		while( !recStack.isEmpty() ) {		// simulate recursion
			info = recStack.removeLast();	// get a state from recursion stack
			
			if( enter ) {					// entering a new state: initialize
				dfsIndexMap.put( info.state, ++globalDfs );
				info.dfs = globalDfs;				
				go = visitor.goingTo(								   /*going*/
					depth, from, fromIndex, action, info.state, info.dfs
				);
				if(!go) 
					return false;	
				depth++;
			} else {						// returning from a child
				depth--;
				go = visitor.comingBack( 								/*back*/
					depth, info.state, info.dfs, action, from, fromIndex
				);
				if(!go)
					return false;
			} 
			from = info.state;
			fromIndex = info.dfs;
			
			post = null;					// get next child
			if( info.postIter.hasNext() ) {
				entry = info.postIter.next();
				post = entry.getValue();
				action = entry.getKey();				
			}
							
			if( post!=null ) {				// if child not null
				dfsPost = dfsIndexMap.get(post);
				recStack.add( info );		// push father
				if( dfsPost==null ) {		// child has not been treated yet
					recStack.add( new DfsInfo(graph,post,action,filter,fwd) );
					enter = true;			// push it and treat it
				} else {					// child has already been treated
					go = visitor.hitMarked(							 	 /*hit*/
						depth, from, fromIndex, action, post, dfsPost
					);
					from = post;
					fromIndex = dfsPost;
					if(!go)
						return false;
					enter = false;			// return
				}
			} else {						// no more children
				action = info.enterAction;
				enter = false;
			}
		}
		
		return visitor.comingBack( 										/*back*/
			depth, null, -1, null, from, fromIndex
		);
	}


	/** 
	 * Rende false se la visita si deve arrestare. 
	 * Assume che lo stato specificato abbia gi� un suo dfsIndex.
	 **/
	private boolean dfsVisitRec(
		EdgeFilter<S,A> filter,
		DepthVisitor<S,A> visitor,
		boolean fwd,
		S from,
		int fromIndex,
		A action,
		S state,
		int depth
	) {
		Integer dfsState = dfsIndexMap.get(state);
		if( dfsState!=null ) {
			return !visitor.hitMarked(
				depth, from, fromIndex, action, state, dfsState
			);
		}
		
		dfsState = ++globalDfs;
		dfsIndexMap.put(state, dfsState);
		
		if(	!visitor.goingTo(depth, from, fromIndex, action, state, dfsState) )
			return false; 

		++depth;
		
		// treat state children
		Map<A, ? extends Set<S>> map = 
			fwd ? graph.getPostsetMapping(state) : graph.getPresetMapping(state);
		if( map==null ) 
			return true;
		
		for( A act: map.keySet() ) {
			for( S post: map.get(action) ) {
				if( !filter.check(state, act, post) )
					continue;	
				if( !dfsVisitRec(
						filter, visitor, fwd, state, dfsState, act, post, depth
				)) {
					--depth;
					return false;
				}
				if( !visitor.comingBack(
					depth, state, dfsState, act, post, dfsIndexMap.get(post)
				)) {
					--depth;
					return false;
				}
			}				
		}
		
		--depth;	
		return true;
	}


// _____________________________________________________________________________
// Delabeller

	/**
	 * Costruisce un nuovo grafo in cui le etichette sugli archi
	 * sono sostituite con nodi intermedi etichettati. <p>
	 * Poggia su una visita non ricorsiva.
	 **/
	public <B extends ActionInterface> 
	ExpandedGraph<S, LabelledState<ActionLabel<A>>, B> deLabel( 
		B soleAction
	) {
		DeLabelVisitor<S,A,B> delabeller = new DeLabelVisitor<S,A,B>(
			graph, soleAction
		);
		depthVisit( TRANSITIONS_ALL, delabeller, true );
		
		return new ExpandedGraph<S, LabelledState<ActionLabel<A>>, B>(
			delabeller.getNewGraph(),
			delabeller.getMap()
		);		
	}
	
	
	/** 
	 * Visitor di grafi per togliere le etichette dagli
	 * archi e sostituirle con stati etichettati.
	 **/
	private static class DeLabelVisitor<
		S1 ,
		A1 extends ActionInterface,
		B1 extends ActionInterface
	> implements DepthVisitor<S1,A1> {
		
		// private data
		private GraphData<S1,A1> graph;
		private GraphInterface<LabelledState<ActionLabel<A1>>, B1> newGraph;
		private HashMap< S1, LabelledState<ActionLabel<A1>> > map;
		private B1 soleAction;
		
		/** Costruttore. **/
		public DeLabelVisitor(
			GraphData<S1,A1> graph,
			B1 soleAction
		) {
			this.graph = graph;
			this.soleAction = soleAction;
			map = new HashMap< S1, LabelledState<ActionLabel<A1>> >(
				(int) (graph.getNumberOfStates()/0.75) + 1
			);
			newGraph = new Graph<LabelledState<ActionLabel<A1>>, B1>();
			initializeNewGraph();
		}
		
		private void initializeNewGraph() {
			Iterator<S1> i = graph.getStatesIterator();
			while( i.hasNext() ) {
				S1 oldState = i.next();
				LabelledState<ActionLabel<A1>> newState = 
					new LabelledState<ActionLabel<A1>>( 
						oldState.toString(), new ActionLabel<A1>(null)
					);
				map.put( oldState, newState );
				newGraph.addState( newState );
			}		
		}
		
		public GraphInterface<LabelledState<ActionLabel<A1>>, B1> getNewGraph() {
			return newGraph;
		}
		
		public Map< S1, LabelledState<ActionLabel<A1>> > getMap() {
			return map;
		}
		
		private void addEdge(S1 from, A1 action, S1 to) {
			LabelledState<ActionLabel<A1>> mediator = 
				new LabelledState<ActionLabel<A1>>(	
					"S"+newGraph.getNumberOfStates(), new ActionLabel<A1>(action)
				);
				
			newGraph.addEdge( map.get(from), soleAction, mediator );
			newGraph.addEdge( mediator, soleAction, map.get(to) );
		}
		
		/** 
		 * Metodo chiamato quando la visita scende in profondit�. 
		 * Lo si usa per creare gli archi del nuovo grafo.
		 **/
		public boolean goingTo(
			int fromDepth, S1 from, int fromIndex, A1 action, S1 to, int toIndex
		) {
			if( fromIndex != -1 )
				addEdge(from, action, to);															
			return true;			
		}
		
		/** 
		 * Metodo chiamato quando la visita ritorna sui suoi passi. 
		 * Rende true.
		 **/
		public boolean comingBack(
			int toDepth, S1 to, int toIndex, A1 action, S1 from, int fromIndex
		) {
			return true;
		}
		
		/** 
		 * Metodo chiamato quando la visita colpisce uno stato gi� visitato.
		 * Lo si usa per creare gli archi del nuovo grafo.
		 **/
		public boolean hitMarked(
			int fromDepth, S1 from, int fromIndex, A1 action, S1 hit, int hitIndex
		) {
			addEdge(from, action, hit);						
			return true;
		}
	}

}


