package org.cmg.tapas.core.graph.algorithms.bisimulation.mpt;


import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Vector;


/** 
 * MPTClazz � un insieme di stati.
 * Vengono aggiunte le informazioni necessarie all'implementazione
 * dell'algoritmo di Paige-Tarjan multiplo.
 * <p>
 * @param <S> tipo degli stati
 * 
 * @author Guzman Tierno
 **/
class MPTClazz<S> 
extends HashSet<S> {
	private static final long serialVersionUID = 1L;
	
	// used to assign a hash code to this MPTClazz object 
	// indipendently of its elements. 
	private Object dummyHash = new Object();
	
	/** Numero massimo di classi sorelle **/
	private int maxSiblings;
	
	/** Eventuale splitter di appartenenza. **/
	private MPTSplitter<S> splitter;
	
	/** Mappa sibling->index **/
	private HashMap<Object,Integer> siblingMap;

	/** Mappa hitIndex->sibling **/
	private Vector<MPTClazz<S>> hitIndex;
	
	/** Classi sorelle. **/
	private Vector<MPTClazz<S>> siblings;
	
	/** 
	 * Costruisce una MPTClazz.
	 **/
	public MPTClazz(int maxSiblings) {
		this.maxSiblings = maxSiblings;
		siblings = new Vector<MPTClazz<S>>(maxSiblings);
		hitIndex = new Vector<MPTClazz<S>>(maxSiblings);
		for( int i=0; i<maxSiblings; ++i )
			hitIndex.add(null);
		siblingMap = new HashMap<Object,Integer>(4);
	}

	/** Rende lo splitter di appartenenza. **/
	public MPTSplitter<S> getSplitter() {
		return splitter;
	}
	
	/** Setta lo splitter di appartenenza. **/
	public void setSplitter(MPTSplitter<S> splitter) {
		this.splitter = splitter;
	}
	
	/** Rende le classi sorelle. **/
	public Vector<MPTClazz<S>> getSiblings() {
		return siblings;
	}

	/** 
	 * Aggiusta la posizione del sibling specificato. 
	 * Si assume che i siblings possano solo crescere 
	 * come dimensione e non decrescere.
	 **/
	public void updateSiblingPosition(MPTClazz<S> sibling) {
		updateSiblingPosition( siblingMap.get(sibling.dummyHash) );
	}
	
	/** 
	 * Aggiusta la posizione del sibling specificato. 
	 * Si assume che i siblings possano solo crescere 
	 * come dimensione e non decrescere.
	 **/
	private void updateSiblingPosition(int index) {			
		MPTClazz<S> next = siblings.get(index);
		MPTClazz<S> current = next;
		while( true ) {
			if( index==0 )
				break;
			next = siblings.get(index-1);	
			if( current.size() <= next.size() )
				break;
			siblings.set(index, next);
			siblingMap.put(next.dummyHash, index);
			siblings.set(index-1, current);
			siblingMap.put(current.dummyHash, index-1);
			index--;
		}
	}

	/** 
	 * Aggiunge una classe sorella.
	 **/ 
	public void addSibling(MPTClazz<S> sibling, int hit) {
		siblings.add(sibling);
		hitIndex.set(hit, sibling);
		siblingMap.put(sibling.dummyHash, siblings.size()-1);
	}
	
	/**
	 * Rende la sorella con l'indice di hit specificato.
	 **/
	public MPTClazz<S> getSibling(int hit) {
		return hitIndex.get(hit);
	}

	/** 
	 * Dice quante classi sorelle ha questa classe.
	 **/ 
	public int getSiblingsCount() {
		return siblings.size();
	}

	/** 
	 * Sgancia questa classe dalle classi sorelle.
	 **/ 
	public void detachSiblings() {
		siblings.clear();
		for( int i=0; i<maxSiblings; ++i )
			hitIndex.set(i, null);
		siblingMap.clear();		
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
