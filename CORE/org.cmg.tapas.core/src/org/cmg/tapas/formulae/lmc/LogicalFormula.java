/**
 * 
 */
package org.cmg.tapas.formulae.lmc;

import org.cmg.tapas.core.graph.ActionInterface;
import org.cmg.tapas.pa.Process;


/**
 * @author loreti
 *
 */
public interface LogicalFormula<S extends Process<S, A>, A extends ActionInterface> {

	String getName();
	void setName( String name );
	String getUnicode();
	boolean satisfies( S s );
	Proof<S,A> getProof( S s );

}
