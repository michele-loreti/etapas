package org.cmg.tapas.core.graph.algorithms.bisimulation.mpt;


import java.util.Iterator;
import java.util.LinkedHashSet;


/** 
 * MPTQueue realizza una coda di MPTSplitter.
 * Durante l'estrazione vengono prediletti gli splitter composti.
 * Si assume che uno splitter inserito come semplice 
 * non diventi composto dopo l'inserimento.
 * Si assume anche che uno splitter nella coda non abbia padre.
 * L'implementazione � realizzata in modo che le operazioni
 * sulla coda abbiano costo costante.
 * <p>
 * <p>
 * @param <S> tipo degli stati
 * 
 * @author Guzman Tierno
 **/
class MPTQueue<S> {
	
	/**
	 * La coda � realizzata per mezzo di due LinkedHashMap:
	 * una per gli splitter composti e una per gli splitter 
	 * semplici.
	 ***/
	private LinkedHashSet<MPTSplitter<S>> simpleQueue = 
		new LinkedHashSet<MPTSplitter<S>>();
	private LinkedHashSet<MPTSplitter<S>> compoundQueue = 
		new LinkedHashSet<MPTSplitter<S>>();

	/** Inserisce uno splitter nella coda. **/
	public void put(MPTSplitter<S> splitter) {
		if( !splitter.hasSons() )
			simpleQueue.add(splitter);
		else 
			compoundQueue.add(splitter);
		splitter.setQueue(this);
	}
	
	/** 
	 * Estrae uno splitter dalla coda prediligendo
	 * gli splitter composti.
	 * I figli composti di uno splitter vengono rimessi 
	 * nella coda senza padre.
	 **/
	public MPTSplitter<S> get() {
		if( compoundQueue.isEmpty() )
			return getSplitter(simpleQueue);
		
		// compound case		
		MPTSplitter<S> splitter = getSplitter(compoundQueue);
		
		// put back compound children        
        for( MPTSplitter<S> child : splitter.getChildren() ) {
    		child.setFather(null);	// pointer from father to child remains
    		if( child.hasSons() )
    			put( child );
        }
		
		return splitter;
	}
		
	/** 
	 * Estrae uno splitter dalla coda specificata.
	 **/
	private MPTSplitter<S> getSplitter(
		LinkedHashSet<MPTSplitter<S>> queue
	) {
		Iterator<MPTSplitter<S>> iterator = queue.iterator();
		MPTSplitter<S> splitter = null;
		if( iterator.hasNext() ) {
			splitter = iterator.next();
			iterator.remove();
			splitter.setQueue(null);
		}
		return splitter;
	}	
	
	/** Dice se la coda � vuota. **/
	public boolean isEmpty() {		
		return simpleQueue.isEmpty() && compoundQueue.isEmpty(); 
	}
	
	/** Rimuove uno splitter dalla coda. **/
	public boolean remove(MPTSplitter<S> splitter) {
		if(	simpleQueue.remove(splitter) ||	compoundQueue.remove(splitter) ) {
			splitter.setQueue(null);
			return true;
		}
		
		return false;			
	}

	/** Dice se lo splitter specificato � presente nella coda **/
	public boolean contains(MPTSplitter<S> splitter) { 
		return 
			simpleQueue.contains(splitter) || 
			compoundQueue.contains(splitter);
	}

}
 
