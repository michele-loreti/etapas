package org.cmg.tapas.core.graph.algorithms.branching.gv;


import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.cmg.tapas.core.graph.ActionInterface;


/**
 * Rappresenta un insieme di stati.
 * Vengono aggiunti i campi e i metodi per 
 * una implementazione efficiente dell'algoritmo di verifica
 * della equivalenza branching.
 * <p>
 * @param <S> tipo degli stati
 * @param <A> tipo delle azioni
 * 
 * @author Guzman Tierno
 **/
class BBlock<
    S ,
    A extends ActionInterface   
> {	
	// IDEA: Group: size, contains, iterable

    private static final long serialVersionUID = 1L;
    
    // stati ultimi del blocco
    private LinkedHashSet<BState<S,A>> last;		
    // stati non ultimi (ossia con transizioni inerti) del blocco
    private LinkedHashSet<BState<S,A>> nonLast;	
    // transizioni entranti nel blocco
    private HashMap<A,HashSet<BEdge<S,A>>> in;	
    // flag per le visite
    private boolean mark;
    // coda di appartenenza del blocco
    private BQueue<S,A> queue;
    // numero di stati ultimi che hanno il flag settato
    private int lastMarked;

	/** Costruisce un blocco vuoto. **/
	BBlock() {
		in = new HashMap<A,HashSet<BEdge<S,A>>>(); 	
		last = new LinkedHashSet<BState<S,A>>();
		nonLast = new LinkedHashSet<BState<S,A>>();
	}
	
	/** Dice se questo blocco contiene lo stato specificato. **/
	public boolean contains(BState<S,A> state) {
		return last.contains(state) || nonLast.contains(state);
	}
	
	/** Rende la mappa degli insiemi delle transizioni entranti. **/
    HashMap<A,? extends Set<BEdge<S, A>>> getIn() {
        return in;
    }
    
    /** Aggiunge una transizione all'insieme delle transizioni entranti. **/
    void addIn(BEdge<S,A> t) {
    	HashSet<BEdge<S,A>> aSet = in.get( t.getAction() );
    	if( aSet==null ) {
    		aSet = new HashSet<BEdge<S,A>>();
    		in.put(t.getAction(), aSet);
    	}
    	aSet.add(t);
    }
    
    /** Aggiunge una transizione all'insieme delle transizioni entranti. **/
    void addIn(BState<S,A> src, A action, BState<S,A> dest) {
    	addIn( new BEdge<S,A>(src, action, dest) );
    }
    
    /** Aggiunge una transizione all'insieme delle transizioni entranti. **/
    void addIn(BState<S,A> src, A action, List<BState<S,A>> dests) {
    	for( BState<S,A> dest: dests )
    		addIn( new BEdge<S,A>(src, action, dest) );
    }
    
    /** 
     * Aggiunge tutte le transizioni della lista specificata 
     * all'insieme delle transizioni entranti. 
     **/
    void addIn(LinkedList<BEdge<S,A>> inEdges) {
    	for( BEdge<S,A> edge: inEdges )
    		addIn(edge);
    }
    
    /** Rende l'insieme degli stati ultimi. **/
    Set<BState<S, A>> getLast() {
        return last;
    }
    
    /** Rende il numero di stati ultimi in questo blocco. **/
	int getLastCount() {
		return last.size();	
	}	
	
    /** Rende l'insieme degli stati non ultimi. **/
    public Set<BState<S,A>> getNonLast() {
        return nonLast;    
    }
    
    /** Aggiunge uno stato all'insieme delgi stati ultimi. **/    
    void addLast(BState<S,A> state) {
    	last.add(state);
    	state.setBlock(this);
    }
    
    /** Aggiunge uno stato all'insieme delgi stati non ultimi. **/    
    void addNonLast(BState<S,A> state) {
    	nonLast.add(state);
    	state.setBlock(this);
    }
    
    /** 
     * Sposta uno stato dall'insieme degli stati ultimi a quello 
     * degli stati non ultimi.
     **/
    void moveToNonLast(BState<S,A> state) {
    	last.remove(state);
    	nonLast.add(state);
    }

    /** 
     * Sposta uno stato dall'insieme degli stati non ultimi a quello 
     * degli stati ultimi.
     **/
    void moveToLast(BState<S,A> state) {
    	nonLast.remove(state);
    	last.add(state);
    }

	/** Rende il flag del blocco. **/
    boolean isMarked() {
        return mark;
    }

	/** Setta il flag del blocco. **/
    void setMarked(boolean mark) {
        this.mark = mark;
    }

	/** Setta a <tt>true</tt> il flag dello stato specificato. **/
    void markState(BState<S,A> state) {
    	if( !state.isMarked() ) {
	    	state.setMarked(true);
	    	if( last.contains(state) )
	    		lastMarked++;
	    }
    }

    /** 
     * Pone a 0 il numero di stati ultimi che hanno il flag settato 
   	 * e rende il valore precedente di tale numero. 
     **/
    int resetLastMarkedCount() {
    	int temp = lastMarked;
    	lastMarked = 0;
    	return temp;
    }

	/** Rende la coda di appartenenza del blocco. **/
    BQueue<S, A> getQueue() {
        return queue;
    }

	/** Setta la coda di appartenenza del blocco. **/
    void setQueue(BQueue<S,A> queue) {
        this.queue = queue;
    }      

	/** Rimuove il blocco dalla coda di appartenenza. **/
    void removeFromQueue() {
    	queue.remove(this);
    	queue = null;
    }

	/** Muove il blocco verso la coda specificata. **/
    void moveToQueue(BQueue<S,A> newQueue) {
    	this.queue.remove(this);
    	this.queue = newQueue;
    	queue.add(this);	
    }

    /** to string **/
    @Override
    public String toString() {
    	return 
    		"(Last=" + last +    	
    		" NonLast=" + nonLast +
    		" In=" + in + ")";
    }
    
}
