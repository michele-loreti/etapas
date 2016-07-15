/**
 * 
 */
package org.cmg.tapas.formulae.lmc;

import org.cmg.tapas.core.graph.ActionInterface;
import org.cmg.tapas.core.graph.StateInterface;

/**
 * @author loreti
 *
 */
public interface ActionLoader<S, A extends ActionInterface> {

	A getAction(String string, String value);

}
