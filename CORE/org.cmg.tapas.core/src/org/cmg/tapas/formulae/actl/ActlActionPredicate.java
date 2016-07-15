/**
 * 
 */
package org.cmg.tapas.formulae.actl;

import org.cmg.tapas.core.graph.ActionInterface;
import org.cmg.tapas.core.graph.filter.Filter;


/**
 * @author loreti
 *
 */
public interface ActlActionPredicate<S, A> {
	
	public Filter<A> getFilter();
	
}
