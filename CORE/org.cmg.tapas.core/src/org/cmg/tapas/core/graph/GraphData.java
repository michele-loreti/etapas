/**
 * Copyright (c) 2012 Concurrency and Mobility Group.
 * Universita' di Firenze
 *      
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *      Michele Loreti
 *      Guzman Tierno
 */

package org.cmg.tapas.core.graph;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.cmg.tapas.core.graph.filter.Filter;

/** 
 * This interface identifies a generic graph.
 * 
 * @param <S> Type identifying nodes.
 * @param <A> Type identifying edges.
 * 
 * @see StateInterface
 * @see ActionInterface
 *  
 * @author Guzman Tierno
 * @author Michele Loreti
 */
public interface GraphData<S,A> {
	
	/**
	 * Returns the number of edges.
	 * 
	 * @return the number of edges.
	 */
	int getNumberOfStates();

	/**
	 * Checks if is a node in the graph.
	 * 
	 * @param s a node.
	 * 
	 * @return true if s is a node in the graph.
	 */
	boolean contains(Object s);

	/**
	 * Returns the set of states satisfying filter f.
	 * 
	 * @param f a filter used to select graph nodes.
	 * 
	 * @return the set of states satisfying filter f.
	 */
	Set<S> getStates(Filter<S> f);
	
	/**
	 * Returns a state iterator. 
	 * 
	 * @return a state iterator.
	 */
	Iterator<S> getStatesIterator();
	
	/**
	 * Returns the number of edges in the graph.
	 * 
	 * @return the number of edges in the graph.
	 */
	int getNumberOfEdges();

	/**
	 * Returns the number of edges in the graph.
	 * 
	 * @param multiplicity if this is true, edge multiplicity is taken into account.
	 * 
	 * @return the number of edges in the graph.
	 */
	int getNumberOfEdges(boolean multiplicity);
	
	/**
	 * Returns the number of edges labeld with action <code>action</code>
	 * 
	 * @param action labeling action
	 * 
	 * @return the number of edges labeld with action <code>action</code>
	 */
	public int getNumberOfEdges(A action);
	
	/**
	 * Returns the number of edges from src to dest labeled by action.
	 * 
	 * @param src source node
	 * @param action labeling action
	 * @param dest destination node
	 * 
	 * @return the number of edges from src to dest labeled by action.
	 */
	public int getNumberOfEdges(S src, A action, S dest);
	
	/**
	 * Checks if the graph contains an a-labeled edge from s1 to s2.
	 * 
	 * @param s1 source node
	 * @param a edge label
	 * @param s2 target node
	 * 
	 * @return true if the graph contains an a-labeled edge from s1 to s2.
	 */
	boolean contains(S s1, A a, S s2);	

	/**
	 * Checks if the graph contains an from s1 to s2.
     *
	 * @param s1 source node
	 * @param s2 target node
	 * 
	 * @return true if the graph contains an from s1 to s2.
	 */
	boolean contains(S from, S to);	
	
	
	/**
	 * Returns the labels associated to the edges in the graph.
	 * 
	 * @return the labels associated to the edges in the graph.
	 */
    Set<A> getActions();


    /**
     * Returns the incoming transitions of a node. These transitions are stored
     * in a map that associates to each transition label 'a' the set of states
     * s' that reach s with a transition labeled a.
     * 
     * @param s a node graph
     * 
     * @return the incoming transitions of s.
     */
	Map<A,? extends Set<S>> getPresetMapping(S s);

	/**
     * Returns the outgoing transitions of a node. These transitions are stored
     * in a map that associates to each transition label 'a' the set of states
     * s' that can be reached from s with a transition labeled 'a'.
     * 
     * @param s a node graph
     * 
     * @return the incoming transitions of s.
     */
	Map<A,? extends Set<S>> getPostsetMapping(Object s);
	
	/**
	 * Returns the set of states that can reach s with an a-labeld transition.
	 * 
	 * @param s a node
	 * @param a a transition label
	 * 
	 * @return the set of states that can reach s with an a-labeled transition.
	 */
	Set<S> getPreset(S s, A a);

	/**
	 * Returns the set of states that can be reached from s with an a-labeled transition.
	 * 
	 * @param s a node
	 * @param a a transition label
	 * 
	 * @return the set of states that can be reached from s with an a-labeled transition.
	 */
	Set<S> getPostset(S s, A a);

	/**
	 * Returns the set of states that can be reached from s with a transition whose label
	 * satisfies filter f.
	 * 
	 * @param s
	 * @param f
	 * 
	 * @return the set of states that can be reached from s with a transition with a label 
	 * satisfying filter f.
	 */
	Set<S> getPostset(S s, Filter<A> f);

	/**
	 * Returns the set of states that can be reached from s with a single transition. 
	 * 
	 * @param s a node
	 * 
	 * @return the set of states that can be reached from s with a single transition. 
	 */
	Set<S> getPostset(Object s);
		
	/**
	 * 
	 * 
	 * @param state
	 * @param post
	 * @param filter
	 * @return
	 */
	Iterator<Map.Entry<A,S>> getImageIterator(
    	S state, boolean post, EdgeFilter<S,A> filter
    );

	/**
	 * Check if is a final state.
	 * 
	 * @param s a node
	 * 
	 * @return true if s is a final state.
	 */
	boolean isFinal(S state);
	
	
	/**
	 * Returns the set of nodes in the graph.
	 * 
	 * @return the set of nodes in the graph.
	 */
	Set<S> getStates();

}
