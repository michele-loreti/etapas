package org.cmg.tapas.core.graph.algorithms.branching.gvks2;


import java.util.HashSet;
import java.util.LinkedList;

import org.cmg.tapas.core.graph.ActionInterface;

/** 
 * BClazz � un insieme di stati (BState).
 * Vengono aggiunte le informazioni necessarie ad una implementazione
 * ottimizzata dell'algoritmo di 
 * Kannelakis-Smolka-Groote-Vaandrager con memoria.
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
	
	/** Eventuale coda di appartenenza. **/
	private BQueue<S,A> queue;
	
	/** Eventuale prima classe sorella. **/
	private BClazz<S,A> sibling;
	
	/** Flag per le visite. **/
	private boolean flag;

	/** 
	 * Nome unico momentaneo della classe.
	 * Ossia nome unico che la classe avr� fino al prossimo splitting.
	 **/
	private int index;
	
	/** 
	 * Insieme delle classi ripetto alle quali questa classe
	 * � stabile.
	 **/
	private HashSet<Integer> stable;
	
	private static int nextIndex = 0;
	
	/** 
	 * Costruisce una BClazz.
	 **/
	BClazz() {
		this.index = nextIndex++;
		this.stable = new HashSet<Integer>();
	}
	
	/** 
	 * Rende il nome unico momentaneo della classe.
	 * Ossia il nome unico che avr� fino al prossimo splitting.
	 **/
	int getIndex() {
		return index;
	}
	
	/** 
	 * Assegna un nuovo nome unico momentaneo alla classe.
	 **/
	int setNewIndex() {
		return this.index = nextIndex++;
	}
	
	/**
	 * Ruba index da un'altra classe di cui si assume
	 * passi a prendere il posto.
	 **/
	void stealIndex(BClazz<S,A> clazz) {
		index = clazz.index;
	}
	
	/** 
	 * Aggiunge una classe all'insieme delle classi rispetto 
	 * alle quali questa classe � stabile.
	 **/
	void addStable(int splitterIndex) {
		stable.add(splitterIndex);
	}
	
	/** 
	 * Dice se la classe specificata compare nell'insieme
	 * delle classi rispetto alle quali questa classe � stabile.
	 **/
	boolean isStableWrt(int splitterIndex) {
		return stable.contains(splitterIndex);
	}
	
	/**
	 * Rimuove le classi ripetto alle quali questa classe 
	 * � stabile ad esclusione dell'ultima.
	 **/
	void clearStable() {
		stable.clear();
	}	
	
	/** Aggiunge uno stato a questa classe di stati **/
    @Override
	public boolean add(BState<S,A> state) {
		state.setClazz(this);
		return super.add(state);
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
    void moveToQueue(BQueue<S,A> queue) {
    	this.queue.remove(this);
    	this.queue = queue;
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
		sibling.stable.addAll(stable);
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
	
}
