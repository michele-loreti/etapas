package org.cmg.tapas.core.graph.algorithms;


import java.util.Iterator;
import java.util.Map;

import org.cmg.tapas.core.graph.GraphData;

/** 
 * La classe LoopInfo rappresenta le informazioni sui cicli di
 * un grafo. Essa contiene un riferimento al grafo 
 * usato per generare le informazioni.
 * Inoltre essa contiene due mappe. La mappa 
 * <code>componentMap</code> associa ad ogni stato del grafo un intero
 * in modo tale che due stati hanno lo stesso numero associato se e solo
 * se fanno parte della stessa componente fortemente connessa.
 * La mappa <code>infoMap</code> associa ad ogni stato varie
 * informazioni sullo stato: si veda <code>StateInfo</code>.
 * <p>
 * <p> 
 * @param <S> tipo degli stati
 * @param <A> tipo delle azioni
 * 
 * @author Guzman Tierno
 **/	
public class LoopInfo< 
	S , 
	A 
> {
	protected GraphData<S,A> graph;
	protected Map<S, Integer> componentMap;
	protected Map<S, StateInfo<S>> infoMap;

	/**
	 * Constructor LoopInfo.
	 * I metodi di modifica hanno volutamente visibilit� pacchetto.
	 */
	LoopInfo(
		GraphData<S,A> graph, 
		Map<S, Integer> componentMap,
		Map<S, StateInfo<S>> infoMap
	) {
		this.graph = graph;
		this.componentMap = componentMap;
		this.infoMap = infoMap;
	}
	
	/**
	 * Method getGraph
	 */
	public GraphData<S,A> getGraph() {
		return graph; 
	}

	/**
	 * Rende l'indice della componente a cui appartiene
	 * lo stato specificato. 
	 * Rende -1 se l'indice di tale componente non 
	 * � stato calcolato.
	 */
	public int getComponent(S state) {
		Integer comp = componentMap.get(state);
		if( comp!=null )
			return comp;
			
		return -1;
	}

	/**
	 * Rende la mappa delle componente.
	 */
	public Map<S, Integer> getComponentMap() {
		return componentMap;
	}

	/**
	 * Rende la dimensione della componente fortemente connessa 
	 * a cui lo stato appartiene. Se rende 0 significa 
	 * che lo stato � l'unico elemento della componente 
	 * e non ha selfloops. Se rende 1 significa 
	 * che lo stato � l'unico elemento della componente 
	 * ed ha selfloops. Se rende <tt>n</tt> significa 
	 * che lo stato appartiene a una componente di <tt>n</tt> stati.
	 */
	public int getComponentSize(S state) {
		return infoMap.get(state).getComponentSize(); 
	}

	/**
	 * Dice se lo stato specificato � convergente.
	 */
	public boolean isConvergent(S state) {
		return infoMap.get(state).isConvergent(); 
	}
	
	/**
	 * Rende true se lo stato � una foglia rispetto
	 * alle azioni accettate dal filtro usato per generare
	 * le informazioni.
	 */
	public boolean isLeaf(S state) {
		return infoMap.get(state).isLeaf(); 
	}
	
	/**
	 * Rende una descrizione delle mappe contenute in questo oggetto.
	 **/
	@Override 
	public String toString() {
		StringBuffer s = new StringBuffer();
		
		s.append( "Comp: " );
		s.append( componentMap.toString() );
		s.append( "\n" );

		s.append( "Info: " );
		s.append( infoMap.toString() );
		
		return s.toString();	
	}
	
	/**
	 * Print.
	 **/
	public void print() {
		// Components
		System.out.println( "Comp: " + componentMap.toString() );
		
		// Convergence
		System.out.print( "conv: {" );
		Iterator<S> i = graph.getStatesIterator();
		while( i.hasNext() ) {
			S state = i.next();
			System.out.print( state + "=" + isConvergent(state) );
			if(i.hasNext())
				System.out.print(", ");
		} 
		System.out.println( "}" );

		// Size
		System.out.print( "size: {" );
		i = graph.getStatesIterator();
		while( i.hasNext() ) {
			S state = i.next();
			System.out.print( state + "=" + getComponentSize(state) );
			if(i.hasNext())
				System.out.print(", ");
		} 
		System.out.println( "}" );

		// Leafs
		System.out.print( "leaf: {" );
		i = graph.getStatesIterator();
		while( i.hasNext() ) {
			S state = i.next();
			System.out.print( state + "=" + isLeaf(state) );
			if(i.hasNext())
				System.out.print(", ");
		} 
		System.out.println( "}" );
	}
	
	/** 
	 * Dice se questo oggetto � uguale a quello specificato. 
	 **/
	@Override
	public boolean equals(Object o) {
		if( o==null || !(o instanceof LoopInfo) )
			return false;
		
		LoopInfo other = (LoopInfo) o;
		return
			graph.equals( other.graph ) &&
			infoMap.equals( other.infoMap );
	}
	
} 
