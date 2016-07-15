/**
 * 
 */
package org.cmg.tapas.formulae.actl.state;


import org.cmg.tapas.formulae.actl.ActlFormula;
import org.cmg.tapas.formulae.actl.ActlFormulaVisitor;


/**
 * @author loreti
 *
 */
public final class 	ActlTrue<X,Y> extends ActlFormula<X,Y> {

	@Override
	protected ActlFormula<X, Y> doClone() {
		return this;
	}

	@Override
	protected void doVisit(ActlFormulaVisitor<X, Y> visitor) {
		visitor.visitTrue(this);
	}

	@Override
	protected String _toString() {
		return "true";
	}

	@Override
	public String getUnicode() {
		return "true";
	}

	@Override
	public String operator() {
		return "";
	}
	
}
