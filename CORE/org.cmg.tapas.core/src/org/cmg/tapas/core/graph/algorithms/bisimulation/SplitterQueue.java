package org.cmg.tapas.core.graph.algorithms.bisimulation;


import java.util.Iterator;
import java.util.LinkedHashSet;

/** 
 * SplitterQueue realizza una coda di Splitter.
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
public class SplitterQueue<S> {
	
	/**
	 * La coda � realizzata per mezzo di due LinkedHashMap:
	 * una per gli splitter composti e una per gli splitter 
	 * semplici.
	 ***/
	private LinkedHashSet<Splitter<S>> simpleQueue = 
		new LinkedHashSet<Splitter<S>>();
	private LinkedHashSet<Splitter<S>> compoundQueue = 
		new LinkedHashSet<Splitter<S>>();

	/** Inserisce uno splitter nella coda. **/
	public void put(Splitter<S> splitter) {
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
	public Splitter<S> get() {
		if( compoundQueue.isEmpty() )
			return getSplitter(simpleQueue);
		
		// compound case		
		Splitter<S> splitter = getSplitter(compoundQueue);
		
		// put back child1 if it is compound
		Splitter<S> child = splitter.getChild1();
		if( child!= null ) { 	
			child.setFather(null);	// pointer from father to child remains
			if( child.hasSons() )
				put( child );
		}
		
		// put back child2 if it is compound
		child = splitter.getChild2();
		if( child!= null ) { 	
			child.setFather(null);	// pointer from father to child remains					
			if( child.hasSons() )
				put( child );
		}
		
		return splitter;
	}
		
	/** 
	 * Estrae uno splitter dalla coda specificata.
	 **/
	private Splitter<S> getSplitter(
		LinkedHashSet<Splitter<S>> queue
	) {
		Iterator<Splitter<S>> iterator = queue.iterator();
		Splitter<S> splitter = null;
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
	public boolean remove(Splitter<S> splitter) {
		if(	simpleQueue.remove(splitter) ||	compoundQueue.remove(splitter) ) {
			splitter.setQueue(null);
			return true;
		}
		
		return false;			
	}

	/** Dice se lo splitter specificato � presente nella coda **/
	public boolean contains(Splitter<S> splitter) { 
		return 
			simpleQueue.contains(splitter) || 
			compoundQueue.contains(splitter);
	}
	
	public LinkedHashSet<Splitter<S>> getAll(){
		LinkedHashSet<Splitter<S>> res = new LinkedHashSet<Splitter<S>>(simpleQueue);		
		res.addAll(compoundQueue);		
		return res;
	}

}
 
