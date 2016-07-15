package org.cmg.tapas.core.graph.algorithms.bisimulation;


import java.util.HashSet;
import java.util.LinkedList;


/** 
 * DecoratedClazz � un insieme di stati.
 * Vengono aggiunte le informazioni necessarie all'implementazione
 * degli algoritmi di Kannelakis-Smolka e Paige-Tarjan.
 * <p>
 * @param <S> tipo degli stati
 * 
 * @author Guzman Tierno
 **/
public class DecoratedClazz<S> 
extends HashSet<S> {
	private static final long serialVersionUID = 1L;
	
	/** Eventuale splitter di appartenenza. **/
	private Splitter<S> splitter;
	
	/** Eventuale prima classe sorella. **/
	private DecoratedClazz<S> sibling1;
	
	/** Eventuale seconda classe sorella. **/
	private DecoratedClazz<S> sibling2;
	
	/** 
	 * Costruisce una DecoratedClazz.
	 **/
	public DecoratedClazz() {
        // empty clazz
	}

	/** Rende lo splitter di appartenenza. **/
	public Splitter<S> getSplitter() {
		return splitter;
	}

	/** Setta lo splitter di appartenenza. **/
	public void setSplitter(Splitter<S> splitter) {
		this.splitter = splitter;
	}
	
	/** Rende la prima classe sorella. **/
	public DecoratedClazz<S> getSibling1() {
		return sibling1;
	}

	/** 
	 * Setta la prima classe sorella di questa classe.
	 **/ 
	public void setSibling1(DecoratedClazz<S> sibling1) {
		this.sibling1 = sibling1;
	}

	/** Rende la seconda classe sorella. **/
	public DecoratedClazz<S> getSibling2() {
		return sibling2;
	}

	/** 
	 * Setta la seconda classe sorella di questa classe.
	 **/ 
	public void setSibling2(DecoratedClazz<S> sibling2) {
		this.sibling2 = sibling2;
	}

	/** 
	 * Sgancia questa classe dalle classi sorelle.
	 **/ 
	public void detachFromSiblings() {
		sibling1 = sibling2 = null;
	}

	/** 
	 * Rende una lista contenente gli elementi di questo insieme. 
	 * (Si � preferito aggiungere questo metodo piuttosto che usare 
	 * toArray(E[] a) perch� quest'ultimo usa la reflection).
	 **/
	public LinkedList<S> toList() {
		LinkedList<S> list = new LinkedList<S>();
		for( S state: this )
			list.add(state);
		return list;
	}

}
