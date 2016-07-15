package org.cmg.tapas.core.graph.algorithms.branching.gvks;


import java.util.HashSet;
import java.util.LinkedList;

import org.cmg.tapas.core.graph.ActionInterface;

/** 
 * BClazz � un insieme di stati (BState).
 * Vengono aggiunte le informazioni necessarie ad una implementazione
 * ottimizzata dell'algoritmo di 
 * Kannelakis-Smolka-Groote-Vaandrager.
 * Non soddisfa (volutamente) il contratto generale di Set, 
 * pi� precisamente due insiemi con gli stessi elementi 
 * non hanno necessariamente lo stesso hashCode. Questo 
 * permette l'inserimento di BClazz in HashMaps e il suo
 * recupero anche dopo eventuali modifiche.
 * <p>
 * @param <S> tipo degli stati
 * 
 * @author Guzman Tierno
 **/
class BClazz<
    S ,
    A extends ActionInterface   
> extends HashSet<BState<S,A>> {
	private static final long serialVersionUID = 1L;

	// _________________________________________________________________________
	// used to assign a hash to this BClazz object 
	// indipendently of its elements. 
	// This breaks the contract for Set but allows insertion
	// of BClazz objects in HashMaps. Otherwise changing the content
	// of a BClazz object changes its hashCode and the object gets lost
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
	// _________________________________________________________________________
	
	/** Eventuale coda di appartenenza. **/
	private BQueue<S,A> queue;
	
	/** Eventuale prima classe sorella. **/
	private BClazz<S,A> sibling;
	
	/** Flag per le visite. **/
	private boolean flag;
	
	/** Numero di stati ultimi. **/
	private int lastCount;
	
	/** Numero di stati ultimi marcati. **/
	private int lastMarkedCount;
	
	/** Flag che indica se la classe ha bisogno di splitting. **/
	private boolean needsSplitting = true;
	
	/** 
	 * Costruisce una BClazz.
	 **/
	BClazz() {
		//
	}
	
	/** Aggiunge uno stato a questa classe di stati **/
    @Override
	public boolean add(BState<S,A> state) {
		state.setClazz(this);
		boolean res = super.add(state);
		if( res && state.getInert().isEmpty() )
			++lastCount;
		return res;		
	}
	
	/** Rimuove uno stato da questa classe di stati **/
	public boolean remove(BState<S,A> state) {
		state.setClazz(null);
		boolean res = super.remove(state);
		if( res && state.getInert().isEmpty() )
			--lastCount;
		return res;		
	}
	
	/** Decrementa il conteggio degli stati ultimi. **/
	void decreaseLastCount() {
		--lastCount;
	}
	
	/** Incrementa il conteggio degli stati ultimi. **/
	void increaseLastCount() {
		++lastCount;
	}
	
	/** Incrementa il conteggio degli stati ultimi marcati. **/
	void increaseLastMarkedCount() {
		if( ++lastMarkedCount==lastCount )
			needsSplitting = false;	
	}
		
	/** Rende il conteggio degli stati ultimi marcati. **/
	int getLastMarkedCount() {
		return lastMarkedCount;
	}

	/** Resetta il conteggio degli stati ultimi marcati. **/
	void resetLastMarkedCount() {
		lastMarkedCount = 0;
		needsSplitting = true;
	}
		
	/** Rende la coda di appartenenza. **/
	BQueue<S,A> getQueue() {
		return queue;
	}

	/** Setta la coda di appartenenza. **/
	void setQueue(BQueue<S,A> queue) {
		this.queue = queue;
	}

	/** Muove la classe verso la coda specificata. **/
    void moveToQueue(BQueue<S,A> newQueue) {
    	this.queue.remove(this);
    	this.queue = newQueue;
    	queue.add(this);	
    }
	
	/** Rende la prima classe sorella. **/
	BClazz<S,A> getSibling() {
		return sibling;
	}

	/** 
	 * Setta la prima classe sorella di questa classe.
	 **/ 
	void setSibling(BClazz<S,A> sibling) {
		this.sibling = sibling;
	}

	/** 
	 * Sgancia questa classe dalle classi sorelle.
	 **/ 
	void detachFromSiblings() {
		sibling = null;
	}

	/** 
	 * Rende una lista contenente gli elementi di questo insieme. 
	 * (Si � preferito aggiungere questo metodo piuttosto che usare 
	 * toArray(E[] a) perch� quest'ultimo usa la reflection).
	 **/
	LinkedList<BState<S,A>> toList() {
		LinkedList<BState<S,A>> list = new LinkedList<BState<S,A>>();
		for( BState<S,A> state: this )
			list.add(state);		
		return list;
	}
	
	/** Rende il flag di questa classe. **/
	boolean getFlag() {
		return flag;
	}
	
	/** Setta il flag di questa classe. **/
	void setFlag(boolean flag) {
		this.flag = flag;
	}
	
	/** Rende il flag che indica se la classe ha bisogno di splitting. **/
	boolean needsSplitting() {
		return needsSplitting;
	}

}
