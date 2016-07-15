package org.cmg.tapas.formulae.hml;

import org.cmg.tapas.core.graph.ActionInterface;
import org.cmg.tapas.core.graph.filter.Filter;

/**
 * @author calzolai
 *
 */
public class TauFilter<Y extends ActionInterface> implements Filter<Y> {
	
	public String toString(){
		return "";
	}

	public boolean check(Y t) {
		return t.isTau();
	}
}
