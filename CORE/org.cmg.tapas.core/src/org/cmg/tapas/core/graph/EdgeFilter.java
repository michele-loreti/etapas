package org.cmg.tapas.core.graph;


/**
 * Filtro per Transizioni.
 *
 * @param <S> tipo degli stati
 * @param <A> tipo delle azioni
 * 
 * @author Guzman Tierno
 */
public abstract class EdgeFilter<
	S,// extends StateInterface,
	A// extends ActionInterface	
> { //implements Filter<TransitionTriple<S,A>> {	
	
//    /** 
//     * Rende <code>true</code> se la transizione specificata
//     * � accettata. Rende <code>false</code> altrimenti.
//     * 
//     * @param edge La transizione da filtrare
//	 * @return Rende <code>true</code> se la transizione specificata
//     * � accettata. Rende <code>false</code> altrimenti.
//	 */
//	public boolean check(TransitionTriple<S,A> edge) {
//		return check(edge.getSrc(), edge.getAction(), edge.getDest());
//	}
	
    /** 
     * Rende <code>true</code> se la transizione specificata
     * � accettata. Rende <code>false</code> altrimenti.
	 * 
     * @param src Sorgente della transizione da filtrare.
	 * @param action Azione della transizione da filtrare.
	 * @param dest Destinazione della transizione da filtrare.
     * @return Rende <code>true</code> se la transizione specificata
     * � accettata. Rende <code>false</code> altrimenti.
	 */
	public abstract boolean check(S src, A action, S dest);
		
}





















