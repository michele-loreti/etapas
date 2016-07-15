package org.cmg.tapas.core.graph.algorithms.branching.gvks2;


import java.util.LinkedHashSet;
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
>  {
	
	// stato da decorare
    private S state;
    // flag per le visite
    private boolean mark;
    // transizioni inerti che hanno origine in state
    private Set<BState<S,A>> inert;
    // transizioni inerti che hanno fine in questo stato
    private Set<BState<S,A>> preInert;
    // blocco di appartenenza
    private BClazz<S,A> clazz;
    
    /** Costruisce un BState che decora lo stato specificato. **/
    public BState(S state) {
    	inert = new LinkedHashSet<BState<S,A>>();
    	preInert = new LinkedHashSet<BState<S,A>>();
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
    public BClazz<S,A> getClazz() {
        return clazz;
    }
    
    /** Setta il blocco di appartenenza di questo stato. **/
    void setClazz(BClazz<S,A> clazz) {
        this.clazz = clazz;
    }
    
    /** Renda la lista delle transizioni inerti di questo stato. **/
    Set<BState<S,A>> getInert() {
        return inert;
    }
    
    /** 
     * Aggiunge una transizione alla lista delle transizioni inerti. 
     * Rende <tt>true</tt> se lo stato era ultimo.
     **/
    void addInert(BState<S,A> to) {    	
   		inert.add(to);
    }
 
    /** Renda la lista delle pretransizioni inerti di questo stato. **/
    Set<BState<S,A>> getPreInert() {
        return preInert;
    }

    /** 
     * Aggiunge una pretransizione alla lista delle transizioni inerti. 
     **/
    void addPreInert(BState<S,A> from) {    	
    	preInert.add(from);
    }

    /** 
     * Rimuove una transizione dalla lista delle pretransizioni inerti. 
     **/
    boolean removePreInert(BState<S,A> from) {
    	inert.remove(from);
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

