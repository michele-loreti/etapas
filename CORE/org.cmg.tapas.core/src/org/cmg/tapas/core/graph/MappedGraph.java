package org.cmg.tapas.core.graph;

import java.util.Map;

/** 
 * MappedGraph rappresenta il risultato della 
 * trasformazione di un grafo.
 * Esso contiene il nuovo grafo e una mappa.
 * La mappa pu� avere due interpretazioni a seconda dei casi:
 * essa pu� associare classi di stati del vecchio grafo a stati del nuovo
 * grafo, oppure pu� associare stati del vecchio grafo
 * a stati del nuovo grafo.
 * <p>
 * @param <S> tipo degli stati
 * @param <A> tipo delle azioni
 * @param <K> tipo delle chiavi della mappa (S o Set<S>)
 * 
 * @author Guzman Tierno
 **/
public class MappedGraph<	
	S ,			
	A extends ActionInterface,
	K 
> {
	// Data	
	private GraphInterface<S,A> graph;
	private Map<K,S> map;
	
	/** Costruisce un MappedGraph con i valori specificati. **/
	public MappedGraph(
		GraphInterface<S,A> graph,
		Map<K,S> map
	) {
		this.graph = graph;
		this.map = map;
	}

	
	/**
	 * Method getGraph
	 */
	public GraphInterface<S,A> getGraph() {
		return graph; 
	}
	
	/**
	 * Rende la mappa delle associazioni.
	 * La mappa pu� avere due interpretazioni a seconda dei casi:
	 * essa pu� associare classi della partizione a stati del nuovo
	 * grafo, oppure pu� associare stati del vecchio grafo
	 * a stati del nuovo grafo.
	 **/
	public Map<K,S> getMap() {
		return map;
	}

	


}






