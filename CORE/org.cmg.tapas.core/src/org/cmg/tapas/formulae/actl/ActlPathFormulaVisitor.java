/**
 * 
 */
package org.cmg.tapas.formulae.actl;


import org.cmg.tapas.core.graph.ActionInterface;
import org.cmg.tapas.formulae.actl.path.ActlNext;
import org.cmg.tapas.formulae.actl.path.ActlUntil;


/**
 * @author loreti
 *
 */
public interface ActlPathFormulaVisitor<S, A> {

	public void visitNextFormula( ActlNext<S, A> phi );
	public void visitUntilFormula( ActlUntil<S, A> phi );
	
}
