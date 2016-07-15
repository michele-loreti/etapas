package org.cmg.tapas.core.graph;


import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import org.cmg.tapas.core.graph.filter.Filter;

/** 
 * GraphComposition rappresenta la composizione di due grafi
 * in un grafo unico. 
 * Internamente GraphComposition non ha n� stati n� archi,
 * ma poggia sui due grafi dati per implementare i vari metodi.
 * Il risultato � di tipo GraphData
 * in quanto non tutti i metodi di GraphInterface
 * possono essere supportati. (Se invece si vogliono
 * fondere due grafi ed ottenere un nuovo grafo che implementi
 * GraphInterface si usi <tt>GraphAnalyzer2.fusion(graph1, graph2)</tt>).
 * Il corretto funzionamento della classe GraphComposition
 * presuppone che i due grafi dati non abbiano stati in comune
 * (ma ammette invece stati con lo stesso nome).
 * La classe GraphComposition risulta estremamente comoda ed
 * efficiente per effettuare controlli di equivalenza tra due
 * grafi perch� permette di vedere momentaneamente i due grafi come
 * un grafo unico. Tale meccanismo � reso automatico
 * dalle classi che implementano EquivalenceChecker (si veda
 * per esempio BisimulationChecker).
 * 
 * @param <S> Tipo degli stati del grafo. 
 * @param <A> Tipo delle azioni del grafo.
 * 
 * @see StateInterface
 * @see ActionInterface
 *  
 * @author Guzman Tierno
 * @author Michele Loreti
 */
public class GraphComposition<
	S , 
	A extends ActionInterface
> implements GraphData<S,A> {
	
	private GraphData<S,A> graph1;
	private GraphData<S,A> graph2;
	
	/** Costruisce un nuovo GraphComposition. **/
	public GraphComposition( GraphData<S,A> graph1, GraphData<S,A> graph2 ) {
		this.graph1 = graph1;
		this.graph2 = graph2;
	}
	
	/** Rende il primo grafo di questo grafo composto. **/
	public GraphData<S,A> getGraph1() {
		return graph1;
	}

	/** Rende il secondo grafo di questo grafo composto. **/
	public GraphData<S,A> getGraph2() {
		return graph2;
	}
	
// _____________________________________________________________________________
// get States 

	/** 
	 * Restituisce il numero degli stati del grafo.
	 **/
	public int getNumberOfStates() {
		return graph1.getNumberOfStates() + graph2.getNumberOfStates();
	}
	
	/** 
	 * Dice se lo stato specificato � presente nel grafo.
	 **/
	public boolean contains(Object state) {
		return graph1.contains(state) || graph2.contains(state);
	}

	/**
	 * Restituisce una <tt>LinkedList</tt> contenente tutti gli 
	 * stati del grafo.
	 **/
	public Set<S> getStates() {
		HashSet<S> list = new HashSet<S>();

		Iterator<S> iter1 = graph1.getStatesIterator();
		while( iter1.hasNext() )
			list.add( iter1.next() );

		Iterator<S> iter2 = graph2.getStatesIterator();
		while( iter2.hasNext() )
			list.add( iter2.next() );
			
		return list;
	}
	
	
	/**
	 * Restituisce una <tt>LinkedList</tt> contenente tutti gli 
	 * stati del grafo che verificano il fltro <tt>f</tt>.
	 **/
	public HashSet<S> getStates(Filter<S> filter) {
		HashSet<S> list = new HashSet<S>();

		S state;
		Iterator<S> iter1 = graph1.getStatesIterator();
		while( iter1.hasNext() ) {
			state = iter1.next();
			if( filter.check(state) )
				list.add( state );
		}

		Iterator<S> iter2 = graph2.getStatesIterator();
		while( iter2.hasNext() ) {
			state = iter2.next();
			if( filter.check(state) )
				list.add( state );
		}
		
		return list;
	}
	
	/**
	 * Rende un iteratore per l'insieme degli stati del grafo.
	 * Il metodo � da preferire a <tt>getAllStates().iterator()</tt>
	 * in quanto evita di dover generate la collezione 
	 * degli stati e scorre invece direttamente la struttura interna
	 * che memorizza gli stati.
	 **/
	public Iterator<S> getStatesIterator() {
		return new Iterator<S>(){
			Iterator<S> iter1 = graph1.getStatesIterator();
			Iterator<S> iter2 = graph2.getStatesIterator();
			Iterator<S> using = iter1;
			public boolean hasNext() {
				return iter1.hasNext() || iter2.hasNext();				
			}			
			public S next() {
				S state;
				if( iter1.hasNext() ) {
					state = iter1.next();
					using = iter1;
				} else {
					state = iter2.next();
					using = iter2;
				}
				
				return state;
			}
			public void remove() {
				using.remove();
			}
		};
	}

	
// _____________________________________________________________________________
// get Edges
	
	/**
	 * Restituisce il numero di archi nel grafo.
	 **/
	public int getNumberOfEdges() {
		return graph1.getNumberOfEdges() + graph2.getNumberOfEdges();
	}
	
	/** 
	 * Rende il numero di archi etichettati con l'azione
	 * specificata. 
	 **/
	public int getNumberOfEdges(A action) {
		return graph1.getNumberOfEdges(action) + graph2.getNumberOfEdges(action);
	}
	

	/**
	 * Rende la molteplicit� di un arco.
	 **/
	public int getNumberOfEdges(S src, A action, S dest) {
		if( graph1.contains(src, action, dest) )
			return graph1.getNumberOfEdges(src, action, dest);
		
		return graph2.getNumberOfEdges(src, action, dest);
	}

	
	/**
	 * Dice se nel grafo vi � un arco tra gli stati passati
	 * come parametri etichettato con l'azione passata come parametro.
	 **/
	public boolean contains(S from, A action, S to) {
		return 
			graph1.contains(from, action, to) || 
			graph2.contains(from, action, to);
	}

		
	public boolean contains(S from, S to) {
		return graph1.contains(from, to)||graph1.contains(from, to);
	}

	
// _____________________________________________________________________________
// get Actions
	

	/**
     * Rende un insieme con le azioni del grafo. 
     **/
    public Set<A> getActions() {
    	Set<A> set1 = graph1.getActions();
    	Set<A> set2 = graph2.getActions();
    	Set<A> actionSet = new HashSet<A>( 
    		(int) (java.lang.Math.max(set1.size(),set2.size()) / 0.75) + 1
    	);
    	
    	for( A action: set1 )
    		actionSet.add(action);
    	for( A action: set2 )
    		actionSet.add(action);

    	return actionSet;
    }

// _____________________________________________________________________________
// get Pre/post-images
	
	/**
	 * Rende la mappa dei preset di uno stato <tt>state</tt>: cio�
	 * la mappa che associa ad ogni azione <tt>a</tt>
	 * la retroimmagine di <tt>state</tt> sotto l'azione <tt>a</tt>.
	 * Nel caso che <tt>state</tt> non abbia nessun arco entrante
	 * il metodo rende null.
	 **/
	public Map<A, ? extends Set<S>> getPresetMapping(S state) {
		if( graph1.contains(state) )
			return graph1.getPresetMapping(state);
		
		return graph2.getPresetMapping(state);
	}

	/**
	 * Rende la mappa dei postset di uno stato <tt>state</tt>: cio�
	 * la mappa che associa ad ogni azione <tt>a</tt>
	 * l'immagine di <tt>state</tt> sotto l'azione <tt>a</tt>.
	 * Nel caso che <tt>state</tt> non abbia nessun arco uscente
	 * il metodo rende null.
	 **/
	public Map<A, ? extends Set<S>> getPostsetMapping(Object state) {
		if( graph1.contains(state) )
			return graph1.getPostsetMapping(state);
		
		return graph2.getPostsetMapping(state);
	}
	
	/**
	 * Rende il preset di uno stato sotto una azione.
	 * Se lo stato non ha archi entranti con tali azione
	 * pu� rendere null oppure una lista vuota.
	 **/
	public Set<S> getPreset(S state, A action) {
		if( graph1.contains(state) )
			return graph1.getPreset(state, action);
		
		return graph2.getPreset(state, action);
	}

	/**
	 * Rende il postset di uno stato sotto una azione.
	 * Se lo stato non ha archi uscenti con tali azione
	 * pu� rendere null oppure una lista vuota.
	 **/
	public Set<S> getPostset(S state, A action) {
		if( graph1.contains(state) )
			return graph1.getPostset(state, action);
		
		return graph2.getPostset(state, action);
	}


	/**
     * Rende un iteratore per le transizioni dello stato specificato.
     * Le transizioni considerate sono quelle uscenti se 'post'
     * � true, quelle entranti altrimenti.
     **/	
    public Iterator<Map.Entry<A,S>> getImageIterator(
    	S state, boolean post, EdgeFilter<S,A> filter
    ) {
		if( graph1.contains(state) )
			return graph1.getImageIterator(state, post, filter);
		
		return graph2.getImageIterator(state, post, filter);
    }
    
   	/** Rende true se lo stato specificato non ha archi uscenti. **/
	public boolean isFinal(S state) {
		if( graph1.contains(state) )
			return graph1.isFinal(state);
		
		return graph2.isFinal(state);
	}

	public int getNumberOfEdges(boolean multiplicity) {
		return graph1.getNumberOfEdges(multiplicity)+graph2.getNumberOfEdges(multiplicity);
	}
	
	public Set<S> getPostset(Object state) {
		if (graph1.contains(state)) {
			return graph1.getPostset(state);
		}
		return graph2.getPostset(state);
	}

	public Set<S> getPostset(S state, Filter<A> filter) {
		if (graph1.contains(state)) {
			return graph1.getPostset(state,filter);
		}
		return graph2.getPostset(state,filter);
	}
	
	public GraphComposition<S, A> clone() {
		return new GraphComposition<S, A>(graph1,graph2);
	}

}
