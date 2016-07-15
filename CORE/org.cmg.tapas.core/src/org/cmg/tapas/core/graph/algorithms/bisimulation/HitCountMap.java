package org.cmg.tapas.core.graph.algorithms.bisimulation;


import java.util.HashMap;

import org.cmg.tapas.core.graph.ActionInterface;
import org.cmg.tapas.core.graph.StateInterface;

/** 
 * HitCountMap rappresenta una mappa per tenere traccia 
 * dei hitCount degli stati di un grafo. Tali hitCount
 * sono utilizzati dagli algoritmi di Kannelakis-Smolka
 * e Paige-Tarjan. Le chiavi della mappa sono terne della forma
 * (Set s, Action a, State p). Il valore associato a tale chiave nella
 * mappa � un intero che rappresenta il numero di volte che lo stato 'p'
 * colpisce l'insieme di stati 's' con transizioni etichettate 'a'.
 * <p>
 * @param <S> tipo degli stati
 * @param <A> tipo delle azioni
 * 
 * @author Guzman Tierno
 **/
public class HitCountMap<
	S ,
	A extends ActionInterface	
> {
	/** La mappa. **/
	private HashMap<Splitter<S>, HashMap<A, HashMap<S, Integer>>> map =
		new HashMap<Splitter<S>, HashMap<A, HashMap<S, Integer>>>();
	
	/**
	 * Aumenta di 1 il conteggio relativo allo stato, l'azione 
	 * e lo splitter specificati.
	 **/	
	public int addToHitCount(
		S state,
		A action,
		Splitter<S> splitter,
		int gap
	) {
		HashMap<S, Integer> map2 = createMap(splitter, action);
		Integer count = map2.get(state);
		if( count==null )
			count=0;
			
		count += gap;	
		map2.put(state, count);
		return count;
	}
	
	/**
	 * Aumenta di 1 il conteggio relativo allo stato, l'azione 
	 * e lo splitter specificati.
	 **/	
	public int increaseHitCount(
		S state,
		A action,
		Splitter<S> splitter
	) {
		return addToHitCount(state, action, splitter, 1);
	}
	
	/**
	 * Decrementa di 1 il conteggio relativo allo stato, l'azione 
	 * e lo splitter specificati.
	 **/	
	public int decreaseHitCount(
		S state,
		A action,
		Splitter<S> splitter
	) {
		return addToHitCount(state, action, splitter, -1);
	}
	
	/**
	 * Rende il conteggio relativo allo stato, l'azione 
	 * e lo splitter specificati.
	 **/	
	public int getHitCount(
		S state,
		A action,
		Splitter<S> splitter
	) {
		HashMap<S, Integer> map2 = createMap(splitter, action);
		Integer count = map2.get(state);
		if( count==null )
			count=0;
	
		return count;
	}
	
	/**
	 * Setta il conteggio relativo allo stato, l'azione 
	 * e lo splitter specificati.
	 **/	
	public void setHitCount(
		S state,
		A action,
		Splitter<S> splitter,
		int count
	) {
		HashMap<S, Integer> map2 = createMap(splitter, action);
		map2.put(state, count);
	}
	
	/**
     * Svuota l'intera mappa.
     **/
	public void clear() {
		map.clear();
	}
	
	/** 
	 * Rende, creandola se necessario, la mappa per lo stato 
	 * e l'azione specificati.
	 **/
	private HashMap<S, Integer> createMap(
		Splitter<S> splitter,
		A action
	) {
		HashMap<A, HashMap<S, Integer>> map1 = map.get(splitter);
		if( map1 == null ) {
			map1 = new HashMap<A, HashMap<S, Integer>>();
			map.put( splitter, map1 );
		}
		
		HashMap<S, Integer> map2 = map1.get(action);
		if( map2 == null ) {
			map2 = new HashMap<S, Integer>();
			map1.put( action, map2 );
		}
		
		return map2;		
	}
	
	/**
	 * Rimuove tutti i hitCount associati a uno splitter.
	 **/
	public void removeCounts(Splitter<S> splitter) {
		map.remove(splitter);
	}

		
}








