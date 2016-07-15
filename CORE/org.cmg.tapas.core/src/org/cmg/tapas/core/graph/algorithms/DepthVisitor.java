package org.cmg.tapas.core.graph.algorithms;

import org.cmg.tapas.core.graph.ActionInterface;

/** 
 * Visitor di grafi per le ricerche in profondit�. 
 * <p>
 * @param <S> tipo degli stati
 * @param <A> tipo delle azioni
 * 
 * @author Guzman Tierno
 **/
public interface DepthVisitor<
	S ,
	A extends ActionInterface
> {
	/** 
	 * Metodo chiamato quando la visita scende in profondit�. 
	 * Se stiamo entrando in una nuova componente
	 * connessa il metodo viene chiamato con 
	 * i parametri <tt>(-1, null, -1, null, initState, initIndex)</tt>.
	 * Se il metodo rende <tt>false</tt> la visita si arresta.
	 **/
	boolean goingTo(
		int fromDepth, S from, int fromIndex, A action, S to, int toIndex
	);
	
	/** 
	 * Metodo chiamato quando la visita ritorna sui suoi passi. 
	 * Se stiamo uscendo da una componente
	 * connessa il metodo viene chiamato con 
	 * i parametri (-1, null, -1, null, initState, initIndex).
	 * Se il metodo rende <tt>false</tt> la visita si arresta.
	 **/
	boolean comingBack(
		int toDepth, S to, int toIndex, A action, S from, int fromIndex
	);
	
	/** 
	 * Metodo chiamato quando la visita colpisce uno stato gi� visitato.
	 * Se il metodo rende <tt>false</tt> la visita si arresta.
	 **/
	boolean hitMarked(
		int fromDepth, S from, int fromIndex, A action, S hit, int hitIndex
	);
}
	
