/**
 * 
 */
package org.cmg.tapas.core.graph;


import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.cmg.tapas.core.graph.filter.Filter;


/**
 * @author loreti
 *
 */
public class GraphClosure<S, A extends ActionInterface> implements GraphData<S,A> { 

	private Graph<S, A> graph;
	protected A action;

	public GraphClosure(GraphData<S,A> graph , A action) {
		this.graph = new Graph<S,A>();
		this.action = action;
		for (S s : graph.getStates()) {
			this.graph.addState(s);
		}
		computeClosure(graph);
	}
	
	public GraphClosure(GraphClosure<S, A> graphClosure) {
		this.graph = graphClosure.graph.clone();
		this.action = graphClosure.action;
	}

	protected void computeClosure( GraphData<S,A> g ) {
		Iterator<S> iter = g.getStatesIterator();
		S state;		
		Set<S> image;
		Set<S> closure;
		Set<A> actions = g.getActions();
		iter = g.getStatesIterator();
		while( iter.hasNext() ) {
			state = iter.next();
			closure = new HashSet<S>();
			closure.add(state);
			computeClosure( g , closure );
			for( S equiState: closure ) {
				if (!contains(state, action, equiState)) {
					addEdge( state , action, equiState );
				}
			}

			for( A a: actions ){				
				if( a.equals(action) ) 
					continue;
				image = new HashSet<S>();
				computeImage(g , closure, image, a);
				computeClosure( g , image );
				for( S reached: image ) {
					if (!contains(state, a, reached)) {
						addEdge( state , a, reached );
					}
				}
			}
		}
	}
	
	/** 
	 * Aggiunge a <code>image</code> gli elementi 
	 * dell'immagine dell'insieme specificato
	 * sotto l'azione specificata. 
	 * Assume che <tt>set</tt> e <tt>image</tt> non coincidano come oggetti.
	 **/
	protected void computeImage(
		GraphData<S, A> g ,
		Collection<S> set, 
		Set<S> image, 
		A action
	) {
		Set<S> postSet;
		for( S element: set ) {					
			postSet = g.getPostset( element, action );
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
	protected void computeClosure(
		GraphData<S, A> g ,
		Set<S> clazz
	) {
		LinkedList<S> queue = new LinkedList<S>(clazz);		
		Set<S> postset;
		while( !queue.isEmpty() ) {
			postset = g.getPostset(queue.remove(), action );
			if( postset!=null )
				for( S post: postset )
					if( clazz.add(post) )
						queue.add(post);
		}
	}

	
	/**
	 * @param from
	 * @param action
	 * @param to
	 * @return
	 * @see org.cmg.tapas.graph.GraphData#edgeIsIn(org.cmg.tapas.graph.StateInterface, org.cmg.tapas.graph.ActionInterface, org.cmg.tapas.graph.StateInterface)
	 */
	public boolean contains(S from, A action, S to) {
		return graph.contains(from, action, to);
	}

	public boolean contains(S from, S to) {
		return graph.contains(from, to);
	}
	
	/**
	 * @return
	 * @see org.cmg.tapas.graph.GraphData#getActionSet()
	 */
	public Set<A> getActions() {
		return graph.getActions();
	}

	/**
	 * @return
	 * @see org.cmg.tapas.graph.GraphData#getAllStates()
	 */
	public Set<S> getStates() {
		return graph.getStates();
	}

	/**
	 * @param action
	 * @return
	 * @see org.cmg.tapas.graph.GraphData#getEdgeCount(org.cmg.tapas.graph.ActionInterface)
	 */
	public int getNumberOfEdges(A action) {
		return graph.getNumberOfEdges(action);
	}

	/**
	 * @param state
	 * @param post
	 * @param filter
	 * @return
	 * @see org.cmg.tapas.graph.GraphData#getImageIterator(org.cmg.tapas.graph.StateInterface, boolean, org.cmg.tapas.graph.EdgeFilter)
	 */
	public Iterator<Entry<A, S>> getImageIterator(S state, boolean post,
			EdgeFilter<S, A> filter) {
		return graph.getImageIterator(state, post, filter);
	}

	/**
	 * @param src
	 * @param action
	 * @param dest
	 * @return
	 * @see org.cmg.tapas.graph.GraphData#getMultiplicity(org.cmg.tapas.graph.StateInterface, org.cmg.tapas.graph.ActionInterface, org.cmg.tapas.graph.StateInterface)
	 */
	public int getNumberOfEdges(S src, A action, S dest) {
		return graph.getNumberOfEdges(src, action, dest);
	}

	/**
	 * @param state
	 * @param action
	 * @return
	 * @see org.cmg.tapas.graph.GraphData#getPostset(org.cmg.tapas.graph.StateInterface, org.cmg.tapas.graph.ActionInterface)
	 */
	public Set<S> getPostset(S state, A action) {
		return graph.getPostset(state, action);
	}

	/**
	 * @param state
	 * @param filter
	 * @return
	 * @see org.cmg.tapas.graph.GraphData#getPostset(org.cmg.tapas.graph.StateInterface, org.cmg.tapas.graph.filter.Filter)
	 */
	public Set<S> getPostset(S state, Filter<A> filter) {
		return graph.getPostset(state, filter);
	}

	/**
	 * @param state
	 * @return
	 * @see org.cmg.tapas.graph.GraphData#getPostset(org.cmg.tapas.graph.StateInterface)
	 */
	public Set<S> getPostset(Object state) {
		return graph.getPostset(state);
	}

	/**
	 * @param state
	 * @return
	 * @see org.cmg.tapas.graph.GraphData#getPostsetMap(org.cmg.tapas.graph.StateInterface)
	 */
	public Map<A, ? extends Set<S>> getPostsetMapping(Object state) {
		return graph.getPostsetMapping(state);
	}

	/**
	 * @param state
	 * @param action
	 * @return
	 * @see org.cmg.tapas.graph.GraphData#getPreset(org.cmg.tapas.graph.StateInterface, org.cmg.tapas.graph.ActionInterface)
	 */
	public Set<S> getPreset(S state, A action) {
		return graph.getPreset(state, action);
	}

	/**
	 * @param state
	 * @return
	 * @see org.cmg.tapas.graph.GraphData#getPresetMap(org.cmg.tapas.graph.StateInterface)
	 */
	public Map<A, ? extends Set<S>> getPresetMapping(S state) {
		return graph.getPresetMapping(state);
	}

	/**
	 * @param f
	 * @return
	 * @see org.cmg.tapas.graph.GraphData#getStates(org.cmg.tapas.graph.filter.Filter)
	 */
	public Set<S> getStates(Filter<S> f) {
		return graph.getStates(f);
	}

	/**
	 * @return
	 * @see org.cmg.tapas.graph.GraphData#getStatesIterator()
	 */
	public Iterator<S> getStatesIterator() {
		return graph.getStatesIterator();
	}

	/**
	 * @param state
	 * @return
	 * @see org.cmg.tapas.graph.GraphData#isFinal(org.cmg.tapas.graph.StateInterface)
	 */
	public boolean isFinal(S state) {
		return graph.isFinal(state);
	}

	/**
	 * @return
	 * @see org.cmg.tapas.graph.GraphData#numEdges()
	 */
	public int getNumberOfEdges() {
		return graph.getNumberOfEdges();
	}

	/**
	 * @param multiplicity
	 * @return
	 * @see org.cmg.tapas.graph.GraphData#numEdges(boolean)
	 */
	public int getNumberOfEdges(boolean multiplicity) {
		return graph.getNumberOfEdges(multiplicity);
	}

	/**
	 * @return
	 * @see org.cmg.tapas.graph.GraphData#numStates()
	 */
	public int getNumberOfStates() {
		return graph.getNumberOfStates();
	}

	/**
	 * @param state
	 * @return
	 * @see org.cmg.tapas.graph.GraphData#stateIsIn(org.cmg.tapas.graph.StateInterface)
	 */
	public boolean contains(Object state) {
		return graph.contains(state);
	}


	@Override 
	public GraphClosure<S,A> clone() {
		return new GraphClosure<S, A>(this);
	}

	/**
	 * @param src
	 * @param action
	 * @param dest
	 * @return
	 * @see org.cmg.tapas.graph.GraphInterface#addEdge(org.cmg.tapas.graph.StateInterface, org.cmg.tapas.graph.ActionInterface, org.cmg.tapas.graph.StateInterface)
	 */
	public boolean addEdge(S src, A action, S dest) {
		return graph.addEdge(src, action, dest);
	}

	/**
	 * @param s
	 * @see org.cmg.tapas.graph.GraphInterface#addState(org.cmg.tapas.graph.StateInterface)
	 */
	public boolean addState(S s) {
		return graph.addState(s);
	}

//	/**
//	 * @return
//	 * @see org.cmg.tapas.graph.GraphInterface#getNewStateName()
//	 */
//	public String getNewStateName() {
//		return graph.ge();
//	}

//	/**
//	 * @param name
//	 * @return
//	 * @see org.cmg.tapas.graph.GraphInterface#getState(java.lang.String)
//	 */
//	public S getState(String name) {
//		return graph.getSt(name);
//	}

	/**
	 * @param st
	 * @return
	 * @see org.cmg.tapas.graph.GraphInterface#removeAllEdge(org.cmg.tapas.graph.StateInterface)
	 */
	public int removeAllEdge(S st) {
		return graph.removeAllEdge(st);
	}

	/**
	 * @param src
	 * @param action
	 * @param dest
	 * @return
	 * @see org.cmg.tapas.graph.GraphInterface#removeEdge(org.cmg.tapas.graph.StateInterface, org.cmg.tapas.graph.ActionInterface, org.cmg.tapas.graph.StateInterface)
	 */
	public boolean removeEdge(S src, A action, S dest) {
		return graph.removeEdge(src, action, dest);
	}

	/**
	 * @param d
	 * @return
	 * @see org.cmg.tapas.graph.GraphInterface#removeIncomingEdge(org.cmg.tapas.graph.StateInterface)
	 */
	public int removeIncomingEdge(S d) {
		return graph.removeIncomingEdge(d);
	}

	/**
	 * @param s
	 * @return
	 * @see org.cmg.tapas.graph.GraphInterface#removeOutgoingEdge(org.cmg.tapas.graph.StateInterface)
	 */
	public int removeOutgoingEdge(S s) {
		return graph.removeOutgoingEdge(s);
	}

	/**
	 * @param s
	 * @return
	 * @see org.cmg.tapas.graph.GraphInterface#removeState(org.cmg.tapas.graph.StateInterface)
	 */
	public boolean removeState(S s) {
		return graph.removeState(s);
	}

//	/**
//	 * @param name
//	 * @return
//	 * @see org.cmg.tapas.graph.GraphInterface#stateIsIn(java.lang.String)
//	 */
//	public boolean stateIsIn(String name) {
//		return graph.stateIsIn(name);
//	}
	
	
}
