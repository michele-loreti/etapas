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
 * 		Francesco Calzolai
 *      Michele Loreti
 *      Guzman Tierno
 */
package org.cmg.tapas.core.graph;


/** 
 * Interfaccia per le classi che rappresentano grafi. Mette a 
 * disposizione una serie di metodi per la gestione del grafo
 * 
 * @param <S> Tipo degli stati del grafo. 
 * @param <A> Tipo delle azioni del grafo.
 * 
 * @see StateInterface
 * @see ActionInterface
 *  
 * @author Francesco Calzolai
 * @author Michele Loreti
 * @author Guzman Tierno
 */
public interface GraphInterface<
	S,// extends StateInterface, 
	A// extends ActionInterface
> extends GraphData<S,A> {
	
// _____________________________________________________________________________
// States (get by name, add, remove)

	/**
	 * Restituisce un nuovo nome che non � presente nel grafo.
	 *
	 * @return Il nuovo nome.
	 */
	//String getNewStateName();
	
		
	/**
	 * Restituisce lo stato con identificativo <tt>name</tt>
	 * 
	 * @param name Il nome dello stato da ricercare.
	 * @return Lo stato che si voleva.
	 */
	//S getState(String name);

		
	/** 
	 * Dice se lo stato col nome specificato � presente nel grafo.
	 **/
	//boolean stateIsIn(String name);

	/** 
	 * Aggiunge Lo stato <tt>s</tt> al grafo.
	 * 
	 * @param s Lo stato da aggiungere.
	 */
	boolean addState(S s);
	
	
	/** 
	 * Rimuove lo stato <tt>s</tt> dal grafo. Restituisce 
	 * <tt>true</tt> se lo stato � stato trovato e rimosso,
	 * <tt>false</tt> altrimenti.
	 *  
	 * @param s Lo stato da rimuovere.
	 * @return <tt>True</tt> se lo stato � stato trovato e rimosso,
	 * <tt>False</tt> altrimenti.
	 */
	boolean removeState(S s);
	
	
// _____________________________________________________________________________
// Edges (add, remove)

	
	
	/**
	 * Aggiunge un nuovo arco al grafo.
	 * Se lo stato di partenza <tt>src</tt> non appartiene al grafo 
	 * allora <tt>src</tt> viene aggiunto al grafo;
	 * se lo stato di arrivo <tt>dest</tt> non appartiene al grafo 
	 * allora <tt>dest</tt> viene aggiunto al grafo;
	 **/	
	boolean addEdge(S src, A action, S dest);
	
	/**
	 * Rimuove un arco dal grafo. Rende true se l'operazione ha successo.
	 **/	
	boolean removeEdge(S src, A action, S dest);
	
	/**
	 * Rimuove dal grafo tutti gli archi entranti nello stato 
	 * <tt>d</tt>, restituendo il numero di archi rimossi.
	 *  
	 * @param d Lo stato destinazione degli archi.
	 * @return Il numero di archi rimossi.
	 */
	int removeIncomingEdge(S d);

	
	/**
	 * Rimuove dal grafo tutti gli archi uscenti dallo stato 
	 * <tt>s</tt>, restituendo il numero di archi rimossi.
	 *  
	 * @param s Lo stato destinazione degli archi.
	 * @return Il numero di archi rimossi.
	 */
	int removeOutgoingEdge(S s);

	
	/**
	 * Rimuove dal grafo tutti gli archi entranti ed uscenti dallo
	 * stato <tt>st</tt>, restituendo il numero di archi rimossi.
	 *  
	 * @param st Lo stato dal quale rimuovere tutti gli archi.
	 * @return Il numero di archi rimossi.
	 */
	int removeAllEdge(S st);
	
	GraphInterface<S,A> clone();
}
