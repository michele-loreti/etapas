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
public final class ActlFalse<X,Y> extends ActlFormula<X,Y> {

	@Override
	protected ActlFormula<X, Y> doClone() {
		return this;
	}

	@Override
	protected void doVisit(ActlFormulaVisitor<X, Y> visitor) {
		visitor.visitFalse(this);
	}

	@Override
	protected String _toString() {
		return "false";
	}

	@Override
	public String getUnicode() {
		return "false";
	}

	@Override
	public String operator() {
		return "";
	}

}
