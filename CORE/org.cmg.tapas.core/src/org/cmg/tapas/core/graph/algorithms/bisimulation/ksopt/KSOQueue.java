package org.cmg.tapas.core.graph.algorithms.bisimulation.ksopt;


import java.util.Iterator;
import java.util.LinkedHashSet;


/** 
 * ClazzQueue realizza una coda di KSOClazz.
 * Tale coda � usata dall'algoritmo di Kannelakis-Smolka ottimizzato.
 * <p>
 * <p>
 * @param <S> tipo degli stati
 * 
 * @author Guzman Tierno
 **/
class KSOQueue<S> {
	
	/**
	 * La coda � realizzata per mezzo di una LinkedHashSet.
	 ***/
	private LinkedHashSet<KSOClazz<S>> queue = new LinkedHashSet<KSOClazz<S>>();

	/** Inserisce una classe nella coda. **/
	public void put(KSOClazz<S> clazz) {
		queue.add(clazz);
		clazz.setQueue(this);
	}
	
	/** 
	 * Estrae una classe dalla coda.
	 **/
	public KSOClazz<S> get() {
		Iterator<KSOClazz<S>> iter = queue.iterator();
		KSOClazz<S> clazz = null;
		if( iter.hasNext() ) {		
			clazz = iter.next();
			iter.remove();
			clazz.setQueue(null);
		}
		return clazz;
	}
		
	/** Dice se la coda � vuota. **/
	public boolean isEmpty() {		
		return queue.isEmpty(); 
	}
	
	/** Rimuove una classe dalla coda. **/
	public boolean remove(KSOClazz<S> clazz) {
		if(	queue.remove(clazz) ) {
			clazz.setQueue(null);
			return true;
		}
		
		return false;			
	}

	/** Dice se la classe specificata � presente nella coda **/
	public boolean contains(KSOClazz<S> clazz) { 
		return queue.contains(clazz);
	}

}
 
