package org.cmg.tapas.core.graph.algorithms;


import java.util.Iterator;
import java.util.Map;

import org.cmg.tapas.core.graph.ActionInterface;
import org.cmg.tapas.core.graph.GraphData;

/** 
 * La classe RankInfo rappresenta le informazioni sul rank degli stati di
 * un grafo. Essa contiene un riferimento al grafo 
 * usato per generare le informazioni.
 * Oltre alle informazioni offerte da <code>LoopInfo</code>
 * associa ad ogni stato il suo rank.
 * E' possibile conoscere anche il rank massimo con il metodo 
 * <code>getMaxRank()</code>. 
 * <p>
 * @param <S> tipo degli stati
 * @param <A> tipo delle azioni
 * 
 * @author Guzman Tierno
 **/
public class RankInfo<
	S , 
	A extends ActionInterface
> extends LoopInfo<S,A> {
	
	private int maxRank;

	/**
	 * Constructor RankInfo
	 */
	RankInfo(
		GraphData<S,A> graph, 
		Map<S, Integer> componentMap,
		Map<S, StateInfo<S>> infoMap,
		int maxRank
	) {
		super(graph, componentMap, infoMap);
		this.maxRank = maxRank;
	}
	
	/**
	 * Rende il rank dello stato specificato.
	 */
	public int getRank(S state) {
		return infoMap.get(state).getRank(); 
	}

	/**
	 * Method getMaxRank
	 */
	public int getMaxRank() {
		return maxRank; 
	}

	/**
	 * Print.
	 **/
	@Override
	public void print() {
		super.print();
		
		// Rank
		System.out.print( "rank: {" );
		Iterator<S> i = graph.getStatesIterator();
		while( i.hasNext() ) {
			S state = i.next();
			System.out.print( state + "=" + getRank(state) );
			if(i.hasNext())
				System.out.print(", ");
		} 
		System.out.println( "}" );
	}
	
} 
