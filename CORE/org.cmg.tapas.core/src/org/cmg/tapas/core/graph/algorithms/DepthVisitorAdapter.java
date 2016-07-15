package org.cmg.tapas.core.graph.algorithms;

import org.cmg.tapas.core.graph.ActionInterface; 

/** 
 * DepthVisitorAdapter d� una implementazione a vuoto 
 * di DepthVisitor.
 * <p>
 * @param <S> tipo degli stati
 * @param <A> tipo delle azioni
 * 
 * @author Guzman Tierno
 **/
public class DepthVisitorAdapter<
	S ,
	A extends ActionInterface
> implements DepthVisitor<S,A> {
	public boolean goingTo(
		int fromDepth, S from, int fromIndex, A action, S to, int toIndex
	) {
		return true;
	}

	public boolean comingBack(
		int toDepth, S to, int toIndex, A action, S from, int fromIndex
	) {
		return true;
	}

	public boolean hitMarked(
		int fromDepth, S from, int fromIndex, A action, S hit, int hitIndex
	) {
		return true;
	}
}
	
