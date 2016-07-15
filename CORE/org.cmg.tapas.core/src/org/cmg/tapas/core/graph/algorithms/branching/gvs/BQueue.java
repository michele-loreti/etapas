package org.cmg.tapas.core.graph.algorithms.branching.gvs;


import java.util.LinkedHashSet;

import org.cmg.tapas.core.graph.ActionInterface;

/** 
 * Coda di BBlocks.
 * <p>
 * @param <S> tipo degli stati
 * @param <A> tipo delle azioni
 * 
 * @author Guzman Tierno
 **/

class BQueue<
	S ,
	A extends ActionInterface
> extends LinkedHashSet<BBlock<S,A>> {
	private static final long serialVersionUID = 1;
	
	@Override
    public boolean add(BBlock<S,A> block) {
		block.setQueue(this);
		return super.add(block);
	}
		
}

