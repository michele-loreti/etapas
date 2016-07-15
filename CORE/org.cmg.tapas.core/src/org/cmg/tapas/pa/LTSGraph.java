/**
 * 
 */
package org.cmg.tapas.pa;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.cmg.tapas.core.graph.ActionInterface;
import org.cmg.tapas.core.graph.EdgeFilter;
import org.cmg.tapas.core.graph.Graph;
import org.cmg.tapas.core.graph.GraphInterface;
import org.cmg.tapas.core.graph.filter.Filter;

/**
 * @author loreti
 *
 */
public class LTSGraph<S extends Process<S,A>,A extends ActionInterface> implements GraphInterface<S, A> {

	private Graph<S, A> graph;
	private LinkedList<S> toVisit;
	
	public LTSGraph() {
		this.graph = new Graph<S, A>();
		this.toVisit = new LinkedList<S>();
	}
	
	/**
	 *  provvisorio Ercoli
	 */
	public Graph<S,A> getGraph(){
		return graph;
	}

	/**
	 * @return
	 * @see org.cmg.tapas.core.graph.Graph#getNumberOfStates()
	 */
	public int getNumberOfStates() {
		return graph.getNumberOfStates();
	}

	/**
	 * @return
	 * @see org.cmg.tapas.core.graph.Graph#getStatesIterator()
	 */
	public Iterator<S> getStatesIterator() {
		return graph.getStatesIterator();
	}

	/**
	 * @param stateFilter
	 * @return
	 * @see org.cmg.tapas.core.graph.Graph#getStates(org.cmg.tapas.core.graph.filter.Filter)
	 */
	public Set<S> getStates(Filter<S> stateFilter) {
		return graph.getStates(stateFilter);
	}

	/**
	 * @param state
	 * @return
	 * @see org.cmg.tapas.core.graph.Graph#contains(java.lang.Object)
	 */
	public boolean contains(Object state) {
		return graph.contains(state);
	}

	/**
	 * @param state
	 * @return
	 * @see org.cmg.tapas.core.graph.Graph#addState(java.lang.Object)
	 */
	public boolean addState(S state) {
		if (graph.addState(state)) {
			toVisit.add(state);
			return true;
		}
		return false;
	}

	/**
	 * @return
	 * @see org.cmg.tapas.core.graph.Graph#getNumberOfEdges()
	 */
	public int getNumberOfEdges() {
		return graph.getNumberOfEdges();
	}

	/**
	 * @param action
	 * @return
	 * @see org.cmg.tapas.core.graph.Graph#getNumberOfEdges(java.lang.Object)
	 */
	public int getNumberOfEdges(A action) {
		return graph.getNumberOfEdges(action);
	}

	/**
	 * @param src
	 * @param action
	 * @param dest
	 * @return
	 * @see org.cmg.tapas.core.graph.Graph#getNumberOfEdges(java.lang.Object, java.lang.Object, java.lang.Object)
	 */
	public int getNumberOfEdges(S src, A action, S dest) {
		return graph.getNumberOfEdges(src, action, dest);
	}

	/**
	 * @param from
	 * @param action
	 * @param to
	 * @return
	 * @see org.cmg.tapas.core.graph.Graph#contains(java.lang.Object, java.lang.Object, java.lang.Object)
	 */
	public boolean contains(S from, A action, S to) {
		return graph.contains(from, action, to);
	}

	/**
	 * @param from
	 * @param to
	 * @return
	 * @see org.cmg.tapas.core.graph.Graph#contains(java.lang.Object, java.lang.Object)
	 */
	public boolean contains(S from, S to) {
		return graph.contains(from, to);
	}

	/**
	 * @param src
	 * @param action
	 * @param dest
	 * @return
	 * @see org.cmg.tapas.core.graph.Graph#addEdge(java.lang.Object, java.lang.Object, java.lang.Object)
	 */
	public boolean addEdge(S src, A action, S dest) {
		return graph.addEdge(src, action, dest);
	}

	/**
	 * @param state
	 * @return
	 * @see org.cmg.tapas.core.graph.Graph#getPresetMapping(java.lang.Object)
	 */
	public Map<A, ? extends Set<S>> getPresetMapping(S state) {
		return graph.getPresetMapping(state);
	}

	/**
	 * @param state
	 * @return
	 * @see org.cmg.tapas.core.graph.Graph#getPostsetMapping(java.lang.Object)
	 */
	public Map<A, ? extends Set<S>> getPostsetMapping(Object state) {
		return graph.getPostsetMapping(state);
	}

	/**
	 * @param state
	 * @param post
	 * @param filter
	 * @return
	 * @see org.cmg.tapas.core.graph.Graph#getImageIterator(java.lang.Object, boolean, org.cmg.tapas.core.graph.EdgeFilter)
	 */
	public Iterator<Entry<A, S>> getImageIterator(S state, boolean post,
			EdgeFilter<S, A> filter) {
		return graph.getImageIterator(state, post, filter);
	}

	/**
	 * @param state
	 * @param action
	 * @return
	 * @see org.cmg.tapas.core.graph.Graph#getPreset(java.lang.Object, java.lang.Object)
	 */
	public Set<S> getPreset(S state, A action) {
		return graph.getPreset(state, action);
	}

	/**
	 * @param state
	 * @param filter
	 * @return
	 * @see org.cmg.tapas.core.graph.Graph#getPreset(java.lang.Object, org.cmg.tapas.core.graph.filter.Filter)
	 */
	public Set<S> getPreset(S state, Filter<A> filter) {
		return graph.getPreset(state, filter);
	}

	/**
	 * @param state
	 * @param action
	 * @return
	 * @see org.cmg.tapas.core.graph.Graph#getPostset(java.lang.Object, java.lang.Object)
	 */
	public Set<S> getPostset(S state, A action) {
		return graph.getPostset(state, action);
	}

	/**
	 * @param state
	 * @return
	 * @see org.cmg.tapas.core.graph.Graph#getPostset(java.lang.Object)
	 */
	public Set<S> getPostset(Object state) {
		return graph.getPostset(state);
	}

	/**
	 * @param state
	 * @param filter
	 * @return
	 * @see org.cmg.tapas.core.graph.Graph#getPostset(java.lang.Object, org.cmg.tapas.core.graph.filter.Filter)
	 */
	public Set<S> getPostset(S state, Filter<A> filter) {
		return graph.getPostset(state, filter);
	}

	/**
	 * @return
	 * @see org.cmg.tapas.core.graph.Graph#clone()
	 */
	public Graph<S, A> clone() {
		return graph.clone();
	}

	/**
	 * @param arg0
	 * @return
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object arg0) {
		return graph.equals(arg0);
	}

	/**
	 * @return
	 * @see org.cmg.tapas.core.graph.Graph#getActions()
	 */
	public Set<A> getActions() {
		return graph.getActions();
	}

	/**
	 * @param multiplicity
	 * @return
	 * @see org.cmg.tapas.core.graph.Graph#getNumberOfEdges(boolean)
	 */
	public int getNumberOfEdges(boolean multiplicity) {
		return graph.getNumberOfEdges(multiplicity);
	}

	/**
	 * @param map
	 * @return
	 * @see org.cmg.tapas.core.graph.Graph#getAdiacenceMatrix(java.util.Map)
	 */
	public int[][] getAdiacenceMatrix(Map<S, Integer> map) {
		return graph.getAdiacenceMatrix(map);
	}

	/**
	 * @return
	 * @see org.cmg.tapas.core.graph.Graph#getStates()
	 */
	public Set<S> getStates() {
		return graph.getStates();
	}

	/**
	 * @return
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		return graph.hashCode();
	}

	/**
	 * @param state
	 * @return
	 * @see org.cmg.tapas.core.graph.Graph#removeState(java.lang.Object)
	 */
	public boolean removeState(S state) {
		return graph.removeState(state);
	}

	/**
	 * @param src
	 * @param action
	 * @param dest
	 * @return
	 * @see org.cmg.tapas.core.graph.Graph#removeEdge(java.lang.Object, java.lang.Object, java.lang.Object)
	 */
	public boolean removeEdge(S src, A action, S dest) {
		return graph.removeEdge(src, action, dest);
	}

	/**
	 * @param dest
	 * @return
	 * @see org.cmg.tapas.core.graph.Graph#removeIncomingEdge(java.lang.Object)
	 */
	public int removeIncomingEdge(S dest) {
		return graph.removeIncomingEdge(dest);
	}

	/**
	 * @param src
	 * @return
	 * @see org.cmg.tapas.core.graph.Graph#removeOutgoingEdge(java.lang.Object)
	 */
	public int removeOutgoingEdge(S src) {
		return graph.removeOutgoingEdge(src);
	}

	/**
	 * @param state
	 * @return
	 * @see org.cmg.tapas.core.graph.Graph#removeAllEdge(java.lang.Object)
	 */
	public int removeAllEdge(S state) {
		return graph.removeAllEdge(state);
	}

	/**
	 * @param state
	 * @return
	 * @see org.cmg.tapas.core.graph.Graph#isFinal(java.lang.Object)
	 */
	public boolean isFinal(S state) {
		return graph.isFinal(state);
	}

	/**
	 * 
	 * @see org.cmg.tapas.core.graph.Graph#printStates()
	 */
	public void printStates() {
		graph.printStates();
	}
	
	public void expand() {
		while (!toVisit.isEmpty()) {
			S s = toVisit.remove();
			Map<A,Set<S>> next = s.getNext();
			for (A a : next.keySet()) {
				for (S s2 : next.get(a)) {
					addState(s2);
					addEdge(s, a, s2);
				}
			}
		}
	}
	
}
