package org.cmg.tapas.core.graph;

/** 
 * PointedGraph rappresenta una terna (grafo, stato, stato).
 * <p>
 * @param <S> tipo degli stati
 * @param <A> tipo delle azioni
 * 
 * @author Guzman Tierno
 **/
public class PointedGraph<	
	S ,			// type for states
	A extends ActionInterface			// type for actions
> {

	// Data	
	// private GraphInterface<S,A> graph;
	private GraphInterface<S,A> graph;
	private S state1; 
	private S state2; 
	
	/** Costruisce un PointedGraph con i valori specificati. **/
	public PointedGraph(
		GraphInterface<S,A> graph,
		S state1,
		S state2
	) {
		this.graph = graph;
		this.state1 = state1;
		this.state2 = state2;
	}

	
	/**
	 * Method getGraph
	 */
	public GraphInterface<S,A> getGraph() {
		return graph; 
	}

	/**
	 * Method setGraph
	 */
	public void setGraph(GraphInterface<S,A> graph) {
		this.graph = graph; 
	}

	/**
	 * Method getState1
	 */
	public S getState1() {
		return state1; 
	}

	/**
	 * Method setState1
	 */
	public void setState1(S state1) {
		this.state1 = state1; 
	}

	/**
	 * Method getState2
	 */
	public S getState2() {
		return state2; 
	}

	/**
	 * Method setState2
	 */
	public void setState2(S state2) {
		this.state2 = state2; 
	}
	


}