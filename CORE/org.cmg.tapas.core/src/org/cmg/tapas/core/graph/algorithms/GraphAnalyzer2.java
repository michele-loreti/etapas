package org.cmg.tapas.core.graph.algorithms;



import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.cmg.tapas.core.graph.ActionInterface;
import org.cmg.tapas.core.graph.EdgeFilter;
import org.cmg.tapas.core.graph.GraphData;
import org.cmg.tapas.core.graph.GraphInterface;
import org.cmg.tapas.core.graph.GraphItemFactoryI;
import org.cmg.tapas.core.graph.MappedGraph;
import org.cmg.tapas.core.graph.PointedGraph;

/** 
 * GraphAnalyzer2 offre vari metodi per analizzare e trasformare 
 * i grafi.
 * I metodi che non richiedono l'uso di fabbriche possono
 * essere usati anche per mezzo di GraphAnalyzer.
 * <p>
 * @param <S> tipo degli stati
 * @param <A> tipo delle azioni
 * 
 * @author Guzman Tierno
 **/ 
public class GraphAnalyzer2<
	S ,
	A extends ActionInterface
> extends GraphAnalyzer<S,A> {
	
	// Fabbrica di grafi.
	private GraphItemFactoryI<S,A> graphFactory;
		
	/** 
	 * Costruttore.
	 **/
	public GraphAnalyzer2(
		GraphData<S,A> graph,
		GraphItemFactoryI<S,A> graphFactory
	){
		super(graph);
		this.graphFactory = graphFactory;
	}

	/** Setta la fabbrica di grafi. **/
	public void setGraphFactory(GraphItemFactoryI<S,A> graphFactory) {
		this.graphFactory = graphFactory;
	}

// _____________________________________________________________________________
// Collapse and fusion

	/**
	 * Collassa i loop del grafo specificato. 
	 * Gli archi considerati nei loop sono quelli che soddisfano
	 * il filtro specificato.
	 * Gli archi del grafo collassato sono quelli che 
	 * soddisfano il secondo filtro specificato.
	 **/
	public MappedGraph<S,A,S> collapseLoops(
		EdgeFilter<S,A> loopEdges,
		EdgeFilter<S,A> acceptEdges
	) {
		GraphInterface<S,A> newGraph = graphFactory.newGraph();
		HashMap<S,S> map = new HashMap<S,S>();
		computeComponents( loopEdges, new CollapseVisitor(newGraph, map) );
		depthVisit( 
			TRANSITIONS_ALL, 
			new EdgeVisitor(newGraph, map, acceptEdges),
			true
		);
		
		return new MappedGraph<S,A,S>(newGraph, map);
	}
	
	
	/** 
	 * Visitatore per la prima visita di collapseLoops().
	 * Collassa i nodi di uno stesso loop creando il corrispondente 
	 * nodo nel nuovo grafo.
	 **/
	private class CollapseVisitor implements CompsVisitor<S,A> {
		/** Nuovo grafo in creazione. **/
		private GraphInterface<S,A> newGraph;
		/** Mappa di corrispondenza tra vecchi e nuovi stati. **/
		private HashMap<S,S> map;
		/** Componente in fase di creazione. **/
		private S component;

		/** Costruttore. **/
		CollapseVisitor(
			GraphInterface<S,A> newGraph,
			HashMap<S,S> map
		) {
			this.newGraph = newGraph;
			this.map = map;
		}
		
		/** Chiamato quando viene creata una componente. **/
		public void openComponent(
			int compIndex, Map<S, StateInfo<S>> infoMap
		) {
			component = graphFactory.newState("S"+newGraph.getNumberOfStates());
			newGraph.addState(component);
		}
		
		/** Chiamato quando viene completata una componente. **/
		public void closeComponent(
			int compIndex, Map<S, StateInfo<S>> infoMap
		) {			
			// do nothing
		}
		
		/** Chiamato quando viene aggiunto un elemento a una componente. **/
		public void addToComponent(
			StateInfo<S> stateInfo, int compIndex, Map<S, StateInfo<S>> infoMap
		) {
			map.put(stateInfo.getState(), component);
		}
	}


	/** 
	 * Visitatore per la seconda visita di collapseLoops().
	 * Crea gli archi nel grafo collassato.
	 **/
	private class EdgeVisitor implements DepthVisitor<S,A> {
		/** Nuovo grafo in creazione. **/
		private GraphInterface<S,A> newGraph;
		/** Mappa di corrispondenza tra vecchi e nuovi stati. **/
		private HashMap<S,S> map;
		/** Filtro per i gli archi nella costruzione del nuovo grafo. **/
		EdgeFilter<S,A> acceptEdges;

		/** Costruttore. **/
		EdgeVisitor(
			GraphInterface<S,A> newGraph,
			HashMap<S,S> map,
			EdgeFilter<S,A> acceptEdges
		) {
			this.newGraph = newGraph;
			this.map = map;
			this.acceptEdges = acceptEdges;
		}

		/** Gestisce la visita di un arco. **/
		private void treat(S src, A action, S dest) {
			S newSrc = map.get( src );
			S newDest = map.get( dest );
			if( acceptEdges.check( newSrc, action, newDest ) )
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
	
	/** 
	 * Rende un nuovo grafo ottenuto accorpando gli stati
	 * all'interno di una stessa classe della partizione specificata.
	 * Due stati sono considerati essere nella stessa classe 
	 * se la mappa specificata assegna loro lo stesso valore (==).
	 * Si ammette che la partizione non sia esaustiva.
	 **/
	public <V> MappedGraph<S,A,V> collapse(
		Evaluator<S,V> partitionMap
	) {
		return collapse( partitionMap, TRANSITIONS_ALL );
	}

	/** 
	 * Rende un nuovo grafo ottenuto accorpando gli stati
	 * all'interno di una stessa classe della partizione specificata.
	 * Due stati sono considerati essere nella stessa classe 
	 * se la mappa specificata assegna loro lo stesso valore (==).
	 * Si ammette che la partizione sia non esaustiva.
	 **/ 
	public <V> MappedGraph<S,A,V> collapse(	
		Evaluator<S,V> partitionMap,
		EdgeFilter<S,A> filter
	) {
		GraphInterface<S,A> newGraph = graphFactory.newGraph();
		HashMap<V,S> map = new HashMap<V,S>();
	
		S state;
		S newState;
		V value;
		Iterator<S> iter = graph.getStatesIterator();
		int stateCount = 0;
		while( iter.hasNext() ) {
			state = iter.next();
			value = partitionMap.eval(state);
			if( value==null	)
				continue;
			newState = map.get(value);
			if( newState==null ) {
				newState = graphFactory.newState(Integer.toString(stateCount++));
				map.put( value, newState );	
				newGraph.addState(newState);
			}							
		}

	    Iterator<Map.Entry<A,S>> image; 
	    Map.Entry<A,S> entry;
		iter = graph.getStatesIterator();
		V postValue;
		A action;
		S newPost;
		while( iter.hasNext() ) {
			state = iter.next();
			value = partitionMap.eval(state);
			if( value==null	)
				continue;
			newState = map.get( value );
	    	image = graph.getImageIterator(state, true, filter );
	    	while( image.hasNext() ) {
	    		entry = image.next();
	    		action = entry.getKey();
	    		postValue = partitionMap.eval( entry.getValue() );		    		   		
	    		newPost = map.get(postValue);
	    		if( postValue!=null )
	    			newGraph.addEdge(newState, action, newPost);
	    	}
		}
		
		return new MappedGraph<S,A,V>( newGraph, map );
	}
	
	/**
	 * Crea un nuovo grafo fondendo il grafo 
	 * dell'analizzatore con quello specificato.
	 * Rende il nuovo grafo con associati i due stati
	 * corrispondenti agli stati iniziali specificati.
	 * Gli stati iniziali possono essere nulli. 
	 **/
	public static <
		S ,
		A extends ActionInterface
	> PointedGraph<S,A> fusion(
		GraphItemFactoryI<S,A> graphFactory,
		GraphData<S,A> graph1,
		GraphData<S,A> graph2,
		S initState1,
		S initState2
	) {
		GraphInterface<S,A> newGraph = graphFactory.newGraph();
		S newInitState1 = fusion(
			graphFactory, newGraph, graph1, initState1, 1
		);
		S newInitState2 = fusion(
			graphFactory, newGraph, graph2, initState2, 2
		);		
		
		return new PointedGraph<S,A>(newGraph, newInitState1, newInitState2);
	}
	
	/** Aggiunge 'graph' al nuovo grafo con l'indice specificato. **/
	private static <
		S ,
		A extends ActionInterface
	> S fusion(
		GraphItemFactoryI<S,A> graphFactory,
		GraphInterface<S,A> newGraph,
		GraphData<S,A> graph,
		S initState,
		int index
	) {
		HashMap<S,S> map = new HashMap<S,S>((int) (graph.getNumberOfStates()/0.75) + 1);

		S state;
		S newState;
		
		// create states
		Iterator<S> iter = graph.getStatesIterator();
		while( iter.hasNext() ) {
			state = iter.next();
			newState = graphFactory.newState( index + "_" + state.toString() );
			map.put( state, newState );	
			newGraph.addState( newState );
		}
		
	    Iterator<Map.Entry<A,S>> image; 
	    Map.Entry<A,S> entry;

		// create edges
		iter = graph.getStatesIterator();
		while( iter.hasNext() ) {
			state = iter.next();
			newState = map.get( state );
	    	image = graph.getImageIterator(
	    		state, true, GraphAnalyzer2.<S,A>getNoEdgeFilter()
	    	);
	    	while( image.hasNext() ) {
	    		entry = image.next();
	    		newGraph.addEdge( 
	    			newState, entry.getKey(), map.get( entry.getValue() )	    			
	    		);
	    	}
		}
		
		if(initState!=null)
			return map.get(initState);
	
		return null;
	}

// _____________________________________________________________________________
// Graph transformations


	// TODO: buildWDG
	/**
	 * Costruisce ricorsivamente il grafo deterministico
	 * associato al grafo specificato. (non testato).
	 **/
	private S buildDG(
		GraphInterface<S,A> detGraph,
		Set<S> set,
		Set<A> actionSet,
		HashMap<Set<S>,S> map		
	) {
		S state = map.get( set );
		if( state!=null ) 
			return state;
		
		S newState = graphFactory.newState("S"+detGraph.getNumberOfStates());		
		detGraph.addState( newState );
		map.put(set, newState);
		Set<S> image;
		for( A action: actionSet ) {
			image = new FastSet<S>();			// using FastSet
			computeImage(set, image, action);
			if( !image.isEmpty() ) {
				state = buildDG( detGraph, image, actionSet, map );
				detGraph.addEdge( newState, action, state );
			}			
		}

		return newState;
	}

	/**
	 * Costruisce il grafo deterministico associato a quello
	 * specificato. <p>
	 * (NON TESTATO: si usi, per ora,
	 * buildAssociatedGraph(graph,state1,state2,0))
	 **/	
	public PointedGraph<S,A> buildDeterministicGraph( 
		S initState
	) {
		GraphInterface<S,A> detGraph = graphFactory.newGraph();
		Set<S> initSet = new FastSet<S>();		// using FastSet
		initSet.add( initState );
		
		initState = buildDG(
			detGraph, initSet, graph.getActions(), 
			new HashMap<Set<S>,S>()
		);
		
		return new PointedGraph<S,A>( detGraph, initState, null );
	}

// _____________________________________________________________________________

	/** Chiusura tau transitiva. **/
	public MappedGraph<S,A,S> buildTauTransitiveClosure() {
		GraphInterface<S,A> newGraph = graphFactory.newGraph();			
		A tau = graphFactory.getTauAction();

		// states
		HashMap<S,S> map = new HashMap<S,S>(
			(int) (graph.getNumberOfStates()/0.75) + 1 
		);
		Iterator<S> iter = graph.getStatesIterator();
		S state;
		S newState;
		while( iter.hasNext() ) {
			state = iter.next();
			newState = graphFactory.newState( state.toString() );
			map.put( state, newState );
		}
		
		// edges
		Set<S> image;
		Set<S> closure;
		Set<A> actions = graph.getActions();
		iter = graph.getStatesIterator();
		while( iter.hasNext() ) {
			state = iter.next();
			closure = new HashSet<S>();
			closure.add(state);
			computeClosure( closure, tau );
			for( S equiState: closure )
				newGraph.addEdge(map.get(state), tau, map.get(equiState));

			for( A action: actions ){				
				if( action.isTau() ) 
					continue;
				image = new HashSet<S>();
				computeImage(closure, image, action);
				computeClosure( image, tau );
				for( S reached: image )
					newGraph.addEdge(map.get(state), action, map.get(reached));
			}
		}
		
		return new MappedGraph<S,A,S>(newGraph, map);
	}


}



