package org.cmg.tapas.core.graph.algorithms.branching.gvs;


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
> extends LinkedHashSet<BState<S,A>> {	
    private static final long serialVersionUID = 1L;
	// OPTION: use of indexes to remember stable splitters
    
    // transizioni entranti nel blocco
    private HashMap<A,HashSet<BEdge<S,A>>> in;	
    // flag per le visite
    private boolean mark;
    // coda di appartenenza del blocco
    private BQueue<S,A> queue;
    // numero di stati ultimi che hanno il flag settato
    private int lastMarked;
    // numero di stati ultimi
    private int lastCount;

	/** Costruisce un blocco vuoto. **/
	BBlock() {
		in = new HashMap<A,HashSet<BEdge<S,A>>>(); 	
	}
	
	// used to assign a hash to this BBlock object 
	// indipendently of its elements. 
	// This breaks the contract for Set but allows insertion
	// of BBlock objects in HashMaps. Otherwise changing the content
	// of a BBlock object changes its hashCode and the object gets lost
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
    
    /** Aggiunge uno stato all'insieme delgi stati ultimi. **/    
    boolean addLast(BState<S,A> state) {
    	boolean res = super.add(state);
    	if( res ) {
    		state.setBlock(this);
    		lastCount++;
    	}
    	return res;    	
    }

    /** Aggiunge uno stato all'insieme delgi stati ultimi. **/
    @Override
    public boolean add(BState<S,A> state) {
    	boolean res;
    	if( state.getInert().isEmpty() )
    		res = addLast(state);
    	else
    		res = addNonLast(state);
    	return res;
    }

    /** Aggiunge uno stato all'insieme delgi stati non ultimi. **/    
    boolean addNonLast(BState<S,A> state) {
    	boolean res = super.add(state);
    	if( res )
    		state.setBlock(this);
    	return res;
    }

	/** 
     * Sposta uno stato dall'insieme degli stati ultimi a quello 
     * degli stati non ultimi.
     **/
    void moveToNonLast(BState<S,A> state) {
    	lastCount--;
    }

    /** 
     * Sposta uno stato dall'insieme degli stati non ultimi a quello 
     * degli stati ultimi.
     **/
    void moveToLast(BState<S,A> state) {
    	lastCount++;
    }

    /** Rende il numero di stati ultimi. **/
    int getLastCount() {
    	return lastCount;
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
	    	if( state.getInert().isEmpty() )
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
    		super.toString() + " In=" + in;
    }
    
}
