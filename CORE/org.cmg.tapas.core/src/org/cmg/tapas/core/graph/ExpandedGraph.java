package org.cmg.tapas.core.graph;

import java.util.Map;

/** 
 * ExpandedGraph rappresenta il risultato dell'estensione di un grafo.
 * Esso contiene il nuovo grafo e una mappa 
 * che dice a quali stati del nuovo grafo corrispondono gli stati del grafo
 * di partenza.
 * <p>
 * @param <S> tipo degli stati del grafo di partenza
 * @param <T> tipo degli stati del nuovo grafo 
 * @param <A> tipo delle azioni del nuovo grafo
 * 
 * @author Guzman Tierno
 **/
public class ExpandedGraph<	
	S ,			
	T ,			
	A extends ActionInterface
> {

	// Data	
	private GraphInterface<T,A> graph;
	private Map<S,T> map;
	
    /** 
     * Costruisce un ExpandedGraph con i valori specificati.
     * 
	 * @param graph Il nuovo grafo  
	 * @param map Mappa di corrispondenza tra gli stati del vecchio
     * grafo e del nuovo grafo.
	 */
	public ExpandedGraph(
		GraphInterface<T,A> graph,
		Map<S,T> map
	) {
		this.graph = graph;
		this.map = map;
	}

	
	/**
	 * Method getGraph
     * 
	 * @return Il nuovo grafo.
	 */
	public GraphInterface<T,A> getGraph() {
		return graph; 
	}
	
	/**
	 * Rende la mappa delle associazioni. Tale mappa
	 * dice a quali stati del nuovo grafo corrispondono gli stati del grafo
	 * di partenza.
     * 
	 * @return La mappa di corrispondenza tra gli stati del vecchio
     * grafo e del nuovo grafo.
	 */
	public Map<S,T> getMap() {
		return map;
	}

}






