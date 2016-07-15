package org.cmg.tapas.core.graph.algorithms.bisimulation.ksopt;


import java.util.HashSet;
import java.util.LinkedList;

import org.cmg.tapas.core.graph.StateInterface;

/** 
 * KSOClazz � un insieme di stati.
 * Vengono aggiunte le informazioni necessarie ad una implementazione
 * ottimizzata dell'algoritmo di Kannelakis-Smolka.
 * Non soddisfa (volutamente) il contratto generale di Set, 
 * pi� precisamente due insiemi con gli stessi elementi 
 * non hanno necessariamente lo stesso hashCode. Questo 
 * permette l'inserimento di KSOClazz in HashMaps e il suo
 * recupero anche dopo eventuali modifiche.
 * <p>
 * @param <S> tipo degli stati
 * 
 * @author Guzman Tierno
 **/
class KSOClazz<S> 
extends HashSet<S> {
	private static final long serialVersionUID = 1L;

	// used to assign a hash to this KSOClazz object 
	// indipendently of its elements. 
	// This breaks the contract for Set but allows insertion
	// of KSOClazz objects in HashMaps. Otherwise changing the content
	// of a KSOClazz object changes its hashCode and the object gets lost
	// in the HashMap.
	private Object dummyHash = new Object();
	
	@Override 
	public int hashCode() {
		return dummyHash.hashCode();
	}
	
	@Override 
	public boolean equals(Object o) {
		return this == o;
	}
	
	/** Eventuale coda di appartenenza. **/
	private KSOQueue<S> queue;
	
	/** Eventuale prima classe sorella. **/
	private KSOClazz<S> sibling;
	
	/** Flag per le visite. **/
	private boolean flag;
	
	/** 
	 * Costruisce una KSOClazz.
	 **/
	public KSOClazz() {
        // empty clazz
	}

	/** Rende la coda di appartenenza. **/
	public KSOQueue<S> getQueue() {
		return queue;
	}

	/** Setta la coda di appartenenza. **/
	public void setQueue(KSOQueue<S> queue) {
		this.queue = queue;
	}
	
	/** Rende la prima classe sorella. **/
	public KSOClazz<S> getSibling() {
		return sibling;
	}

	/** 
	 * Setta la prima classe sorella di questa classe.
	 **/ 
	public void setSibling(KSOClazz<S> sibling) {
		this.sibling = sibling;
	}

	/** 
	 * Sgancia questa classe dalle classi sorelle.
	 **/ 
	public void detachFromSiblings() {
		sibling = null;
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
	
	/** Rende il flag di questa classe. **/
	public boolean getFlag() {
		return flag;
	}
	
	/** Setta il flag di questa classe. **/
	public void setFlag(boolean flag) {
		this.flag = flag;
	}
	
}
