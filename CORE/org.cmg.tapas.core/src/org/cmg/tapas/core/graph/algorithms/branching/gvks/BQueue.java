package org.cmg.tapas.core.graph.algorithms.branching.gvks;


import java.util.LinkedHashSet;

import org.cmg.tapas.core.graph.ActionInterface;

/** 
 * Coda di BClazz's.
 * <p>
 * @param <S> tipo degli stati
 * @param <A> tipo delle azioni
 * 
 * @author Guzman Tierno
 **/
class BQueue<
	S ,
	A extends ActionInterface
> extends LinkedHashSet<BClazz<S,A>> {
	private static final long serialVersionUID = 1;
	
	@Override
    public boolean add(BClazz<S,A> clazz) {
		clazz.setQueue(this);
		return super.add(clazz);
	}
		
}

