package org.cmg.tapas.core.graph.algorithms.branching.gv;

import org.cmg.tapas.core.graph.ActionInterface;

/** 
 * Rappresenta una terna (BState, Action, BState).
 * <p>
 * @param <S> tipo degli stati
 * @param <A> tipo delle azioni
 * 
 * @author Guzman Tierno
 **/
class BEdge<
    S ,
    A extends ActionInterface   
> {
	// sorgente
    private BState<S,A> source;
    // azione
    private A action;
    // destinazione 
    private BState<S,A> dest;
    
    /** Costruttore. **/
    BEdge( BState<S,A> source, A action, BState<S,A> dest ) {
    	this.source = source;
    	this.action = action;
    	this.dest = dest;
    }

	/** Rende l'azione di questo arco. **/
    A getAction() {
        return action;
    }
    
	/** Setta l'azione di questo arco. **/
    void setAction(A action) {
        this.action = action;
    }
	
	/** Rende la sorgente di questo arco. **/
    BState<S, A> getSource() {
        return source;
    }
    
	/** Setta la sorgente di questo arco. **/
    void setSource(BState<S, A> source) {
        this.source = source;
    }
    
	/** Rende la destinazione di questo arco. **/
    BState<S, A> getDest() {
        return dest;
    }
    
	/** Setta la destinazione di questo arco. **/
    void setDest(BState<S, A> dest) {
        this.dest = dest;
    }
    
    /** 
     * Rende <tt>true</tt> se sorgente, azione e destinazione
     * sono uguali a quelle del parametro.
     **/
    @Override
    public boolean equals(Object o) {
    	if( o==null || !(o instanceof BEdge) )
    		return false;
    		
    	BEdge other = (BEdge) o;
    	return 
    		source.equals(other.source) &&
    		action.equals(other.action) && 
    		dest.equals(other.dest);
    }
    
    /** toString **/
    @Override
    public String toString() {
    	return 
    		"(" + source + "," + action + "," + dest + ")";
    }
}
