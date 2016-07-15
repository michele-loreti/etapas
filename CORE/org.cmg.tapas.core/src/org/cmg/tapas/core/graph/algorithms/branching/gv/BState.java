package org.cmg.tapas.core.graph.algorithms.branching.gv;


import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.cmg.tapas.core.graph.ActionInterface;

/** 
 * Rappresenta uno stato.
 * Vengono aggiunti i campi e i metodi per 
 * una implementazione efficiente dell'algoritmo di verifica
 * della equivalenza branching.
 * <p>
 * @param <S> tipo degli stati
 * @param <A> tipo delle azioni
 * 
 * @author Guzman Tierno
 **/
class BState<
    S ,
    A extends ActionInterface   
> {

	// stato da decorare
    private S state;
    // flag per le visite
    private boolean mark;
    // transizioni inerti che hanno origine in state
    private LinkedHashSet<BState<S,A>> inert;
    // blocco di appartenenza
    private BBlock<S,A> block;
    
    /** Costruisce un BState che decora lo stato specificato. **/
    public BState(S state) {
    	inert = new LinkedHashSet<BState<S,A>>();
    	this.state = state;    	
    }
    
    /** Nome dello stato decorato. **/
    public String getId() {
		return state.toString();
    }
    
    /** 
     * Setta il nome dello stato decorato. 
     * Implementa StateInterface.
     **/
//    public void setId(String name) {
//        state.setId(name);          
//    }
    
    /** Rende il blocco di appartenenza di questo stato. **/
    public BBlock<S,A> getBlock() {
        return block;
    }
    
    /** Setta il blocco di appartenenza di questo stato. **/
    void setBlock(BBlock<S,A> block) {
        this.block = block;
    }
    
    /** Renda la lista delle transizioni inerti di questo stato. **/
    Set<BState<S,A>> getInert() {
        return inert;
    }
    
    /** 
     * Aggiunge una transizione alla lista delle transizioni inerti. 
     **/
    void addInert(BState<S,A> to) {
    	if( !inert.add(to) )
    		return;
    	if( inert.size()==1 )
    		block.moveToNonLast(this);
    }
    
    /** 
     * Rimuove una transizione dalla lista delle transizioni inerti. 
     * Rende <tt>true</tt> se lo stato � diventato ultimo.
     **/
    boolean removeInert(BState<S,A> to) {
    	inert.remove(to);
    	if( inert.isEmpty() )
    		block.moveToLast(this);
    	return inert.isEmpty();
    }
    
    /** 
     * Rimuove tutte le transizioni della lista specificata
     * dlla lista delle transizioni inerti. 
     * Rende <tt>true</tt> se lo stato � diventato ultimo.
     **/
    boolean removeInert(List<BState<S,A>> inertStates) {
    	for( BState<S,A> bstate: inertStates )
    	   	inert.remove( bstate );
    	if( inert.isEmpty() )
    		block.moveToLast(this);
    	return inert.isEmpty();
    }
    
    /** Rende il flag di questo stato. **/
    boolean isMarked() {
        return mark;
    }
    
    /** Setta il flag di questo stato. **/
    void setMarked(boolean mark) {
        this.mark = mark;
    }
    
    /** Rende lo stato decorato da questo BState. **/
    public S getState() {
        return state;
    }
    
    /** Rende una descrizione a stringa di questo BState. **/
    @Override
    public String toString() {
    	return "B" + state.toString();
    }
}

